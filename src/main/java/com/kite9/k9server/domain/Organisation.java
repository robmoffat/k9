package com.kite9.k9server.domain;

import java.util.Date;

public abstract class Organisation extends RestEntity<Organisation> {

	@Override
	public Date getLastUpdated() {
		return null;
	}
	
	@Override
	public RestEntity<?> getParent() {
		return null;
	}

	@Override
	public String getType() {
		return "organisation";
	}

	@Override
	public String getCommands() {
		return "focus";
	}
}
