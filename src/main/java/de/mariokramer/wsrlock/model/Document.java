package de.mariokramer.wsrlock.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Document {

	@Id
	@GeneratedValue
	private Long docId;
	private String docName;
	private String docValue;
	
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public String getDocValue() {
		return docValue;
	}
	public void setDocValue(String docValue) {
		this.docValue = docValue;
	}
	public Long getDocId() {
		return docId;
	}
}
