package com.kite9.k9server.resource;

import java.util.Objects;

import com.kite9.k9server.domain.entity.Repository;

public class RepositoryResource extends Repository {
	
	private String title;
	private String description;

	public RepositoryResource() {
		super();
	}

	public RepositoryResource(String title, String description) {
		super();
		this.title = title;
		this.description = description;
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(description, title);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RepositoryResource other = (RepositoryResource) obj;
		return Objects.equals(description, other.description) && Objects.equals(title, other.title);
	}

}
