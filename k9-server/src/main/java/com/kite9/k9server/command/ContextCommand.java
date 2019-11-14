package com.kite9.k9server.command;

import java.net.URI;

import org.springframework.http.HttpHeaders;

import com.kite9.k9server.domain.entity.RestEntity;

/**
 * This is a command that is applied within a particular view.  For example, the view
 * could be a project, or a document.
 * 
 * The context encapsulates what the user is looking at when a particular action is taken.
 * 
 * Note, the action will sometimes change what the user is looking at, so when you create a new 
 * project, you'll have your context changed to that project.   Other times, the context will
 * remain unchanged.
 * 
 * 
 * @author robmoffat
 *
 * @param <X>
 */
public interface ContextCommand extends Command {

	/**
	 * @param context domain object where we are applying the command
	 * @param url where we are applying the command
	 * @param requestHeaders headers that were sent with the request.
	 */
	public void setCommandContext(RestEntity context, URI url, HttpHeaders requestHeaders);

}
