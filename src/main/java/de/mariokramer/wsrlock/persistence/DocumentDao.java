package de.mariokramer.wsrlock.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mariokramer.wsrlock.model.Document;

public interface DocumentDao extends JpaRepository<Document, Long>{
	Document findByDocName(String docName);
}
