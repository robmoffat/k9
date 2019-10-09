package com.kite9.k9server.command.domain;

import java.net.URI;

import org.springframework.http.HttpHeaders;

import com.kite9.k9server.command.Command;
import com.kite9.k9server.domain.SecuredCrudRepository;

/**
 * This is a command that applies to a specific domain object.  Could be undo/redo
 * update, delete etc.
 * 
 * @author robmoffat
 *
 * @param <X>
 */
public interface DomainCommand<X> extends Command {

	public <Y extends SecuredCrudRepository<X>> void setCommandContext(Y repo, X current, URI url, HttpHeaders requestHeaders);

}
