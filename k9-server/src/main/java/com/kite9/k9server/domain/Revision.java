package com.kite9.k9server.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;


/**
 * Contains a single diagram revision.  
 */
@Entity
public class Revision extends AbstractLongIdEntity {

	@ManyToOne(targetEntity=Revision.class, optional=false, fetch=FetchType.LAZY)
    Document document;
    
	String inputXml;

	@Column(length=32)
    String diagramHash;
    
    Date dateCreated = new Date();
    
    @ManyToOne(targetEntity=User.class, optional=false, fetch=FetchType.LAZY)
    User author;
    
    String renderedXml;
    
    @ManyToOne(targetEntity=Revision.class, optional=true, fetch=FetchType.LAZY)
    Revision previousRevision;
    
    @ManyToOne(targetEntity=Revision.class, optional=true, fetch=FetchType.LAZY)
    Revision nextRevision;

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getInputXml() {
		return inputXml;
	}

	public void setInputXml(String inputXml) {
		this.inputXml = inputXml;
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

	public String getRenderedXml() {
		return renderedXml;
	}

	public void setRenderedXml(String renderedXml) {
		this.renderedXml = renderedXml;
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
