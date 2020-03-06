package com.kite9.k9server.command.content;

import java.io.InputStream;

import org.springframework.http.HttpStatus;

import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class Redo extends AbstractContentCommand {
	
	public Redo() {
		super();
	}

	@Override
	public ADL applyCommand() throws CommandException {
		try {
			InputStream is = api.redo();
			Format f = fs.getFormatFor(url.getPath()).orElseThrow();
			ADL adl = f.handleRead(is, url, requestHeaders);
			return adl;
		} catch (Exception e) {
			throw new CommandException(HttpStatus.BAD_REQUEST, "Couldn't redo:", e, this);
		}
	}
}
