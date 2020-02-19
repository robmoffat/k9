package com.kite9.k9server.resource;

import java.util.Date;
import java.util.Objects;

import com.kite9.k9server.domain.entity.Document;
import com.kite9.k9server.domain.entity.RestEntity;


public class DocumentResource extends Document {

   private String title;
   private String description;
   private String icon;
   private Date lastUpdated;
   private String type;
   private RestEntity<?> parent;
    
    public DocumentResource() {
    }

	public DocumentResource(String title, String description, String icon, Date lastUpdated, String type,
			RestEntity<?> parent) {
		super();
		this.title = title;
		this.description = description;
		this.icon = icon;
		this.lastUpdated = lastUpdated;
		this.type = type;
		this.parent = parent;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	@Override
	public Date getLastUpdated() {
		return lastUpdated;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public RestEntity<?> getParent() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(description, icon, lastUpdated, parent, title, type);
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
		DocumentResource other = (DocumentResource) obj;
		return Objects.equals(description, other.description) && Objects.equals(icon, other.icon)
				&& Objects.equals(lastUpdated, other.lastUpdated) && Objects.equals(parent, other.parent)
				&& Objects.equals(title, other.title) && Objects.equals(type, other.type);
	}

    
}
