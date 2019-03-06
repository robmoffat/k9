package com.kite9.k9server.domain.permission;

/**
 * Enumerates over the different roles a user could have on a project.
 */
public enum ProjectRole {
	
	ADMIN("A", "glyphicon-list-alt", true, true, true), 
	PUBLISHER("P", "publisher", true, true, false), 
	MEMBER("M", "glyphicon-pencil", true, false, false), 
	VIEWER("V", "glyphicon-eye-open", false, false, false);

    String id;
	String icon;
	boolean canEditDiagrams;
	boolean canPublish;
	boolean canChangeUsers;

    public boolean canEditDiagrams() {
		return canEditDiagrams;
	}
	
	public boolean canPublish() {
		return canPublish;
	}
	
	public boolean canChangeUsers() {
		return canChangeUsers;
	}

	ProjectRole(String id, String icon, boolean canEditDiagrams, boolean canPublish, boolean canChangeUsers) { 
		this.id = id; 
		this.icon = icon;
		this.canEditDiagrams = canEditDiagrams;
		this.canPublish = canPublish;
		this.canChangeUsers = canChangeUsers;
	}
	
	public String getCorrectCaps() {
		return this.name().substring(0, 1).toUpperCase() + this.name().toLowerCase().substring(1);
	}
}
