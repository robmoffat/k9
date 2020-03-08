package com.kite9.k9server.command.content;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.net.URI;

import org.apache.commons.io.Charsets;
import org.apache.xmlgraphics.util.WriterOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;

public abstract class AbstractContentCommand implements ContentCommand {

	protected transient ContentAPIFactory<?> apiFactory;
	protected transient HttpHeaders requestHeaders;
	protected transient Authentication a;
	protected transient FormatSupplier fs;
	protected transient URI url;
	
	@Override
	public void setContentApi(ContentAPIFactory<?> apiFactory, HttpHeaders requestHeaders, Authentication a, FormatSupplier fs, URI url) {
		this.apiFactory = apiFactory;
		this.requestHeaders = requestHeaders;
		this.a = a;
		this.fs = fs;
		this.url = url;
	}

	public static void persistContent(ADL adlContent, Format formatter, ContentAPI api, String commitMessage) throws Exception {
		if (formatter.isBinaryFormat()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			formatter.handleWrite(adlContent, baos, false, 0, 0);
			api.commitRevision(baos.toByteArray(), commitMessage);
		} else {
			StringWriter sw = new StringWriter();
			WriterOutputStream osw = new WriterOutputStream(sw, Charsets.UTF_8.name());
			formatter.handleWrite(adlContent, osw, false, 0, 0);
			api.commitRevision(sw.toString(), commitMessage);
		}
	}
	
	
}
