package com.kite9.k9server.domain.project;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.springframework.security.core.context.SecurityContextHolder;

import com.kite9.k9server.domain.AbstractLongIdEntity;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.permission.Member;
import com.kite9.k9server.domain.permission.ProjectRole;

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
	
	@OneToMany(mappedBy="project", targetEntity=Document.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<Document> documents;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL }, mappedBy = "project")
    private List<Member> members = new ArrayList<>();

	public Project() {
	}
    
    public Project(String title, String description, String stub) {
		super();
		this.title = title;
		this.description = description;
		this.stub = stub;
	}
	
	public static String createRandomString() {
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
	
	public List<Document> getDocuments() {
		return documents;
	}
	
	public List<Member> getMembers() {
		return members;
	}

	public boolean checkAccess(boolean write) {
		String principal = SecurityContextHolder.getContext().getAuthentication().getName();
		for (Member m : getMembers()) {
			if (m.getUser().getUsername().equals(principal)) {
				if (write) {
					return (m.getProjectRole() != ProjectRole.VIEWER);  
				} else {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
