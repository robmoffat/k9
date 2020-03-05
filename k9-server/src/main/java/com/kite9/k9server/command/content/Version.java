package com.kite9.k9server.command.content;

import java.util.Objects;

/**
 * Will extend this later with extra details and attributes.
 * 
 * @author robmoffat
 *
 */
public class Version {

	String versionId;

	@Override
	public int hashCode() {
		return Objects.hash(versionId);
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
		Version other = (Version) obj;
		return Objects.equals(versionId, other.versionId);
	}

	public String getVersionId() {
		return versionId;
	}

	public Version(String versionId) {
		super();
		this.versionId = versionId;
	}

	public Version() {
		super();
	}

}
