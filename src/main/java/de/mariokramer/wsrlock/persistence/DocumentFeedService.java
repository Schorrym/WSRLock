package de.mariokramer.wsrlock.persistence;

import java.util.List;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.DocumentResourceLock;
import de.mariokramer.wsrlock.model.Message;

public interface DocumentFeedService {
	//Send all over the Application
	public void sendChallengeToUser(String userName, @SuppressWarnings("rawtypes") Message msg);
	
	//Broadcasts on start page
	public void broadcastDocument(Document doc);
	public void deleteDocument(Long id);
	
	//Broadcasts on read page
	public void broadcastUsers(Long docId, Message<List<String>> users);
	public void broadcastUsersToLockUser(String userName, Message<List<String>> users);
	public void timeOverMessage(String userName, Message<Document> doc);
	public void lockDockument(Long docId, Message<DocumentResourceLock> msg);
	public void saveDocument(Long docId, Message<Document> newDoc);	
}
