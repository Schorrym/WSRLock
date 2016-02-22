package de.mariokramer.wsrlock.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import de.mariokramer.wsrlock.model.DocumentResourceLock;

@Transactional
public interface DocumentResourceLockDao extends JpaRepository<DocumentResourceLock, Long>{
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM DocumentResourceLock c WHERE c.docId = :docId")
    boolean existsByDocId(@Param("docId") Long docId);
	void deleteByDocId(Long docId);
}
