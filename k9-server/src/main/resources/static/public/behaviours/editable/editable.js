/**
 * This composes the basic edit behaviour of the application
 */

// classes

import { Instrumentation } from "/public/classes/instrumentation/instrumentation.js";
import { Metadata } from "/public/classes/metadata/metadata.js";
import { ContextMenu } from "/public/classes/context-menu/context-menu.js";
import { Transition } from '/public/classes/transition/transition.js';

// Behaviours

import { initActionable } from '/public/behaviours/actionable/actionable.js' 

// Context Menu Imports

import { initDeleteContextMenuCallback } from '/public/commands/delete/delete.js';
//@script url('/public/behaviours/editable/delete.js');
//@script url('/public/commands/move/move.js');
//@script url('/public/commands/link/link.js');

// Instrumentation Imports

import { zoomableInstrumentationCallback } from "/public/behaviours/zoomable/zoomable.js";
import { identityInstrumentationCallback, identityMetadataCallback } from "/public/behaviours/identity/identity.js";
import { createUndoableInstrumentationCallback, undoableMetadataCallback } from "/public/behaviours/undoable/undoable.js";
import { createUndoCallback, createRedoCallback } from '/public/commands/undo/undo.js';



var metadata = new Metadata([
	identityMetadataCallback, 
	undoableMetadataCallback ]);

var transition = new Transition([
	
	(r) => metadata.transitionCallback(r)
	
	
]);

new Instrumentation([
	identityInstrumentationCallback,
	createUndoableInstrumentationCallback(createUndoCallback(transition), createRedoCallback(transition)),
	zoomableInstrumentationCallback
	]);

var contextMenu = new ContextMenu([ initDeleteContextMenuCallback(transition) ]);

initActionable(contextMenu);
