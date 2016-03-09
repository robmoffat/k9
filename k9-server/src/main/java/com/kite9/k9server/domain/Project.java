package com.kite9.k9server.domain;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class Project extends AbstractLongIdEntity {
	
	@Column(length=50,nullable=false)
	private String title;
	
	@Column(length=200,nullable=true)
	private String description;
	
	@Column(length=50,nullable=false, unique=true)
	private String stub;
		
	@Column(length=32,nullable=false)
	private String secret = createRandomString();
	
	@OneToMany(targetEntity=Document.class, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<Document> documents;
	
	public Project() {
	}
    
    public Project(String title, String description, String stub) {
		super();
		this.title = title;
		this.description = description;
		this.stub = stub;
	}
	
	static String createRandomString() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	@Override
	public String toString() {
		return "Project [title=" + title + ", description=" + description + ", stub=" + stub + ", secret=" + secret + ", id=" + id + "]";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStub() {
		return stub;
	}

	public void setStub(String stub) {
		this.stub = stub;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
}
