import { getContextMenu, registerActionableCallback, destroyContextMenu } from "/public/behaviours/actionable/actionable.js";
import { transition, postCommands } from "/public/bundles/transition.js"
import { SHA1 } from "/public/bundles/sha1.js";
import { getChangeUri } from "/public/bundles/api.js";

/**
 * Takes a node and creates a delete command.
 */
function createDeleteStep(e) {
	var id = e.getAttribute("id");
	var deleteMode = e.getAttribute("delete");
	if (deleteMode == 'none') {
		return null;
	} 
	
	return {
		fragmentHash: '', 
		fragmentId: id,
		type: 'ADLDelete',
		cascade: deleteMode != 'single'
	};
}

function performDelete() {
	const selectedElements = document.querySelectorAll("[id].selected");
	
	const steps = Array.from(selectedElements)
		.map(e => createDeleteStep(e))
		.filter(x => x != null);
	
	if (steps.length > 0) {
		destroyContextMenu();
		postCommands(steps, getChangeUri());
		console.log("delete complete");
	}
}


/**
 * Provides a delete option for the context menu
 */
registerActionableCallback(function(event) {
	
	const e = document.querySelector("[id].lastSelected");
	
	if ((e) && ('none' != e.getAttribute('delete')) && (e.classList.contains("selected"))){
		var htmlElement = getContextMenu(event);
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		img.setAttribute("title", "Delete");
		img.setAttribute("src", "/public/commands/delete/delete.svg");
		img.addEventListener("click", performDelete);
	}
});

/**
 * This should only be called once.  Adds the delete-key shortcut.
 */
document.addEventListener('keydown', function(event) {
	if (event.key == 'Delete') {
	    performDelete();
	}
});
