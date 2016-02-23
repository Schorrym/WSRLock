package de.mariokramer.wsrlock.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="resourcelock")
public class DocumentResourceLock {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="lockId")
	private Long lockId;
	
	@Column(name="docId")
	private Long docId;
	
	@Column(name="tempDocValue", columnDefinition = "text")
	private String tempDocValue;
		
	@Column(name="userName")
	private String userName;
	@Column(name="sessionId", length=32)
	private String sessionId;
	
	@Column(name="datetime", updatable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetime;

	/*
	 * Constructor with or without document as parameter
	 */
	public DocumentResourceLock() {}
	public DocumentResourceLock(Document doc) {
		this.setDocId(doc.getDocId());
	}
	
	public Long getLockId() {
		return lockId;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
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
