/**
 * This handles moving a block from one place to another on the diagram, via drag and drop.
 * You can't drop into an element unless it has 
 */
import { registerDragableDropCallback, registerDragableMoveCallback, getDropTarget } from "/public/behaviours/dragable/dragable.js";
import { transition, postCommands } from "/public/bundles/transition.js"
 

registerDragableDropCallback(function(dragTarget, evt) {

	var targetElement = getDropTarget(evt.target);
	
	if (targetElement == null) {
		returnDragTarget();
	} else {
		alert(dragTarget.id + ' has been dropped on top of ' + targetElement.id);
	}

	return false;
})


