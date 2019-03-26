/**
 * This composes the basic edit behaviour of the application
 */

// classes

import { Instrumentation } from "/public/classes/instrumentation/instrumentation.js";
import { Metadata } from "/public/classes/metadata/metadata.js";
import { ContextMenu } from "/public/classes/context-menu/context-menu.js";
import { Transition } from '/public/classes/transition/transition.js';
import { Linker } from '/public/classes/linker/Linker.js';
import { Palette } from '/public/classes/palette/Palette.js';

// Behaviours

import { initActionable } from '/public/behaviours/actionable/actionable.js' 
import { initDragable } from '/public/behaviours/dragable/dragable.js' 
import { initLinkable } from '/public/behaviours/linkable/linkable.js';


// Context Menu Imports

//@script url('/public/commands/link/link.js');

// Instrumentation Imports

import { zoomableInstrumentationCallback, zoomableTransitionCallback } from "/public/behaviours/zoomable/zoomable.js";
import { identityInstrumentationCallback, identityMetadataCallback } from "/public/behaviours/identity/identity.js";
import { createUndoableInstrumentationCallback, undoableMetadataCallback } from "/public/behaviours/undoable/undoable.js";

// Commands

import { createUndoCallback, createRedoCallback } from '/public/commands/undo/undo.js';
import { createMoveDragableDropCallback, moveDragableMoveCallback } from '/public/commands/move/move.js';
import { initDeleteContextMenuCallback } from '/public/commands/delete/delete.js';
import { initLinkLinkerCallback, initLinkContextMenuCallback } from '/public/commands/link/link.js';
import { initInsertPaletteCallback, initInsertContextMenuCallback } from '/public/commands/insert/insert.js';


var metadata = new Metadata([
	identityMetadataCallback, 
	undoableMetadataCallback ]);

var transition = new Transition([
	(r) => metadata.transitionCallback(r),
],[
	zoomableTransitionCallback
]);

var instrumentation = new Instrumentation([
	identityInstrumentationCallback,
	createUndoableInstrumentationCallback(createUndoCallback(transition), createRedoCallback(transition)),
	zoomableInstrumentationCallback
	]);

var linker = new Linker([
	initLinkLinkerCallback(transition)
]);


var palette = new Palette([
	initInsertPaletteCallback(transition)
], '/public/behaviours/editable/palette-example');

var contextMenu = new ContextMenu([ 
	initDeleteContextMenuCallback(transition),
	initLinkContextMenuCallback(transition, linker),
	initInsertContextMenuCallback(palette)
]);


initActionable(contextMenu);

initDragable([
	moveDragableMoveCallback,
], [
	createMoveDragableDropCallback(transition)	
]);

initLinkable(linker);
