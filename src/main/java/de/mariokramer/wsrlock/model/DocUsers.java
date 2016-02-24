package de.mariokramer.wsrlock.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Target;

@Entity
@Table
public class DocUsers {

	@Id
	@GeneratedValue
	@Column(name="duId")
	private Long duId;	
	
	@ManyToOne
	@Target(value=Users.class)
	private Users user;
	
	@ManyToOne
	@Target(value=Document.class)
	private Document doc;

	public DocUsers() {}
	public DocUsers(Document doc, Users user) {
		this.user = user;
		this.doc = doc;
	}
	
	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
}
