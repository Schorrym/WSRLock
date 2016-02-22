package de.mariokramer.wsrlock.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="document")
public class Document {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="docId")
	private Long docId;	
	@Column(name="docName", nullable=false)
	private String docName;
	@Column(name="docValue", columnDefinition = "text")
	private String docValue;
	
	public Document(String docName) {
		this.setDocName(docName);
	}
	
	public Document() {}

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
