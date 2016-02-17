package de.mariokramer.wsrlock.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.mariokramer.wsrlock.model.Document;

public interface DocumentDao extends CrudRepository<Document, Long>{
	List<Document> findByDocName(String docName);
}
