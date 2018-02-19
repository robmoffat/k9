package com.kite9.k9server.resource;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

public class RevisionResource extends ResourceSupport {

	public String document;
	public String inputXml;
	public String diagramHash;
	public Date dateCreated = new Date();
	public String author;
	public String renderedXml;
	public String previousRevision;
	public String nextRevision;
    
	public RevisionResource() {
		super();
	}

	public RevisionResource(String document, String inputXml, String diagramHash, Date dateCreated, String author, String renderedXml, String previousRevision, String nextRevision) {
		super();
		this.document = document;
		this.inputXml = inputXml;
		this.diagramHash = diagramHash;
		this.dateCreated = dateCreated;
		this.author = author;
		this.renderedXml = renderedXml;
		this.previousRevision = previousRevision;
		this.nextRevision = nextRevision;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result + ((diagramHash == null) ? 0 : diagramHash.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((inputXml == null) ? 0 : inputXml.hashCode());
		result = prime * result + ((nextRevision == null) ? 0 : nextRevision.hashCode());
		result = prime * result + ((previousRevision == null) ? 0 : previousRevision.hashCode());
		result = prime * result + ((renderedXml == null) ? 0 : renderedXml.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		RevisionResource other = (RevisionResource) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (dateCreated == null) {
			if (other.dateCreated != null)
				return false;
		} else if (!dateCreated.equals(other.dateCreated))
			return false;
		if (diagramHash == null) {
			if (other.diagramHash != null)
				return false;
		} else if (!diagramHash.equals(other.diagramHash))
			return false;
		if (inputXml == null) {
			if (other.inputXml != null)
				return false;
		} else if (!inputXml.equals(other.inputXml))
			return false;
		if (nextRevision == null) {
			if (other.nextRevision != null)
				return false;
		} else if (!nextRevision.equals(other.nextRevision))
			return false;
		if (previousRevision == null) {
			if (other.previousRevision != null)
				return false;
		} else if (!previousRevision.equals(other.previousRevision))
			return false;
		if (renderedXml == null) {
			if (other.renderedXml != null)
				return false;
		} else if (!renderedXml.equals(other.renderedXml))
			return false;
		return true;
	}
	
	
    
}
