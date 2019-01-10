package com.kite9.k9server.domain.revision;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kite9.k9server.adl.format.formattable.AbstractFormattable;
import com.kite9.k9server.adl.format.formattable.Formattable;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.AbstractLongIdEntity;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.user.User;


/**
 * Contains a single diagram revision.  
 */
@Entity
public class Revision extends AbstractLongIdEntity implements Formattable {

	@ManyToOne(targetEntity=Document.class, optional=false, cascade=CascadeType.ALL)
    Document document;
    
	@Column(columnDefinition="TEXT")
	String inputXml;

	@Column(length=40)
    String diagramHash;
    
    Date dateCreated = new Date();
    
    @ManyToOne(targetEntity=User.class, optional=false, fetch=FetchType.LAZY)
    User author;
    
	@Column(columnDefinition="TEXT")
    String outputXml;
    
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

	public String getOutputXml() {
		return outputXml;
	}

	public void setOutputXml(String renderedXml) {
		this.outputXml = renderedXml;
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
	
	private transient ADL adl;
	
	private transient boolean requiresSave = false;

	@Override
	@JsonIgnore
	public ADL getInput() {
		if (adl == null) {
			adl = new ADLImpl(inputXml, null);
		}
		
		return adl;
	}

	@Override
	@JsonIgnore
	public String getSVG() {
		if (outputXml == null) {
			outputXml = AbstractFormattable.render(getInput());
			requiresSave = true;
		}
		
		return outputXml;
	}

	@Override
	@JsonIgnore
	public boolean requiresSave() {
		return requiresSave;
	}

	
}
