package de.mariokramer.wsrlock.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.mariokramer.wsrlock.model.UserRole;

public interface UserRoleDao extends JpaRepository<UserRole, Long>{
    @Query("select a.role from UserRole a, Users b where b.userName=?1 and a.userId=b.userId")
    public List<String> findRoleByUserName(String username);
}
