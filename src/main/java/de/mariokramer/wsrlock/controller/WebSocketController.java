package de.mariokramer.wsrlock.controller;

import java.security.Principal;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.OptimisticLockException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import de.mariokramer.wsrlock.config.security.MD5;
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
	
	private boolean check = false;

	private boolean checkChallenge(String base64, String userName){
		Users user = null;
		try {
			user = userDao.getUsersByUserName(userName);
		} catch (Exception e) {
			log.error("User: "+user.getUserName()+" was not found");
			e.getStackTrace();
		}
		
		String md5 = StringUtils.newStringUtf8(Base64.decodeBase64(base64));
		String challenge = null;
		String hash = null;
		try{
			challenge = user.getUserHash().concat(user.getjSession());
			hash = MD5.getMD5(challenge);			
		}catch (Exception e){
			log.error("No User found "+e.getStackTrace());
		}
		if(md5.equals(hash) && userName != null){
			generateChallenge(user);
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	private String generateChallenge(Users user){
		String randomNum = String.valueOf( 0 + (int)(Math.random() * Integer.MAX_VALUE) );		
		String date = Long.toString(new Date().getTime());
		String challenge = randomNum + date;
		String hash = null;
		try {
			user.setUserHash(challenge);
			userDao.save(user);
		} catch (Exception e) {
			log.error("Problem with finding user: " + user.getUserName() + " " + e.getStackTrace());
			e.printStackTrace();
		}
		String base64 = StringUtils.newStringUtf8(Base64.encodeBase64(challenge.getBytes()));
		docWebSocketService.sendChallengeToUser(user.getUserName(), new Message(base64));
		return hash;
	}
	
	//Only allow one user at a time to check if the resource is free or already in use
	private String checkResourceLock(String userName, Long docId){
		Users user = userDao.getUsersByUserName(userName);
		Document doc = docDao.findOne(docId);
		DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
		List<DocumentResourceLock> drls = resLockDao.findAll();
		
		if(drls != null){			
			if(resLockDao.existsByDocUsers(du)){
				return "writeMode";
			}
			//If current document was locked by any other user
			for(DocumentResourceLock drl : drls){
				Long id = drl.getDocUsers().getDoc().getDocId();
				if(id == doc.getDocId()){
					return "lockView";
				}
			}
		}		
		return null;
	}

	@MessageMapping("/tokenCreate")
	public void createToken(Principal p, @Header(value="userObj") String userObj){
		Users user = userDao.getUsersByUserName(p.getName());
		if(user != null){
			user.setjSession(userObj);
			generateChallenge(user);
		}			
	}
	
	@MessageMapping("/checkDoc")
	@SendTo("/topic/checkDoc")
	public Message<DocumentResourceLock> checkDoc(Document doc, Principal p, 
			@Header(value="challenge") String challenge){
		
		if(checkChallenge(challenge, p.getName()) || check){
			Users user = null;
			user = userDao.getUsersByUserName(p.getName());
			
			//Check whether document-users combination already exists or not
			doc = docDao.findOne(doc.getDocId());		
			DocUsers du = null;
			if(!docUserDao.existsByUser(user, doc) && user != null){
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
			return messageDrl;
		}else{
			log.error("Smth went wrong, maybe no permission for user: "+p.getName());
		}
		return null;		
	}
	
	@MessageMapping("/broadcastUser")
	public void broadcastUser(Document doc, Principal p, 
			@Header(value="challenge") String challenge){
		
		if(checkChallenge(challenge, p.getName()) || check){
			doc = docDao.findOne(doc.getDocId());		
			List<DocUsers> dus = docUserDao.findAllByDoc(doc);		
			LinkedList<String> users = new LinkedList<String>();
			
			for(DocUsers du : dus){
				Users userTemp = userDao.findOne(du.getUser().getUserId());
				users.add(userTemp.getUserName());
			}
			for(DocUsers du : dus){
				if(resLockDao.findOneByDocUsers(du) != null){
					String userName = du.getUser().getUserName();
					docWebSocketService.broadcastUsersToLockUser(userName, new Message<List<String>>(users, "userUpdate"));
				}
			}		
			docWebSocketService.broadcastUsers(doc.getDocId(), new Message<List<String>>(users, "userUpdate"));
		}else{
			log.error("Smth went wrong, maybe no permission for user: "+p.getName());
		}		
	}
	
	@MessageMapping("/autoSave")
	public void autoSave(Document doc, Principal p, 
			@Header(value="challenge") String challenge){
		
		if(checkChallenge(challenge, p.getName()) || check){
			String tempValue = doc.getDocValue();
			doc = docDao.findOne(doc.getDocId());
			
			Users user = userDao.getUsersByUserName(p.getName());
			DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
			DocumentResourceLock drl = resLockDao.findOneByDocUsers(du);
			
			if(!drl.getTempDocValue().equals(tempValue)){
				drl.setTempDocValue(tempValue);
				drl.setTimer(1);
				resLockDao.save(drl);
			}else{
				int timer = drl.getTimer();
				++timer;
				drl.setTimer(timer);
				resLockDao.save(drl);
				if(timer >= 5){
					Message<Document> msg = new Message<Document>(doc, "timeOver");
					docWebSocketService.timeOverMessage(p.getName(), msg);
					docWebSocketService.saveDocument(doc.getDocId(), msg);
					resLockDao.delete(drl);
				}			
			}
		}else{
			log.error("Smth went wrong, maybe no permission for user: "+p.getName());
		}				
	}
	
	@SuppressWarnings("rawtypes")
	@MessageMapping("/leaveDoc")
	public void leaveDoc(Message msg, Principal p, 
			@Header(value="challenge") String challenge){
		
		if(checkChallenge(challenge, p.getName()) || check){
			Document doc = docDao.findOne(Long.valueOf(msg.getTask()));
			Users user = userDao.getUsersByUserName(p.getName());
			DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
			if(resLockDao.findOneByDocUsers(du) == null){
				docUserDao.delete(du);
			}
		}else{
			log.error("Smth went wrong, maybe no permission for user: "+p.getName());
		}		
	}
	
	@MessageMapping("/saveDoc")
	@SendToUser("/queue/editMode")
	public Message<Document> saveDoc(Document doc, Principal p,
			@Header(value="challenge") String challenge){
		
		if(checkChallenge(challenge, p.getName()) || check){
			Long docVersion = doc.getDocVersion();
			Document newDoc = docDao.findOne(doc.getDocId());
			Users user = userDao.getUsersByUserName(p.getName());
			newDoc.setDocValue(doc.getDocValue());
			try{
				DocUsers du = new DocUsers(newDoc, user);
				if(du != null && du.getDoc().getDocVersion() == docVersion){
					docDao.save(newDoc);
				}			
			}catch(OptimisticLockException e){
				log.error("Could not save new Document due to double saving Error: "+e.getMessage());
			}
			
			docWebSocketService.saveDocument(newDoc.getDocId(), new Message<Document>(newDoc, "newDoc"));
			resLockDao.deleteByDocUsers(docUserDao.findOneByUserAndDoc(user, newDoc));
			
			return new Message<Document>(newDoc, "docSaved");
		}else{
			log.error("Smth went wrong, maybe no permission for user: "+p.getName());
		}
		return null;
		
	}
	
	@MessageMapping("/editDoc")
	@SendToUser("/queue/editMode")
	public Message<DocumentResourceLock> editDoc(Document doc, Principal p,
			@Header(value="challenge") String challenge){
		if(checkChallenge(challenge, p.getName()) || check){
			doc = docDao.findOne(doc.getDocId());		
			Users user = userDao.getUsersByUserName(p.getName());		
			DocUsers du = docUserDao.findOneByUserAndDoc(user, doc);
			DocumentResourceLock drl = resLockDao.findOneByDocUsers(du);
			Message<DocumentResourceLock> messageDrl = new Message<>();
			String task = checkResourceLock(p.getName(), doc.getDocId());
			
			if(task == null){
				//If no locking entry is found for that document, editing the document can be allowed
				synchronized (this) {
					drl = new DocumentResourceLock();
					drl.setTimer(1);
					drl.setTempDocValue(doc.getDocValue());
					drl.setDocUsers(du);
					messageDrl.setTask("lockDoc");
					messageDrl.setObject(drl);
					resLockDao.save(drl);
				}
				docWebSocketService.lockDockument(doc.getDocId(), messageDrl);			
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
			messageDrl.getObject().getDocUsers().getUser().setUserPass("");
			return messageDrl;
		}else{
			log.error("Smth went wrong, maybe no permission for user: "+p.getName());
		}
		return null;		
	}
	
	@MessageMapping("/delDoc")
	public void deleteDocument(Document doc, Principal p, 
			@Header(value="challenge") String challenge){
		if(checkChallenge(challenge, p.getName()) || check){
			if(docDao.findOne(doc.getDocId()) != null) {
				docDao.delete(doc.getDocId());
				docWebSocketService.deleteDocument(doc.getDocId());
				log.info("Document with ID-"+doc.getDocId()+" was deleted");
			}else{
				log.error("Document with ID-"+doc.getDocId()+" was not found in Database");
			}
		}else{
			log.error("Smth went wrong, maybe no permission for user: "+p.getName());
		}
	}
	
	@MessageMapping("/addDoc")
	public void addDocument(Document doc, Principal p, 
			@Header(value="challenge") String challenge) {		

		if(checkChallenge(challenge, p.getName()) || check){
			doc = docDao.save(doc);
			if(doc.getDocValue().length() > 64){
				doc.setDocValue(doc.getDocValue().substring(0,32) + "...");
			}
			log.info("New Document added to database through WebSockets");
			docWebSocketService.broadcastDocument(doc);
		}else{
			log.error("Smth went wrong, maybe no permission for user: "+p.getName());
		}
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
