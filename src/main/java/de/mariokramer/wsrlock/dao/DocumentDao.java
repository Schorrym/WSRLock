package de.mariokramer.wsrlock.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.mariokramer.wsrlock.model.Document;

@Repository
public interface DocumentDao {

	public List<Document> getAllDocuments();
}
