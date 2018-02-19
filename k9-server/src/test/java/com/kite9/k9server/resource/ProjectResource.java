package com.kite9.k9server.resource;

import org.springframework.hateoas.ResourceSupport;

public class ProjectResource extends ResourceSupport {

	public String title;
	public String description;
	public String stub;
	public String secret;
	
	public ProjectResource(String title, String description, String stub, String secret) {
		super();
		this.title = title;
		this.description = description;
		this.stub = stub;
		this.secret = secret;
	}

	public ProjectResource() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((secret == null) ? 0 : secret.hashCode());
		result = prime * result + ((stub == null) ? 0 : stub.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		ProjectResource other = (ProjectResource) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (secret == null) {
			if (other.secret != null)
				return false;
		} else if (!secret.equals(other.secret))
			return false;
		if (stub == null) {
			if (other.stub != null)
				return false;
		} else if (!stub.equals(other.stub))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	
	
}
