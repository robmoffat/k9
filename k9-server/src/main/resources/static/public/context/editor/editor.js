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

// dragable
import { initDragable } from '/public/behaviours/dragable/dragable.js' 
import { createMoveDragableDropCallback, moveDragableMoveCallback, initCompleteDragable } from '/public/behaviours/dragable/move/move.js';

// selectable
import { initSelectable } from '/public/behaviours/selectable/selectable.js';
import { initDeleteContextMenuCallback } from '/public/behaviours/selectable/delete/delete.js';
import { initReplaceContextMenuCallback, initReplacePaletteCallback } from '/public/behaviours/selectable/replace/replace.js';
import { initXCPContextMenuCallback } from '/public/behaviours/selectable/xcp/xcp.js';

// hoverable
import { initHoverable } from '/public/behaviours/hoverable/hoverable.js';


//zoomable
import { zoomableInstrumentationCallback, zoomableTransitionCallback } from "/public/behaviours/zoomable/zoomable.js";

// identity
import { identityInstrumentationCallback, identityMetadataCallback } from "/public/behaviours/identity/identity.js";

// undo
import { createUndoableInstrumentationCallback, undoableMetadataCallback } from "/public/behaviours/undoable/undoable.js";
import { createUndoCallback, createRedoCallback } from '/public/behaviours/undoable/undoredo/undoredo.js';

// Containers
import { initInsertPaletteCallback, initInsertContextMenuCallback } from '/public/behaviours/containers/insert/insert.js';
import { initContainPaletteCallback, initContainContextMenuCallback } from '/public/behaviours/containers/contain/contain.js';
import { initLayoutDragableMoveCallback, initLayoutContextMenuCallback, initCellCreator } from '/public/behaviours/containers/layout/layout.js';

// Links
import { initLinkable, updateLink } from '/public/behaviours/links/linkable.js';
import { initAutoConnectDragableDropCallback, initAutoConnectDragableMoveCallback } from '/public/behaviours/links/autoconnect/autoconnect.js';
import { initLinkPaletteCallback, initLinkLinkerCallback, initLinkContextMenuCallback, initLinkInstrumentationCallback, selectedLink, linkTemplateUri } from '/public/behaviours/links/link/link.js';
import { initDirectionContextMenuCallback } from '/public/behaviours/links/direction/direction.js';
import { initAlignContextMenuCallback } from '/public/behaviours/links/align/align.js';

// text
import { initEditContextMenuCallback } from '/public/behaviours/text/edit/edit.js';


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
		initContainPaletteCallback(transition),
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
		initContainContextMenuCallback(palette), 
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

