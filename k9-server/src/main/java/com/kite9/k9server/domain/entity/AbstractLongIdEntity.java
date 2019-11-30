package com.kite9.k9server.domain.entity;

import java.util.Arrays;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.kite9.k9server.command.domain.WithCommands;

/**
 * Since all K9 entities are keyed by a long id, this is abstracted to here.
 * 
 * @author robmoffat
 *
 */
@MappedSuperclass
public abstract class AbstractLongIdEntity implements RestEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JacksonXmlProperty(isAttribute = true)
	protected Long id;

	public void setId(Long id) {
		this.id = id;
	} 

	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractLongIdEntity other = (AbstractLongIdEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String getType() {
		String className = this.getClass().getCanonicalName();
		className = className.substring(className.lastIndexOf(".") + 1).toLowerCase();
		return className;
	}

	@Override
	public String getCommands() {
		WithCommands wc = this.getClass().getAnnotation(WithCommands.class);
		if (wc == null) {
			return null;
		} else {
			return Arrays.stream(wc.value())
				.map(c -> c.getName().substring(c.getPackage().getName().length()+1))	
				.reduce((a,b) -> a+" "+b).orElse("");
		}
	}

	public String getLocalId() {
		return "/api/"+getType()+"s/"+getId();
	}
}
