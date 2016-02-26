package de.mariokramer.wsrlock.persistence;

import java.util.List;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.DocumentResourceLock;
import de.mariokramer.wsrlock.model.Message;
import de.mariokramer.wsrlock.model.Users;

public interface DocumentFeedService {
	//Broadcasts on start page
	public void broadcastDocument(Document doc);
	public void deleteDocument(Long id);
	
	//Broadcasts on read page
	public void broadcastUsers(Long docId, Message<List<Users>> users);
	public void broadcastUsersToLockUser(String userName, Message<List<Users>> users);
	public void timeOverMessage(String userName, Message<Document> doc);
	public void lockDockument(Long docId, Message<DocumentResourceLock> msg);
	public void saveDocument(Long docId, Message<Document> newDoc);
}
