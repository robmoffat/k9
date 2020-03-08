package com.kite9.k9server.command.content;

public interface UndoableAPI<X> {

	public X getCurrentRevisionContent();

	/**
	 * Returns input stream for previous version, or current version again if
	 * undo can't be done
	 */
	public X undo();
	
	/**
	 * Returns input stream for previous version, or current version again if
	 * redo can't be done
	 */
	public X redo();
	
}
