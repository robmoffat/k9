package com.kite9.k9server.command;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.document.Document;
import com.kite9.k9server.domain.user.User;

/**
 * This command applies a bunch of {@link Step}s to a single revision of a diagram.
 * If any step fails, the command throws an exception.
 * 
 * @author robmoffat
 */
public class StepsCommand extends AbstractCommand {
	
	private Step[] steps;

	public StepsCommand(Document d, User author, Step... steps) {
		super(d, author);
		this.steps = steps;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		for (Step step : steps) {
			adl = step.apply(this, adl);
		}
		
		return adl;
	}

}
