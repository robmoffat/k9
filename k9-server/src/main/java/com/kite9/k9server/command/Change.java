package com.kite9.k9server.command;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.revision.Revision;

public class Change {
	
	private final ADL contents;
	
	private final Revision changeRevision;

	public ADL getContents() {
		return contents;
	}

	public Revision getChangeRevision() {
		return changeRevision;
	}

	public Change(ADL contents, Revision changeRevision) {
		super();
		this.contents = contents;
		this.changeRevision = changeRevision;
	}
}