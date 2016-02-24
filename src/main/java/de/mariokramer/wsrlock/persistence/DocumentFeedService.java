package de.mariokramer.wsrlock.persistence;

import java.util.List;

import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.Message;
import de.mariokramer.wsrlock.model.Users;

public interface DocumentFeedService {
	//Broadcasts on start page
	public void broadcastDocument(Document doc);
	public void deleteDocument(Long id);
	
	//Broadcasts on read page
	public void broadcastUsers(Long docId, List<Users> users, String task);
	public void lockDockument(Long docId, Message msg, String task);
	public void saveDocument(Long docId, Document newDoc, String task);
}
