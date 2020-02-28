package com.kite9.k9server.command.content;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.command.Command;

/**
 * Encapsulates the commands are often applied to a subject, e.g. a particular 
 * document or user.
 * 
 * @author robmoffat
 *
 */
public interface ContentCommand extends Command {
	
	public void setContentApi(ContentAPI g, HttpHeaders h, Authentication a, FormatSupplier fs);
}
