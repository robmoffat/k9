import { icon } from '/public/bundles/form.js';

var canUndo = false;
var canRedo = false;

var undo = undefined;
var redo = undefined;

function updateOpacity() {
	if (undo) {
		undo.style.opacity = canUndo ? 1 : .5;
	}
	
	if (redo) {
		redo.style.opacity = canRedo ? 1 : .5;
	}
}

export function undoableMetadataCallback(metadata) {
	canUndo = metadata['undo'] == 'true';
	canRedo = metadata['redo'] == 'true';
	updateOpacity();
}

function ensureButton(nav, name, cb) {
	var b = nav.querySelector("#--"+name);
	
	if (b == undefined) {
		var b = icon('--'+name, name, 
				 "/public/behaviours/revisioned/"+name+".svg",
				 cb);
		nav.appendChild(b);
	}
	
	return b;
}

/**
 * Provide the on-click actions that will be called when the user hits undo/redo
 */
export function createUndoableInstrumentationCallback(undoAction, redoAction) {
	return function(nav) {
		undo = ensureButton(nav, "undo", (evt) => undoAction(evt, canUndo));
		redo = ensureButton(nav, "redo", (evt) => redoAction(evt, canRedo));
		updateOpacity();
	}
}