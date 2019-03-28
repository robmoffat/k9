import { SHA1 } from "/public/bundles/sha1.js";
import { getChangeUri } from "/public/bundles/api.js";


export function initDeleteContextMenuCallback(transition, selector) {
	
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

	function performDelete(cm) {
		const selectedElements = document.querySelectorAll("[id].selected");
		
		const steps = Array.from(selectedElements)
			.map(e => createDeleteStep(e))
			.filter(x => x != null);
		
		if (steps.length > 0) {
			cm.destroy();
			transition.postCommands(steps, getChangeUri());
			console.log("delete complete");
		}
	}

	/**
	 * This should only be called once.  Adds the delete-key shortcut.
	 */
	document.addEventListener('keydown', function(event) {
		if (event.key == 'Delete') {
		    performDelete();
		}
	});
	
	/**
	 * Provides a delete option for the context menu
	 */
	return function(event, cm) {
		
		const e = document.querySelector("[id].lastSelected");
		
		if ((e) && ('none' != e.getAttribute('delete')) && (e.classList.contains("selected"))){
			var htmlElement = cm.get(event);
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Delete");
			img.setAttribute("src", "/public/commands/delete/delete.svg");
			img.addEventListener("click", () => performDelete(cm));
		}
	}
}


