package com.kite9.k9server.domain.permission;

import javax.persistence.Entity;

import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.revision.Revision;

public interface Permission {

	public boolean canRead(Document d);
	
	public boolean canWrite(Document d);
	
	public boolean canRead(Project p);
	
	public boolean canWrite(Project p);
	
	public boolean canRead(Revision r);
	
}
