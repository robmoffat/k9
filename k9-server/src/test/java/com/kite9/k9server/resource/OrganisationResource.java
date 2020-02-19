package com.kite9.k9server.resource;

import java.util.Objects;

import com.kite9.k9server.domain.entity.Organisation;

public class OrganisationResource extends Organisation {
	
	private String title;
	private String description;
	private String icon;

	public OrganisationResource() {
		super();
	}

	public OrganisationResource(String title, String description, String icon) {
		super();
		this.title = title;
		this.description = description;
		this.icon = icon;
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(description, icon, title);
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
		OrganisationResource other = (OrganisationResource) obj;
		return Objects.equals(description, other.description) && Objects.equals(icon, other.icon)
				&& Objects.equals(title, other.title);
	}

}
