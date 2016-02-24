package de.mariokramer.wsrlock.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.mariokramer.wsrlock.model.DocUsers;
import de.mariokramer.wsrlock.model.Document;
import de.mariokramer.wsrlock.model.Users;

public interface DocUsersDao extends JpaRepository<DocUsers, Long>{
	List<DocUsers> findAllByUser(Users user);
	DocUsers findOneByUserAndDoc(Users user, Document doc);
	List<DocUsers> findAllByDoc(Document doc);
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM DocUsers c WHERE c.user = :user AND c.doc = :doc")
    boolean existsByUser(@Param("user") Users user, @Param("doc") Document doc);
}
