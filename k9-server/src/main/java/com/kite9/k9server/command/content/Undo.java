package com.kite9.k9server.command.content;

import java.io.InputStream;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;

public class Undo extends AbstractContentCommand {

	public Undo() {
		super();
	}
	
	@Override
	public ADL applyCommand() throws CommandException {
		try {
			Version current = api.getCurrentVersion();
			List<Version> versions = api.getVersionHistory();
			int idx = versions.indexOf(current);
			idx = Math.min(versions.size()-1, idx + 1);
			current = versions.get(idx);
			InputStream is = api.updateCurrentRevision(current.getVersionId());
			Format f = fs.getFormatFor(url.getPath()).orElseThrow();
			ADL adl = f.handleRead(is, url, requestHeaders);
			return adl;
		} catch (Exception e) {
			throw new CommandException(HttpStatus.BAD_REQUEST, "Couldn't redo:", e, this);
		}
	}
}
