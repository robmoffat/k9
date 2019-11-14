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

// selectable
import { initSelectable } from '/public/behaviours/selectable/selectable.js';

// hoverable
import { initHoverable } from '/public/behaviours/hoverable/hoverable.js';

//zoomable
import { zoomableInstrumentationCallback, zoomableTransitionCallback } from "/public/behaviours/zoomable/zoomable.js";

// identity
import { identityInstrumentationCallback, identityMetadataCallback } from "/public/behaviours/identity/identity.js";

// rest stuff
import { initDeleteEntityContextMenuCallback } from "/public/behaviours/rest/DeleteEntity/DeleteEntity.js";


var initialized = false;

function initEditor() {

	var metadata = new Metadata([
		identityMetadataCallback ]);
	
	var transition = new Transition(
			() => metadata.get('change'),		//change uri
			() => metadata.get('content'),		// reload uri
			[(r) => metadata.transitionCallback(r)],
			[ zoomableTransitionCallback ]);
	
	var instrumentation = new Instrumentation([
		identityInstrumentationCallback,
//		createUndoableInstrumentationCallback(createUndoCallback(transition), createRedoCallback(transition)),
		zoomableInstrumentationCallback,
		]);
	
	var contextMenu = new ContextMenu([ 
		initDeleteEntityContextMenuCallback(transition),
		]); 
	
	
	initActionable(contextMenu);
		
	initHoverable();		// init for main svg area
		
	initSelectable();

}

if (!initialized) {
	initEditor();
	initialized = true;
}

