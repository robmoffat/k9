package com.kite9.k9server.command;

import com.kite9.k9server.domain.entity.RestEntity;

public abstract class AbstractSubjectCommand<X extends RestEntity> extends AbstractRepoCommand implements SubjectCommand<X> {

	protected String subjectUri;
	protected X current;
	
	@Override
	public String getSubjectUrl() {
		return subjectUri;
	}
	@Override
	public void setSubjectEntity(X e) {
		this.current = e;
	}
	/**
	 * Used for testing
	 */
	public void setSubjectUri(String subjectUri) {
		this.subjectUri = subjectUri;
	}
}
