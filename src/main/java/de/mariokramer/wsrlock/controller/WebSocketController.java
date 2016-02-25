package de.mariokramer.wsrlock.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

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
@Component
public class WebSocketController{

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
	public Message<DocumentResourceLock> checkDoc(Document doc, Principal p, StompHeaderAccessor s){
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
		doc = docDao.findOne(doc.getDocId());
		DocUsers du = null;
		if(!docUserDao.existsByUser(user, doc)){
			du = new DocUsers(docDao.findOne(doc.getDocId()), user);
			docUserDao.save(du);
		}else{
			du = docUserDao.findOneByUserAndDoc(user, doc);
		}
		
		//Check if document is already locked by a user
		Message<DocumentResourceLock> drl = new Message<DocumentResourceLock>();
		DocumentResourceLock rl = resLockDao.findOneByDocUsers(du);
		if(rl != null){
			drl.setTask("lockView");
			if(rl.getDocUsers().getUser().getUserName().equals(p.getName())){
				drl.setTask("writeMode");
			}
			drl.setObject(rl);
		}
		return drl;
	}
	
	@MessageMapping("/broadcastUser")
	public void broadcastUser(Document doc, Principal p){
		doc = docDao.findOne(doc.getDocId());
		List<DocUsers> dus = docUserDao.findAllByDoc(doc);
		LinkedList<Users> users = new LinkedList<Users>();
		for(DocUsers du : dus){
			users.add(userDao.findOne(du.getUser().getUserId()));
		}
		for(DocUsers du : dus){
			if(resLockDao.findOneByDocUsers(du) != null){
				String userName = du.getUser().getUserName();
				docWebSocketService.broadcastUsersToLockUser(userName, new Message<List<Users>>(users, "userUpdate"));
			}
		}		
		docWebSocketService.broadcastUsers(doc.getDocId(), new Message<List<Users>>(users, "userUpdate"));
	}
	
	@MessageMapping("/autoSave")
	public void autoSave(Document doc, Principal p){
		String tempValue = doc.getDocValue();
		doc = docDao.findOne(doc.getDocId());
		Users user = userDao.findOneByUserName(p.getName());
		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
		DocumentResourceLock rl = resLockDao.findOneByDocUsers(du);
		rl.setTempDocValue(tempValue);
		resLockDao.save(rl);
	}
	
	@MessageMapping("/leaveDoc")
	public void leaveDoc(Message<List<Users>> msg, Principal p){
		Document doc = docDao.findOne(Long.valueOf(msg.getTask()));
		Users user = userDao.findOneByUserName(p.getName());
		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
		docUserDao.delete(du);
//		msg.setTask("userUpdate");
//		docWebSocketService.broadcastUsers(doc.getDocId(), msg);
	}
	
	@MessageMapping("/saveDoc")
	@SendToUser("/queue/editMode")
	public Message<Document> saveDoc(Document doc, Principal p){		
		Document newDoc = docDao.findOne(doc.getDocId());
		Users user = userDao.getUsersByUserName(p.getName());
		newDoc.setDocValue(doc.getDocValue());
		docDao.save(newDoc);
		docWebSocketService.saveDocument(newDoc.getDocId(), new Message<Document>(newDoc, "newDoc"));
		resLockDao.deleteByDocUsers(docUserDao.findOneByUserAndDoc(user, newDoc));
		
		return new Message<Document>(newDoc, "docSaved");
	}
		
	@MessageMapping("/editDoc")
	@SendToUser("/queue/editMode")
	public Message<DocumentResourceLock> editDoc(Document doc, Principal p, SimpMessageHeaderAccessor header){
		doc = docDao.findOne(doc.getDocId());
		Users user = userDao.findOneByUserName(p.getName());
		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
		DocumentResourceLock drl = resLockDao.findOneByDocUsers(du);
		Message<DocumentResourceLock> messageDrl = new Message<>();
		
		//If current document and current user can be found in locking table
		if(resLockDao.existsByDocUsers(du)){
			messageDrl.setTask("writeMode");
			messageDrl.setObject(drl);
			return messageDrl;
		}
		
		//If current document was locked by any other user
		for(DocumentResourceLock drls : resLockDao.findAll()){
			Long id = drls.getDocUsers().getDoc().getDocId();
			if(id == doc.getDocId()){
				log.error("This document is already locked, and can only be unlocked by the user who locked it");
				messageDrl.setTask("lockView");
				messageDrl.setObject(drl);
				return messageDrl;
			}
		}			

		//If no locking entry is found, editing the document can be allowed
		synchronized (doc) {
			drl.setTempDocValue(doc.getDocValue());
			messageDrl.setTask("lockDoc");
			messageDrl.setObject(drl);
			docWebSocketService.lockDockument(doc.getDocId(), messageDrl);
		}
		resLockDao.save(drl);
		return messageDrl;
//		doc.setObject(docDao.findOne(Long.valueOf(doc.getTask())));
//		Users user = userDao.findOneByUserName(p.getName());
//		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc.getObject());
//		DocumentResourceLock docLock = null;
//		if(resLockDao.existsByDocUsers(du)){
//			log.error("Requested source is already in locking table. Check the resource lock table for that document and delete it");
//		}else{			
//			log.info("Document: " + doc.getObject().getDocName() + " is now locked for user: " + p.getName());
//			doc.setTask("lockView");
//			synchronized (doc) {				
//				docLock = new DocumentResourceLock(du);
//				docLock.setTempDocValue(doc.getObject().getDocValue());
//				docWebSocketService.lockDockument(doc.getObject().getDocId(), doc);
//			}
//			resLockDao.save(docLock);
//		}
//		Message<DocumentResourceLock> msg = new Message<DocumentResourceLock>(docLock, "lockDoc");
//		return msg;
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

	@EventListener
	public void startupHandler(BrokerAvailabilityEvent event){
		//Delete all Document-User realtion at the startup of the Application
		for(DocUsers du : docUserDao.findAll()){
			if(resLockDao.findOneByDocUsers(du) == null ){
				docUserDao.delete(du.getDuId());
			}
		}
	}
}
