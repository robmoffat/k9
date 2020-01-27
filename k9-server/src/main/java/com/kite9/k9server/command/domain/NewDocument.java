package com.kite9.k9server.command.domain;

import java.net.URI;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.AbstractSubjectCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.links.ContentResourceProcessor;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

public class NewDocument extends AbstractSubjectCommand<Project> {
	
	public String title;
	
	public String description;
	
	public String templateUri;
	
	public boolean open = false;	// sends a redirect after creating.
	
	@Override
	public Object applyCommand() throws CommandException {
		if (!current.checkWrite()) {
			throw new CommandException(HttpStatus.UNAUTHORIZED, "User can't write to "+current, this);
		}
		
		try {
			// create document
			Document out = new Document();
			out.setTitle(title);
			out.setDescription(description);
			out.setProject(current);
			getRepositoryFor(Document.class).save(out);
			
			// we need to return ADL which copies the templateUri;
			ADL adl = ADLImpl.uriMode(new URI(templateUri), requestHeaders);
			String content = adl.getAsADLString();
			
			// create first revision
			Revision r = new Revision();
			r.setDocument(out);
			String userId =  SecurityContextHolder.getContext().getAuthentication().getName();
			User u = ((UserRepository) getRepositoryFor(User.class)).findByUsername(userId);
			r.setAuthor(u);
			r.setXml(content);
			getRepositoryFor(Revision.class).save(r);
			
			// ensure the document has the current revision set
			out.setCurrentRevision(r);
			getRepositoryFor(Document.class).save(out);
			
			current.getDocuments().add(out);
			if (open) {
				return Collections.singletonMap("redirect", out.getLocalId()+ContentResourceProcessor.CONTENT_URL);
			} else {
				return current;
			}
		} catch (Exception e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't create document: ", e, this);
		}
	}

}
