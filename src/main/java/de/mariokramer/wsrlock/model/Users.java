package de.mariokramer.wsrlock.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class Users {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="userId")
	private Long userId;
	
	@Column(name="userName", unique=true, nullable=false)
	private String userName;
	
	@Column(name="userPass", nullable=false)
	private String userPass;
	
	@Column(name="userHash")
	private String userHash;
	
	@Column(name="jSession", columnDefinition = "text")
	private String jSession;
	
	@Column(name="enabled")
	private int enabled;
	
	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	public String getjSession() {
		return jSession;
	}
	public void setjSession(String jSession) {
		this.jSession = jSession;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public Users() {}
	public Users(Users users){
		this.userName = users.userName;
		this.userPass = users.userPass;
		this.userHash = users.userHash;
		this.enabled = users.enabled;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserHash() {
		return userHash;
	}
	public void setUserHash(String userHash) {
		this.userHash = userHash;
	}
}
