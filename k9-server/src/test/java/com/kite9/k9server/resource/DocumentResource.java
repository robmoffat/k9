package com.kite9.k9server.resource;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.hateoas.ResourceSupport;


public class DocumentResource extends ResourceSupport {

    public String title;
    public String description;
    public String project;
	public String currentRevision = null;
    
    public Date dateCreated = new Date();
    public Date lastUpdated;
    
    public DocumentResource() {
    }
	
    public DocumentResource(String title, String description, String project) {
		super();
		this.title = title;
		this.description = description;
		this.project = project;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((currentRevision == null) ? 0 : currentRevision.hashCode());
		result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		DocumentResource other = (DocumentResource) obj;
		if (currentRevision == null) {
			if (other.currentRevision != null)
				return false;
		} else if (!currentRevision.equals(other.currentRevision))
			return false;
		if (dateCreated == null) {
			if (other.dateCreated != null)
				return false;
		} else if (!dateCreated.equals(other.dateCreated))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!lastUpdated.equals(other.lastUpdated))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

    
    
}
