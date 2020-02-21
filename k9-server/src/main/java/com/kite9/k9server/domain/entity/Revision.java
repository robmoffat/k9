package com.kite9.k9server.domain.entity;

import java.util.Date;
import java.util.Objects;

/**
 * Contains revision information for a particular version of a diagram
 * 
 * @author robmoffat
 *
 */
public class Revision {
	
	private String sha1;
	private Date date;
	private String username;
	
	public Revision() {
		super();
	}

	public Revision(String sha1, Date date, String username) {
		super();
		this.sha1 = sha1;
		this.date = date;
		this.username = username;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sha1);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Revision other = (Revision) obj;
		return Objects.equals(sha1, other.sha1);
	}

	@Override
	public String toString() {
		return "Revision [sha1=" + sha1 + ", date=" + date + ", username=" + username + "]";
	}

	public String getSha1() {
		return sha1;
	}

	public Date getDate() {
		return date;
	}

	public String getUsername() {
		return username;
	}

}
