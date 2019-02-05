package com.kite9.k9server.domain.revision;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.kite9.k9server.domain.AbstractLongIdEntity;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.user.User;


/**
 * Contains a single diagram revision.  
 */
@Entity
public class Revision extends AbstractLongIdEntity {

	@ManyToOne(targetEntity=Document.class, optional=false, cascade=CascadeType.ALL)
    Document document;
    
	@Column(columnDefinition="TEXT")
	String xml;

	@Column(length=40)
    String diagramHash;
    
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

	public String getDiagramHash() {
		return diagramHash;
	}

	public void setDiagramHash(String diagramHash) {
		this.diagramHash = diagramHash;
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
}
