package de.mariokramer.wsrlock.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import de.mariokramer.wsrlock.model.DocUsers;
import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.DocumentResourceLock;
import de.mariokramer.wsrlock.model.Message;
import de.mariokramer.wsrlock.model.Users;
import de.mariokramer.wsrlock.persistence.DocUsersDao;
import de.mariokramer.wsrlock.persistence.DocumentDao;
import de.mariokramer.wsrlock.persistence.DocumentFeedService;
import de.mariokramer.wsrlock.persistence.DocumentResourceLockDao;
import de.mariokramer.wsrlock.persistence.UserDao;

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
	@Autowired
	private UserDao userDao;
	@Autowired
	private DocUsersDao docUserDao;
	
	@MessageMapping("/checkDoc")
	@SendTo("/topic/checkDoc")
	public GenericMessage checkDoc(Message msg, Principal p, StompHeaderAccessor s){
		//Check whether user already exists or not
		Users user = null;
		if(userDao.existsByUserName(p.getName())){
			user = userDao.findOneByUserName(p.getName());
			user.setSessionId(s.getSessionId());
		}else{
			user = new Users();
			user.setUserName(p.getName());
			user.setSessionId(s.getSessionId());
		}
		userDao.save(user);
		
		//Check whether document-users combination already exists or not
		Long docId = msg.getDocId();
		Document doc = docDao.findOne(docId);
		DocUsers du = null;
		if(!docUserDao.existsByUser(user, doc)){
			du = new DocUsers(docDao.findOne(docId), user);
			docUserDao.save(du);
		}else{
			du = docUserDao.findOneByUserAndDoc(user, doc);
		}
		
		//Check if document is already locked by a user
		DocumentResourceLock rl = resLockDao.findOneByDocUsers(du);
		if(rl != null){
			msg.setTask("lockView");
			if(rl.getDocUsers().getUser().getUserId() == user.getUserId()){
				msg.setTask("writeMode");
			}
			msg.setUser(user.getUserName());
		}		
		s.setNativeHeader("bitch", "value");
		Map headers = new HashMap<>();
		headers.put("furz", "kot");
		return new GenericMessage("foo", headers);
	}
	
	@MessageMapping("/saveDoc")
	@SendToUser("/queue/saveSuccess")
	public void saveDoc(Document doc, Principal p){		
		Document newDoc = docDao.findOne(doc.getDocId());
		Users user = userDao.getUsersByUserName(p.getName());
		newDoc.setDocValue(doc.getDocValue());
		docDao.save(newDoc);
		docWebSocketService.saveDocument(newDoc.getDocId(), newDoc, "newDoc");
		resLockDao.deleteByDocUsers(docUserDao.findOneByUserAndDoc(user, newDoc));
	}
		
	@MessageMapping("/editDoc")
	@SendToUser("/queue/lockSuccess")
	public DocumentResourceLock editDoc(Document doc, Principal p, SimpMessageHeaderAccessor header){
		Message msg = new Message();
		doc = docDao.findOne(doc.getDocId());
		Users user = userDao.findOneByUserName(p.getName());
		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
		
		if(resLockDao.existsByDocUsers(du)){
			log.error("Requested source is already in locking table. Check the resource lock table for that document and delete it");
		}else{
			DocumentResourceLock docLock = null;
			log.info("Document: " + doc.getDocName() + " is now locked for user: " + p.getName());
			msg.setDocId(doc.getDocId().toString());
			msg.setTask("lockView");
			msg.setUser(p.getName());
			synchronized (doc) {				
				docLock = new DocumentResourceLock(du);
				docLock.setTempDocValue(doc.getDocValue());
				docWebSocketService.lockDockument(doc.getDocId(), msg, "lockView");
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
