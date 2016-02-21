package de.mariokramer.wsrlock.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import de.mariokramer.wsrlock.model.Document;

@Service
public class DocumentFeedServiceImpl implements DocumentFeedService {

	private SimpMessageSendingOperations messaging;
	
	@Autowired
	public DocumentFeedServiceImpl(SimpMessageSendingOperations messaging) {
		this.messaging = messaging;
	}

	@Override
	public void broadcastDocument(Document doc) {
		messaging.convertAndSend("/topic/addDoc", doc);
	}

	@Override
	public void deleteDocument(Long docId) {
		messaging.convertAndSend("/topic/delDoc", docId);
	}
	
	@Override
	public void specificDocumentSubscription(Document doc) {
		if(doc.getDocId() != null){
			messaging.convertAndSend("/topic/doc"+doc.getDocId(), doc);
		}
	}	
}
