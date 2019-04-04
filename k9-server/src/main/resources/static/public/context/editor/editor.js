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
import { initSelectable } from '/public/behaviours/selectable/selectable.js';
import { initHoverable } from '/public/behaviours/hoverable/hoverable.js';

// Instrumentation Imports

import { zoomableInstrumentationCallback, zoomableTransitionCallback } from "/public/behaviours/zoomable/zoomable.js";
import { identityInstrumentationCallback, identityMetadataCallback } from "/public/behaviours/identity/identity.js";
import { createUndoableInstrumentationCallback, undoableMetadataCallback } from "/public/behaviours/undoable/undoable.js";

// Commands

import { createUndoCallback, createRedoCallback } from '/public/commands/undo/undo.js';
import { createMoveDragableDropCallback, moveDragableMoveCallback, initCompleteDragable } from '/public/commands/move/move.js';
import { initDeleteContextMenuCallback } from '/public/commands/delete/delete.js';
import { initLinkPaletteCallback, initLinkLinkerCallback, initLinkContextMenuCallback, initLinkInstrumentationCallback, selectedLink, linkTemplateUri } from '/public/commands/link/link.js';
import { initInsertPaletteCallback, initInsertContextMenuCallback } from '/public/commands/insert/insert.js';
import { initEditContextMenuCallback } from '/public/commands/edit/edit.js';
import { initAutoConnectDragableDropCallback, initAutoConnectDragableMoveCallback } from '/public/commands/autoconnect/autoconnect.js';
import { initAlignContextMenuCallback } from '/public/commands/align/align.js';
import { initLayoutDragableMoveCallback, initLayoutContextMenuCallback, initCellCreator } from '/public/commands/layout/layout.js';
import { initDirectionContextMenuCallback } from '/public/commands/direction/direction.js';

var initialized = false;

function initEditor() {

	var metadata = new Metadata([
		identityMetadataCallback, 
		undoableMetadataCallback ]);
	
	var transition = new Transition([
		(r) => metadata.transitionCallback(r),
	],[
		zoomableTransitionCallback
	]);
	
	var shapePalette = new Palette("--palette", [
		initInsertPaletteCallback(transition)
	], document.params['shape-palette']);

	var linkPalette = new Palette("--linkpalette", [
		initLinkPaletteCallback()
	], document.params['link-palette']);
	
	var linker = new Linker([
		initLinkLinkerCallback(transition, linkTemplateUri)
	], selectedLink);

	var instrumentation = new Instrumentation([
		identityInstrumentationCallback,
		createUndoableInstrumentationCallback(createUndoCallback(transition), createRedoCallback(transition)),
		zoomableInstrumentationCallback,
		initLinkInstrumentationCallback(linkPalette)
		]);
	
	var contextMenu = new ContextMenu([ 
		initDeleteContextMenuCallback(transition),
		initLinkContextMenuCallback(transition, linker),
		initInsertContextMenuCallback(shapePalette), 
		initEditContextMenuCallback(transition),
//		initAlignContextMenuCallback(transition, document.params['align-template-uri']),
		initDirectionContextMenuCallback(transition),
//		initLayoutContextMenuCallback(transition, initCellCreator(document.params['cell-template-uri'], transition))
		
		]); 
	
	
	initActionable(contextMenu);
	
	initDragable([
		() => contextMenu.destroy(),
		moveDragableMoveCallback,
		initAutoConnectDragableMoveCallback(),
		initLayoutDragableMoveCallback()
	], [
		createMoveDragableDropCallback(transition),	
		initAutoConnectDragableDropCallback(transition, document.params['align-template-uri']),
		initCompleteDragable(transition)
	]);
	
	initLinkable(linker);
		
	initHoverable();		// init for main svg area
	
	initHoverable(function() { return document.querySelectorAll("#--palette svg [id][k9-palette~=insertable]"); });

	initHoverable(function() { return document.querySelectorAll("#--linkpalette svg [id][k9-palette~=chooseable]"); });

	initSelectable();

}




window.addEventListener('load', function(event) {

	if (!initialized) {
		initEditor();
		initialized = true;
	}
	
})

