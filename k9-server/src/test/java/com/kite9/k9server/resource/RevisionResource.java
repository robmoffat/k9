package com.kite9.k9server.resource;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

public class RevisionResource extends ResourceSupport {

	public String document;
	public String xml;
	public Date dateCreated = new Date();
	public String author;
	public String previousRevision;
	public String nextRevision;
    
	public RevisionResource() {
		super();
	}

	public RevisionResource(String document, Date dateCreated, String author, String xml, String previousRevision, String nextRevision) {
		super();
		this.document = document;
		this.xml = xml;
		this.dateCreated = dateCreated;
		this.author = author;
		this.previousRevision = previousRevision;
		this.nextRevision = nextRevision;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((nextRevision == null) ? 0 : nextRevision.hashCode());
		result = prime * result + ((previousRevision == null) ? 0 : previousRevision.hashCode());
		result = prime * result + ((xml == null) ? 0 : xml.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
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
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
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
		if (xml == null) {
			if (other.xml != null)
				return false;
		} else if (!xml.equals(other.xml))
			return false;
		return true;
	}

	
    
}
