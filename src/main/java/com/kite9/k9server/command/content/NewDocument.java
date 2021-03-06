package com.kite9.k9server.command.content;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class NewDocument extends AbstractContentCommand {
	
	public String title;
		
	public String templateUri;
	
	public String format;
	
	@Override
	public Object applyCommand() throws CommandException {
		try {
			ADL adlContent = getNewDocumentContent(requestHeaders);
			// work out filename, format
			Format formatter = fs.getFormatFor(format).orElseThrow();
			
			String newPath = url.getPath()+"/"+ title+"."+formatter.getExtension();
			URI fileURI = new URI(url.getScheme(), url.getHost(), newPath, url.getFragment());
			ContentAPI apiForFile = apiFactory.createAPI(a, fileURI.toString());
			
			AbstractContentCommand.persistContent(adlContent, formatter, apiForFile, "Created New Diagram in Kite9 named "+title);
			return null;
		} catch (Exception e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't create document: ", e, this);
		}
	}

	public ADL getNewDocumentContent(HttpHeaders h) throws Exception {
		// we need to return ADL which copies the templateUri;
		Format f = fs.getFormatFor(templateUri).orElseThrow();
		ADL adl = f.handleRead(new URI(templateUri), h);
		return adl;
	}

}
