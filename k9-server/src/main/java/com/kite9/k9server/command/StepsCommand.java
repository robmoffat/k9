package com.kite9.k9server.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * This command applies a bunch of {@link Step}s to a single revision of a diagram.
 * If any step fails, the command throws an exception.
 * 
 * @author robmoffat
 */
public class StepsCommand implements Command {
	
	private Step[] steps;
	
	@JsonDeserialize(as = ADLImpl.class)
	private ADL input;
	
	public StepsCommand() {
	}

	public StepsCommand(ADL d, Step... steps) {
		this.input = d;
		this.steps = steps;
	}
	
	public ADL applyCommand() throws CommandException {
		ADL adl = getInput();
		if (steps != null) {
			for (Step step : steps) {
				adl = step.apply(this, adl);
			}
		}
		
		return adl;
	}

	@Override
	public ADL getInput() {
		return input;
	}
	
	public Step[] getSteps() {
		return steps;
	}

	public void setSteps(Step[] steps) {
		this.steps = steps;
	}

	public void setInput(ADL input) {
		this.input = input;
	}


}
