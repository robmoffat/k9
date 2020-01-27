/**
 * This composes the basic edit behaviour of the application
 */

// classes

import { Instrumentation } from "/public/classes/instrumentation/instrumentation.js";
import { Metadata } from "/public/classes/metadata/metadata.js";
import { ContextMenu } from "/public/classes/context-menu/context-menu.js";
import { Transition } from "/public/classes/transition/transition.js";

// Behaviours

import { initActionable } from '/public/behaviours/actionable/actionable.js' 

// selectable
import { initSelectable } from '/public/behaviours/selectable/selectable.js';

// hoverable
import { initHoverable } from '/public/behaviours/hoverable/hoverable.js';

//zoomable
import { zoomableInstrumentationCallback, zoomableTransitionCallback } from "/public/behaviours/zoomable/zoomable.js";

// identity
import { initIdentityInstrumentationCallback, identityMetadataCallback } from "/public/behaviours/identity/identity.js";

// navigation
import { initFocusContextMenuCallback, initFocusMetadataCallback, initFocus } from "/public/behaviours/navigable/focus/focus.js";
import { initOpenContextMenuCallback } from "/public/behaviours/navigable/open/open.js";

// rest stuff
import { initDeleteEntityContextMenuCallback } from "/public/behaviours/rest/DeleteEntity/DeleteEntity.js";
import { initNewDocumentContextMenuCallback } from "/public/behaviours/rest/NewDocument/NewDocument.js";
import { initNewProjectContextMenuCallback } from "/public/behaviours/rest/NewProject/NewProject.js";
import { initUpdateContextMenuCallback } from "/public/behaviours/rest/Update/Update.js";

// indicators

import { toggleInstrumentationCallback } from '/public/behaviours/indication/toggle/toggle.js';

// authentication actions

import { initLoginContextMenuCallback } from '/public/behaviours/auth/login/login.js';
import { initLogoutContextMenuCallback } from '/public/behaviours/auth/logout/logout.js';
import { initRegisterContextMenuCallback } from '/public/behaviours/auth/register/register.js';


var initialized = false;

function initEditor() {

	var metadata = new Metadata([
		identityMetadataCallback,
		initFocusMetadataCallback(),
		]);
	
	var transition = new Transition(
			() => '/api/admin',		// change uri
			[(r) => metadata.transitionCallback(r)],
			[ zoomableTransitionCallback ]);
	
	var instrumentation = new Instrumentation([
		initIdentityInstrumentationCallback(transition),
//		createUndoableInstrumentationCallback(createUndoCallback(transition), createRedoCallback(transition)),
		zoomableInstrumentationCallback,
		toggleInstrumentationCallback,
		]);
	
	var contextMenu = new ContextMenu([ 
		initFocusContextMenuCallback(transition),
		initOpenContextMenuCallback(transition),
		initDeleteEntityContextMenuCallback(transition),
		initNewDocumentContextMenuCallback(transition),
		initNewProjectContextMenuCallback(transition),
		initUpdateContextMenuCallback(transition),
		initLoginContextMenuCallback(transition, metadata),
		initLogoutContextMenuCallback(transition, metadata),
		initRegisterContextMenuCallback(transition, metadata)
		]); 
	
	initFocus(transition)
	
	initActionable(contextMenu);
		
	initHoverable();		// init for main svg area
		
	initSelectable(undefined, true);

}

if (!initialized) {
	initEditor();
	initialized = true;
}

