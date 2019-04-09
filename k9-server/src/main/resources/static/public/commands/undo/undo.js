/**
 * Provides click-callbacks for undo/redo functionality.
 */

export function createUndoCallback(transition) {
	
	return function(evt, canUndo) {
		if (canUndo) {
			transition.postCommands([{type: "Undo"}]);
			console.log("undo");
		}
	};	
}

export function createRedoCallback(transition) {
	
	return function(evt, canUndo) {
		if (canUndo) {
			transition.postCommands([{type: "Redo"}]);
			console.log("redo");
		}
	};	
}



