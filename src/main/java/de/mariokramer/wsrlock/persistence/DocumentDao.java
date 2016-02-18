package de.mariokramer.wsrlock.persistence;

import org.springframework.data.repository.CrudRepository;

import de.mariokramer.wsrlock.model.Document;

public interface DocumentDao extends CrudRepository<Document, Long>{
	Document findByDocName(String docName);
}
