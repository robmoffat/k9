package com.kite9.k9server.command.domain;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.command.AbstractRepoCommand;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.project.Project;
import com.kite9.k9server.domain.revision.Revision;
import com.kite9.k9server.domain.user.User;
import com.kite9.k9server.domain.user.UserRepository;

public class NewDocument extends AbstractRepoCommand<Project> {
	
	public String title;
	
	public String description;
	
	public String templateUri;
	
	@Override
	public Document applyCommand() throws CommandException {
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
			ADL adl = new ADLImpl(new URI(templateUri), requestHeaders);
			String content = adl.getAsXMLString();
			
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

			return out;
		} catch (Exception e) {
			throw new CommandException(HttpStatus.CONFLICT, "Couldn't create document: ", e, this);
		}
	}

}
