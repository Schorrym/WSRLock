package de.mariokramer.wsrlock.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name="resourcelock")
public class DocumentResourceLock {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="lockId")
	private Long lockId;
	
	@JoinColumn(name="docUsers")
	@ManyToOne
	private DocUsers docUsers;
	
	@Column(name="tempDocValue", columnDefinition = "text")
	private String tempDocValue;
	
	@Column(name="timer")
	private int timer;
	
	@Column(name="dateCreated", updatable=false, insertable=false)
	private Date dateCreated;
	
	@Column(name="lastModified", updatable=false, insertable=false)
	private Date lastModified;

	@PreUpdate
	@PrePersist
	public void updateTimeStamps(){
		lastModified = new Date();
		if(dateCreated==null){
			dateCreated = new Date();
		}
	}
	
	/*
	 * Constructor with or without document as parameter
	 */
	public DocumentResourceLock() {}
	public DocumentResourceLock(DocUsers docUsers) {
		this.setDocUsers(docUsers);	
	}
	
	public DocUsers getDocUsers() {
		return docUsers;
	}

	public void setDocUsers(DocUsers docUsers) {
		this.docUsers = docUsers;
	}

	public Long getLockId() {
		return lockId;
	}


	public Date getDateCreated() {
		return dateCreated;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public String getTempDocValue() {
		return tempDocValue;
	}
	
	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void setTempDocValue(String tempDoc) {
		this.tempDocValue = tempDoc;
	}
}
