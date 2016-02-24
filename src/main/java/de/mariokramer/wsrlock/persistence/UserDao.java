package de.mariokramer.wsrlock.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.mariokramer.wsrlock.model.Users;

public interface UserDao extends JpaRepository<Users, Long>{
	Users getUsersByUserName(String userName);
	
	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Users c WHERE c.userName = :userName")
    boolean existsByUserName(@Param("userName") String userName);
	Users findOneByUserName(String userName);
}
