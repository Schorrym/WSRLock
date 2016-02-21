package de.mariokramer.wsrlock.persistence;

import de.mariokramer.wsrlock.model.Document;

public interface DocumentFeedService {
	public void broadcastDocument(Document doc);
	public void deleteDocument(Long id);
	public void specificDocumentSubscription(Document doc);
	public void lockDockument(Document doc);
}
