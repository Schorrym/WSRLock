package de.mariokramer.wsrlock.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.DocumentResourceLock;
import de.mariokramer.wsrlock.model.Message;
import de.mariokramer.wsrlock.persistence.DocumentDao;
import de.mariokramer.wsrlock.persistence.DocumentFeedService;
import de.mariokramer.wsrlock.persistence.DocumentResourceLockDao;

/**
 * This Controller is for handling messages between the STOMP WebSocket client and the Stomp Messagebroker (ActiveMQ)
 * @author Mario Kramer
 *
 */
@Controller
public class WebSocketController {

	private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
	
	@Autowired
	private DocumentDao docDao;
	
	@Autowired
	private DocumentFeedService docWebSocketService;
	
	@Autowired
	private DocumentResourceLockDao resLockDao;
	
	@MessageMapping("/checkDoc")
	@SendTo("/topic/checkDoc")
	public Message checkDoc(Message msg){
		Long docId = msg.getDocId();
		if(resLockDao.existsByDocId(docId)){
			msg.setTask("lockView");	
		}		
		return msg;
	}
	
	@MessageMapping("/saveDoc")
	@SendToUser("/queue/saveSuccess")
	public Document saveDoc(Document doc){
		Document newDoc = docDao.findOne(doc.getDocId());
		newDoc.setDocValue(doc.getDocValue());
		docDao.save(newDoc);
		docWebSocketService.lockDockument(newDoc.getDocId(), newDoc);
		resLockDao.deleteByDocId(newDoc.getDocId());
		
		return newDoc;
	}
		
	@MessageMapping("/editDoc")
	@SendToUser("/queue/lockSuccess")
	public DocumentResourceLock editDoc(Document doc, Principal principal, SimpMessageHeaderAccessor header){
		doc = docDao.findOne(doc.getDocId());
		if(resLockDao.existsByDocId(doc.getDocId())){
			log.error("Requested source is already in locking table. Check the resource lock table for that document and delete it");
		}else{
			DocumentResourceLock docLock = null;
			log.info("Document: " + doc.getDocName() + " is now locked for user: " + principal.getName());
			synchronized (doc) {				
				docLock = new DocumentResourceLock(doc);
				docLock.setSessionId(header.getSessionId());
				docLock.setUserName(principal.getName());
				docLock.setTempDocValue(doc.getDocValue());
				docWebSocketService.lockDockument(doc.getDocId(), new Document());
			}
			resLockDao.save(docLock);
			return docLock;
		}
		return null;
	}
	
	@MessageMapping("/delDoc")
	public void deleteDocument(Document doc){
		if(docDao.findOne(doc.getDocId()) != null) {
			docDao.delete(doc);
			docWebSocketService.deleteDocument(doc.getDocId());
			log.info("Document with ID-"+doc.getDocId()+" was deleted");
		}else{
			log.error("Document with ID-"+doc.getDocId()+" was not found in Database");
		}
	}
	
	@MessageMapping("/addDoc")
	public void addDocument(Document doc) {
		doc = docDao.save(doc);
		if(doc.getDocValue().length() > 64){
			doc.setDocValue(doc.getDocValue().substring(0,32) + "...");
		}
		log.info("New Document added to database through WebSockets");
		docWebSocketService.broadcastDocument(doc);
	}
}
