package com.kite9.k9server.domain.revision;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kite9.k9server.domain.AbstractLongIdEntity;
import com.kite9.k9server.domain.Secured;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.user.User;


/**
 * Contains a single diagram revision.  
 */
@Entity
public class Revision extends AbstractLongIdEntity implements Secured {

	@ManyToOne(targetEntity=Document.class, optional=false, fetch=FetchType.EAGER)
    Document document;
    
	@Column(columnDefinition="TEXT")
	String xml;
    
    Date dateCreated = new Date();
    
    @ManyToOne(targetEntity=User.class, optional=false, fetch=FetchType.LAZY)
    User author;
    
    @ManyToOne(targetEntity=Revision.class, optional=true, fetch=FetchType.LAZY)
    Revision previousRevision;
    
    @ManyToOne(targetEntity=Revision.class, optional=true, fetch=FetchType.LAZY)
    Revision nextRevision;
    
	public Revision() {
		super();
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String inputXml) {
		this.xml = inputXml;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Revision getPreviousRevision() {
		return previousRevision;
	}

	public void setPreviousRevision(Revision previousRevision) {
		this.previousRevision = previousRevision;
	}

	public Revision getNextRevision() {
		return nextRevision;
	}

	public void setNextRevision(Revision nextRevision) {
		this.nextRevision = nextRevision;
	}

	@Override
	public boolean checkAccess(Action a) {
		if (document == null) {
			return false;
		}
		
		return document.checkAccess(a);
	}

	@JsonIgnore
	@Override
	public String getTitle() {
		return author.getUsername();
	}
	
	@JsonIgnore
	@Override
	public String getDescription() {
		return "on "+document.getTitle();
	}

	@JsonIgnore
	@Override
	public String getLocalImagePath() {
		return "/public/admin/icons/revision.svg";
	}

	@JsonIgnore
	@Override
	public Date getLastUpdated() {
		return getDateCreated();
	}
	
	
}
