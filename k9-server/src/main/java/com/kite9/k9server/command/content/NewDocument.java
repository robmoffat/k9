package com.kite9.k9server.command.content;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.xmlgraphics.util.WriterOutputStream;
import org.kohsuke.github.GHBlob;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.github.AbstractGithubController;
import com.kite9.k9server.github.EntityController;
import com.kite9.k9server.github.GithubContentAPI;

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
			
			if (formatter.isBinaryFormat()) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				formatter.handleWrite(adlContent, baos, false, 0, 0);
				api.commitRevision(baos.toByteArray(), "Created New Diagram in Kite9 named "+title);
			} else {
				StringWriter sw = new StringWriter();
				WriterOutputStream osw = new WriterOutputStream(sw, Charsets.UTF_8.name());
				formatter.handleWrite(adlContent, osw, false, 0, 0);
				api.commitRevision(sw.toString(), "Created New Diagram in Kite9 named "+title);
			}

			LinkBuilder lb = BasicLinkBuilder.linkToCurrentMapping();
			
			return EntityController.templateDirectoryPage(type, owner, reponame, path, p, repo, lb, fs);
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
