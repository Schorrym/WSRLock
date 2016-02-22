package de.mariokramer.wsrlock.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.DocumentResourceLock;

public interface DocumentResourceLockDao extends JpaRepository<DocumentResourceLock, Long>{
	Long findLockIdByLockingDoc(Document doc);
}
