package de.mariokramer.wsrlock.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="resourcelock")
public class DocumentResourceLock {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="lockId")
	private Long lockId;
	
	@ManyToOne
	private Document lockingDoc;
	
	@Column(name="tempDocValue", columnDefinition = "text")
	private String tempDocValue;
		
	@Column(name="userName")
	private String userName;
	@Column(name="sessionId", length=32)
	private String sessionId;
	
	@Column(name="datetime", insertable=false, updatable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date datetime;

	/*
	 * Constructor with or without document as parameter
	 */
	public DocumentResourceLock() {}
	public DocumentResourceLock(Document doc) {
		this.setLockingDoc(doc);
	}
	
	public Long getLockId() {
		return lockId;
	}

	public Document getLockingDoc() {
		return lockingDoc;
	}

	public void setLockingDoc(Document lockingDoc) {
		this.lockingDoc = lockingDoc;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getDatetime() {
		return datetime;
	}
	
	public String getTempDocValue() {
		return tempDocValue;
	}
	
	public void setTempDocValue(String tempDoc) {
		this.tempDocValue = tempDoc;
	}
}
