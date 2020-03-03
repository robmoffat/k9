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
import { Revisions } from '/public/classes/revisions/revisions.js';

// Behaviours

import { initActionable } from '/public/behaviours/actionable/actionable.js' 

// dragable
import { initDragable, initCompleteDragable, initDragableDropLocator, initDragableDropCallback, initDragableDragLocator } from '/public/behaviours/dragable/dragable.js' 

// selectable
import { initSelectable } from '/public/behaviours/selectable/selectable.js';
import { initDeleteContextMenuCallback } from '/public/behaviours/selectable/delete/delete.js';
import { initReplaceContextMenuCallback, initReplacePaletteCallback } from '/public/behaviours/selectable/replace/replace.js';
import { initXCPContextMenuCallback } from '/public/behaviours/selectable/xcp/xcp.js';

// indication

import { toggleInstrumentationCallback } from '/public/behaviours/indication/toggle/toggle.js';

// hoverable
import { initHoverable } from '/public/behaviours/hoverable/hoverable.js';

// navigation
import { closeMetadataCallback, closeInstrumentationCallback } from "/public/behaviours/navigable/close/close.js";

//zoomable
import { zoomableInstrumentationCallback, zoomableTransitionCallback } from "/public/behaviours/zoomable/zoomable.js";

// identity
import { initIdentityInstrumentationCallback, identityMetadataCallback } from "/public/behaviours/identity/identity.js";

// undo, redo, revisions
import { createUndoableInstrumentationCallback, undoableRevisionsCallback } from "/public/behaviours/revisioned/undoable.js";
import { createUndoCallback, createRedoCallback } from '/public/behaviours/revisioned/undoredo/undoredo.js';
import { initHistoryDocumentCallback, initHistoryMetadataCallback } from '/public/behaviours/revisioned/history.js';

// Containers
import { initInsertPaletteCallback, initInsertContextMenuCallback } from '/public/behaviours/containers/insert/insert.js';
import { initContainPaletteCallback, initContainContextMenuCallback } from '/public/behaviours/containers/contain/contain.js';
import { initLayoutMoveCallback, initLayoutContextMenuCallback, initCellCreator } from '/public/behaviours/containers/layout/layout.js';

// Links
import { initLinkable, updateLink } from '/public/behaviours/links/linkable.js';
import { initAutoConnectDropCallback, initAutoConnectMoveCallback } from '/public/behaviours/links/autoconnect/autoconnect.js';
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
import { initCellDropLocator, initCellDragLocator, initCellDropCallback, initCellMoveCallback } from '/public/behaviours/grid/drag/drag.js';
import { replaceCellSelector, initGridReplacePaletteCallback } from '/public/behaviours/grid/replace/replace.js';
import { initSelectContextMenuCallback } from '/public/behaviours/grid/select/select.js';
import { initCellAppendContextMenuCallback } from '/public/behaviours/grid/append/append.js';

var initialized = false;

function initEditor() {
	
	var revisions = new Revisions(
		[undoableRevisionsCallback]);

	var metadata = new Metadata([
		identityMetadataCallback,
		initHistoryMetadataCallback(revisions),
		closeMetadataCallback ]);
		
	var transition = new Transition(
			() => metadata.get('self'),		// command
			[(r) => metadata.transitionCallback(r) ],			// load callbacks
			[ initHistoryDocumentCallback(revisions, metadata) ],  // document callbacks
			[ zoomableTransitionCallback ]);	// animation callbacks
	
	var palette = new Palette("--palette", [
		initInsertPaletteCallback(transition),
		initContainPaletteCallback(transition),
		initLinkPaletteCallback(),
		initReplacePaletteCallback(transition, 'end', {keptAttributes: ['id', 'reference'], approach: 'ATTRIBUTES'}),
		initReplacePaletteCallback(transition, 'link', {approach: 'SHALLOW'}),
		initGridReplacePaletteCallback(transition, 'replace-cell'),
		initReplacePaletteCallback(transition, 'replace-connected', {approach: 'SHALLOW', keptAttributes: ['id']}),
	], document.params['palettes']);
	
	var linker = new Linker([
		initLinkLinkerCallback(transition, linkTemplateUri)
	], selectedLink, updateLink);

	var instrumentation = new Instrumentation([
		initIdentityInstrumentationCallback(transition),
		closeInstrumentationCallback,
		createUndoableInstrumentationCallback(createUndoCallback(transition), createRedoCallback(transition)),
		zoomableInstrumentationCallback,
		initLinkInstrumentationCallback(palette),
		toggleInstrumentationCallback
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
		initReplaceContextMenuCallback(palette, 'replace-cell', replaceCellSelector),
		initEditContextMenuCallback(transition),
		initAlignContextMenuCallback(transition, document.params['align-template-uri']),
		initDirectionContextMenuCallback(transition),
		initLayoutContextMenuCallback(transition, initCellCreator(document.params['cell-template-uri'], transition)),
		initSelectContextMenuCallback(),
		initXCPContextMenuCallback(transition, metadata),
		initCellAppendContextMenuCallback(transition)
		]); 
	
	
	initActionable(contextMenu);
	
	var dragger = new Dragger(
		[
			() => contextMenu.destroy(),
			initTerminatorMoveCallback(),
			initAutoConnectMoveCallback(),
			initLayoutMoveCallback(),
			initCellMoveCallback(),
		],
		[
			initDragableDropCallback(transition),
			initLinkDropCallback(transition),
			initTerminatorDropCallback(transition),
			initAutoConnectDropCallback(transition, document.params['align-template-uri']),
			initCellDropCallback(transition),
			initCompleteDragable(transition)
		],
		[
			initDragableDragLocator(),
			initCellDragLocator()
		],
		[
			initDragableDropLocator(),
			initCellDropLocator(),
			initLinkDropLocator(),
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

