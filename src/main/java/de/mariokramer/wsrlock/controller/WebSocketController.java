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
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
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
	

	@SubscribeMapping("/tokenCheck")
	public Message<Document> createToken(Principal p, SimpMessageHeaderAccessor s){
		String userName = p.getName();
		String sessionId = s.getSessionId();
		String hashCode = String.valueOf((userName+sessionId).hashCode());
		
		Users user = null;
		if(userDao.existsByUserName(userName)){
			user = userDao.findOneByUserName(userName);
			user.setSessionId(String.valueOf(hashCode));
		}else{
			user = new Users();
			user.setUserName(userName);
			user.setSessionId(String.valueOf(hashCode));
		}
		userDao.save(user);
		Message<Document> msg = new Message<Document>();
		msg.setHash(hashCode);
		
		return msg;
	}
	
	@MessageMapping("/checkDoc")
	@SendTo("/topic/checkDoc")
	public Message<DocumentResourceLock> checkDoc(Document doc, Principal p, SimpMessageHeaderAccessor s){
		//Check whether user already exists or not
		Users user = null;
//		if(userDao.existsByUserName(p.getName())){
			user = userDao.findOneByUserName(p.getName());
//			user.setSessionId(s.getSessionId());
//		}else{
//			user = new Users();
//			user.setUserName(p.getName());
//			user.setSessionId(s.getSessionId());
//		}
//		userDao.save(user);
		
		//Check whether document-users combination already exists or not
		doc = docDao.findOne(doc.getDocId());		
		DocUsers du = null;
		if(!docUserDao.existsByUser(user, doc)){
			du = new DocUsers(docDao.findOne(doc.getDocId()), user);
			docUserDao.save(du);
		}else{
			du = docUserDao.findOneByUserAndDoc(user, doc);
		}
		
		String task = checkResourceLock(p.getName(), doc.getDocId());
		
		//Check if document is already locked by a user
		Message<DocumentResourceLock> messageDrl = new Message<DocumentResourceLock>();
		DocumentResourceLock drl = resLockDao.findOneByDocUsers(du);
		if(task == null){
			messageDrl.setTask("alright");
			messageDrl.setObject(drl);
		}else if(task.equals("writeMode")){
			messageDrl.setTask(task);
			messageDrl.setObject(drl);
		}else if(task.equals("lockView")){
			messageDrl.setTask("lockView");
			messageDrl.setObject(drl);
			log.error("This document is already locked, and can only be unlocked by the user who locked it");
		}
		
//		DocumentResourceLock drl = resLockDao.findOneByDocUsers(du);
//		if(drl != null){
//			//If current document and current user can be found in locking table
//			if(resLockDao.existsByDocUsers(du)){
//				messageDrl.setTask("writeMode");
//				messageDrl.setObject(drl);
//				return messageDrl;
//			}
//			
//			//If current document was locked by any other user
//			for(DocumentResourceLock drls : resLockDao.findAll()){
//				Long id = drls.getDocUsers().getDoc().getDocId();
//				if(id == doc.getDocId()){
//					log.error("This document is already locked, and can only be unlocked by the user who locked it");
//					messageDrl.setTask("lockView");
//					messageDrl.setObject(drl);
//					return messageDrl;
//				}
//			}
//		}
		return messageDrl;
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
		DocumentResourceLock drl = resLockDao.findOneByDocUsers(du);
		
		if(!drl.getTempDocValue().equals(tempValue)){
			drl.setTempDocValue(tempValue);
			drl.setTimer(1);
			
		}else{
			int timer = drl.getTimer();
			++timer;
			drl.setTimer(timer);
			resLockDao.save(drl);
			if(timer >= 5){
				Message<Document> msg = new Message<Document>(doc, "timeOver");
				docWebSocketService.timeOverMessage(p.getName(), msg);
				resLockDao.delete(drl);
			}			
		}		
	}
	
	@MessageMapping("/leaveDoc")
	public void leaveDoc(Message<List<Users>> msg, Principal p){
		Document doc = docDao.findOne(Long.valueOf(msg.getTask()));
		Users user = userDao.findOneByUserName(p.getName());
		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
		if(resLockDao.findOneByDocUsers(du) == null){
			docUserDao.delete(du);
		}			
//		msg.setTask("userUpdate");
//		docWebSocketService.broadcastUsers(doc.getDocId(), msg);
	}
	
	@MessageMapping("/saveDoc")
	@SendToUser("/queue/editMode")
	public Message<Document> saveDoc(Document doc, Principal p){		
		Document newDoc = docDao.findOne(doc.getDocId());
		Users user = userDao.getUsersByUserName(p.getName());
		newDoc.setDocValue(doc.getDocValue());
		synchronized (newDoc) {
			docDao.save(newDoc);	
		}
		docWebSocketService.saveDocument(newDoc.getDocId(), new Message<Document>(newDoc, "newDoc"));
		resLockDao.deleteByDocUsers(docUserDao.findOneByUserAndDoc(user, newDoc));
		
		return new Message<Document>(newDoc, "docSaved");
	}
	
	public String checkResourceLock(String userName, Long docId){
		Users user = userDao.findOneByUserName(userName);
		Document doc = docDao.findOne(docId);
		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
		List<DocumentResourceLock> drls = resLockDao.findAll();
		
		if(drls != null){
			
			if(resLockDao.existsByDocUsers(du)){
//				messageDrl.setTask("writeMode");
//				messageDrl.setObject(drl);
				return "writeMode";
			}
			//If current document was locked by any other user
			for(DocumentResourceLock drl : drls){
				Long id = drl.getDocUsers().getDoc().getDocId();
				if(id == doc.getDocId()){
					
//					messageDrl.setTask("lockView");
//					messageDrl.setObject(drl);
					return "lockView";
				}
			}
		}		
		return null;
	}
	
	@MessageMapping("/editDoc")
	@SendToUser("/queue/editMode")
	public Message<DocumentResourceLock> editDoc(Document doc, Principal p, SimpMessageHeaderAccessor header){
		doc = docDao.findOne(doc.getDocId());		
		Users user = userDao.findOneByUserName(p.getName());		
		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
		DocumentResourceLock drl = resLockDao.findOneByDocUsers(du);
		Message<DocumentResourceLock> messageDrl = new Message<>();
		
		String task = checkResourceLock(p.getName(), doc.getDocId());
		
		if(task == null){
			//If no locking entry is found for that document, editing the document can be allowed
			drl = new DocumentResourceLock();
			drl.setTimer(1);
			synchronized (doc) {
				drl.setTempDocValue(doc.getDocValue());
				drl.setDocUsers(du);
				messageDrl.setTask("lockDoc");
				messageDrl.setObject(drl);
				docWebSocketService.lockDockument(doc.getDocId(), messageDrl);
			}
			resLockDao.save(drl);
		}else if(task.equals("writeMode")){
			//If current document AND current user can be found in locking table
			messageDrl.setTask(task);
			messageDrl.setObject(drl);
		}else if(task.equals("lockView")){
			//If current document was locked by any other user
			messageDrl.setTask(task);
			messageDrl.setObject(drl);
			log.error("This document is already locked, and can only be unlocked by the user who locked it");
		}
		
//		if(drl != null){
//			//If current document and current user can be found in locking table
//			if(resLockDao.existsByDocUsers(du)){
//				messageDrl.setTask("writeMode");
//				messageDrl.setObject(drl);
//				return messageDrl;
//			}
//			//If current document was locked by any other user
//			for(DocumentResourceLock drls : resLockDao.findAll()){
//				Long id = drls.getDocUsers().getDoc().getDocId();
//				if(id == doc.getDocId()){
//					log.error("This document is already locked, and can only be unlocked by the user who locked it");
//					messageDrl.setTask("lockView");
//					messageDrl.setObject(drl);
//					return messageDrl;
//				}
//			}
//		}else{
//			//If no locking entry is found, editing the document can be allowed
//			drl = new DocumentResourceLock();
//			drl.setTimer(1);
//			synchronized (doc) {
//				drl.setTempDocValue(doc.getDocValue());
//				drl.setDocUsers(du);
//				messageDrl.setTask("lockDoc");
//				messageDrl.setObject(drl);
//				docWebSocketService.lockDockument(doc.getDocId(), messageDrl);
//			}
//			resLockDao.save(drl);
//		}
		return messageDrl;
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
