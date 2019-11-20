package com.kite9.k9server.command;

import com.kite9.k9server.domain.entity.RestEntity;

/**
 * Encapsulates the commands are often applied to a subject, e.g. a particular 
 * document or user.
 * 
 * @author robmoffat
 *
 */
public interface SubjectCommand<X extends RestEntity> extends Command {

	public String getSubjectUrl();
	
	public void setSubjectEntity(X e);
}
