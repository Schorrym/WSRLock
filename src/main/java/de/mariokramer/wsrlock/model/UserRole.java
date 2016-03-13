package de.mariokramer.wsrlock.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Target;

@Entity
@Table(name="userRoles")
public class UserRole {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
@Column(name="userRoleId")
private Long userRoleId;

@ManyToOne
@Target(value=Users.class)
private Long userId;

@Column(name="role")
private String role;

public Long getUserRoleId() {
	return userRoleId;
}

public void setUserRoleId(Long userRoleId) {
	this.userRoleId = userRoleId;
}

public Long getUserId() {
	return userId;
}

public void setUserId(Long userId) {
	this.userId = userId;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
} 


}
