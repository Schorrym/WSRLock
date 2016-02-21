package de.mariokramer.wsrlock.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.Message;
import de.mariokramer.wsrlock.persistence.DocumentDao;
import de.mariokramer.wsrlock.persistence.DocumentFeedService;
import net.minidev.json.JSONObject;

/**
 * This Controller is for handling messages between the STOMP WebSocket client and the Stomp Messagebroker (ActiveMQ)
 * @author Mario Kramer
 *
 */
@Controller
public class WebSocketController {

	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	private DocumentDao docDao;
	
	@Autowired
	private DocumentFeedService docService;
	
	@MessageMapping("/editDoc")
	public void editDoc(Document doc, Principal principal){
		doc = docDao.findOne(doc.getDocId());
		synchronized (doc) {
			
		}
	}
	
	@MessageMapping("/delDoc")
	public void deleteDocument(Document doc){
		if(docDao.findOne(doc.getDocId()) != null) {
			docDao.delete(doc);
			docService.deleteDocument(doc.getDocId());
			log.info("Document with ID-"+doc.getDocId()+" was deleted");
		}else{
			log.error("Document with ID-"+doc.getDocId()+" was not found in Database");
		}
	}
	
	@MessageMapping("/addDoc")
	public void handleSubscription(Document doc) {
		doc = docDao.save(doc);
		log.info("New Document added to database through WebSockets");
		docService.broadcastDocument(doc);
	}
}
