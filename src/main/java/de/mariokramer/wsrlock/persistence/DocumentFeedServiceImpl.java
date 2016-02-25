package de.mariokramer.wsrlock.persistence;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.DocumentResourceLock;
import de.mariokramer.wsrlock.model.Message;
import de.mariokramer.wsrlock.model.Users;

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
	public void broadcastUsersToLockUser(String userName, Message<List<Users>> users) {
		messaging.convertAndSendToUser(userName, "/queue/editMode", users);		
	}
	
	@Override
	public void broadcastUsers(Long docId, Message<List<Users>> users) {
		messaging.convertAndSend("/topic/doc"+docId, users);
	}

	@Override
	public void lockDockument(Long docId, Message<DocumentResourceLock> msg) {
		messaging.convertAndSend("/topic/doc"+docId, msg);
	}

	@Override
	public void saveDocument(Long docId, Message<Document> newDoc) {
		messaging.convertAndSend("/topic/doc"+docId, newDoc);		
	}
}