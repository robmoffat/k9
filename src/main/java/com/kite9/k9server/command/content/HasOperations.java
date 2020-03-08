package com.kite9.k9server.command.content;

import java.util.EnumSet;

import com.kite9.k9server.adl.holder.ADL;

public interface HasOperations {

	enum Operation { REDO, UNDO, COMMIT }

	EnumSet<HasOperations.Operation> getOperations();

	public default void addMeta(ADL adl) {
		EnumSet<HasOperations.Operation> ops = getOperations();
		adl.setMeta("undo", ""+ops.contains(HasOperations.Operation.UNDO));
		adl.setMeta("redo", ""+ops.contains(HasOperations.Operation.REDO));
	}
}