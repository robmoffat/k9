/**
 * This composes the basic edit behaviour of the application
 */

// classes

import { Instrumentation } from "/public/classes/instrumentation/instrumentation.js";
import { Metadata } from "/public/classes/metadata/metadata.js";
import { ContextMenu } from "/public/classes/context-menu/context-menu.js";
import { Transition } from '/public/classes/transition/transition.js';
import { Palette, initPaletteHoverableAllowed } from '/public/classes/palette/Palette.js';
import { Linker } from '/public/classes/linker/Linker.js';

// Behaviours

import { initActionable } from '/public/behaviours/actionable/actionable.js' 
import { initDragable } from '/public/behaviours/dragable/dragable.js' 
import { initLinkable, updateLink } from '/public/behaviours/links/linkable.js';
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
import { initLinkPaletteCallback, initLinkLinkerCallback, initLinkContextMenuCallback, initLinkInstrumentationCallback, selectedLink, linkTemplateUri } from '/public/behaviours/links/link/link.js';
import { initInsertPaletteCallback, initInsertContextMenuCallback } from '/public/commands/insert/insert.js';
import { initEditContextMenuCallback } from '/public/commands/edit/edit.js';
import { initAutoConnectDragableDropCallback, initAutoConnectDragableMoveCallback } from '/public/behaviours/links/autoconnect/autoconnect.js';
import { initAlignContextMenuCallback } from '/public/behaviours/links/align/align.js';
import { initLayoutDragableMoveCallback, initLayoutContextMenuCallback, initCellCreator } from '/public/behaviours/containers/layout/layout.js';
import { initDirectionContextMenuCallback } from '/public/behaviours/links/direction/direction.js';
import { initReplaceContextMenuCallback, initReplacePaletteCallback } from '/public/commands/replace/replace.js';
import { initXCPContextMenuCallback } from '/public/commands/xcp/xcp.js';


var initialized = false;

function initEditor() {

	var metadata = new Metadata([
		identityMetadataCallback, 
		undoableMetadataCallback ]);
	
	var transition = new Transition(
			() => metadata.get('change'),		//change uri
			() => metadata.get('content'),		// reload uri
			[(r) => metadata.transitionCallback(r)],
			[ zoomableTransitionCallback ]);
	
	var palette = new Palette("--palette", [
		initInsertPaletteCallback(transition),
		initLinkPaletteCallback(),
		initReplacePaletteCallback(transition, 'end', {keptAttributes: ['id', 'reference'], approach: 'ATTRIBUTES'}),
		initReplacePaletteCallback(transition, 'link', {replaceContents: false, approach: 'SHALLOW'}),
		initReplacePaletteCallback(transition, 'replace-connected'),
	], document.params['palettes']);
	
	var linker = new Linker([
		initLinkLinkerCallback(transition, linkTemplateUri)
	], selectedLink, updateLink);

	var instrumentation = new Instrumentation([
		identityInstrumentationCallback,
		createUndoableInstrumentationCallback(createUndoCallback(transition), createRedoCallback(transition)),
		zoomableInstrumentationCallback,
		initLinkInstrumentationCallback(palette)
		]);
	
	var contextMenu = new ContextMenu([ 
		initDeleteContextMenuCallback(transition),
		initLinkContextMenuCallback(transition, linker),
		initInsertContextMenuCallback(palette), 
		initReplaceContextMenuCallback(palette, 'end'),
		initReplaceContextMenuCallback(palette, 'link'),
		initReplaceContextMenuCallback(palette, 'replace-connected'),
		initEditContextMenuCallback(transition),
		initAlignContextMenuCallback(transition, document.params['align-template-uri']),
		initDirectionContextMenuCallback(transition),
		initLayoutContextMenuCallback(transition, initCellCreator(document.params['cell-template-uri'], transition)),
		initXCPContextMenuCallback(transition, metadata),
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
	
	initHoverable(() => palette.get().querySelectorAll("[k9-elem][id]"), initPaletteHoverableAllowed(palette));
	
	initSelectable();

}




window.addEventListener('load', function(event) {

	if (!initialized) {
		initEditor();
		initialized = true;
	}
	
})

