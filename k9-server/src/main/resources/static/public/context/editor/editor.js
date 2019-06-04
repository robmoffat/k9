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
import { Overlay } from '/public/classes/overlay/overlay.js';
import { Dragger } from '/public/classes/dragger/dragger.js';

// Behaviours

import { initActionable } from '/public/behaviours/actionable/actionable.js' 

// dragable
import { initDragable, initCompleteDragable, initDragableDropLocator, initDragableDropCallback, initDragableDragLocator } from '/public/behaviours/dragable/dragable.js' 

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
import { initLayoutMoveCallback, initLayoutContextMenuCallback, initCellCreator } from '/public/behaviours/containers/layout/layout.js';

// Links
import { initLinkable, updateLink } from '/public/behaviours/links/linkable.js';
import { initAutoConnectDragableDropCallback, initAutoConnectDragableMoveCallback } from '/public/behaviours/links/autoconnect/autoconnect.js';
import { initLinkPaletteCallback, initLinkLinkerCallback, initLinkContextMenuCallback, initLinkInstrumentationCallback, selectedLink, linkTemplateUri } from '/public/behaviours/links/link/link.js';
import { initDirectionContextMenuCallback } from '/public/behaviours/links/direction/direction.js';
import { initAlignContextMenuCallback } from '/public/behaviours/links/align/align.js';
import { initTerminatorDropCallback, initTerminatorMoveCallback } from '/public/behaviours/links/move/move.js';
import { initLinkDropLocator, initLinkDropCallback } from '/public/behaviours/links/drop/drop.js';

// labels
import { initLinkLabelContextMenuCallback, initContainerLabelContextMenuCallback } from '/public/behaviours/labels/label/label.js'; 

// text
import { initEditContextMenuCallback } from '/public/behaviours/text/edit/edit.js';

// grid
import { initCellDropLocator, initCellDragLocator, initCellDropCallback, initCellMoveCallback } from '/public/behaviours/grid/cell/cell.js';


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
		initReplacePaletteCallback(transition, 'link', {approach: 'SHALLOW'}),
		initReplacePaletteCallback(transition, 'replace-cell', {approach: 'SHALLOW', keptAttributes: ['id']}),
		initReplacePaletteCallback(transition, 'replace-connected', {approach: 'SHALLOW', keptAttributes: ['id']}),
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
		initContainerLabelContextMenuCallback(transition, document.params['label-template-uri']),
		initLinkLabelContextMenuCallback(transition, document.params['label-template-uri']),
		initInsertContextMenuCallback(palette), 
		initContainContextMenuCallback(palette), 
		initReplaceContextMenuCallback(palette, 'end'),
		initReplaceContextMenuCallback(palette, 'link'),
		initReplaceContextMenuCallback(palette, 'replace-connected'),
		initReplaceContextMenuCallback(palette, 'replace-cell'),
		initEditContextMenuCallback(transition),
		initAlignContextMenuCallback(transition, document.params['align-template-uri']),
		initDirectionContextMenuCallback(transition),
		initLayoutContextMenuCallback(transition, initCellCreator(document.params['cell-template-uri'], transition)),
		initXCPContextMenuCallback(transition, metadata),
		]); 
	
	
	initActionable(contextMenu);
	
	var dragger = new Dragger(
		[
			() => contextMenu.destroy(),
			initTerminatorMoveCallback(),
			initAutoConnectDragableMoveCallback(),
			initCellMoveCallback(initLayoutMoveCallback())
		],
		[
			initCellDropCallback(transition, initDragableDropCallback(transition)),
			initLinkDropCallback(transition),
			initTerminatorDropCallback(transition),
			initAutoConnectDragableDropCallback(transition, document.params['align-template-uri']),
			initCompleteDragable(transition)
		],
		[
			initCellDragLocator(initDragableDragLocator()),
		],
		[
			initCellDropLocator(initDragableDropLocator()),
			initLinkDropLocator()
		]);
	
	initDragable(dragger); 
	
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

