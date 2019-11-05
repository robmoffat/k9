package com.kite9.k9server.command;

import java.net.URI;

import org.springframework.http.HttpHeaders;

import com.kite9.k9server.domain.entity.RestEntity;

/**
 * This is a command that applies to a specific domain object.  Could be undo/redo
 * update, delete etc.
 * 
 * @author robmoffat
 *
 * @param <X>
 */
public interface DomainCommand<C extends RestEntity> extends Command {

	public void setCommandContext(C current, URI url, HttpHeaders requestHeaders);

}