import { SHA1 } from "/public/bundles/sha1.js";
import { getChangeUri, hasLastSelected } from "/public/bundles/api.js";


export function initDeleteContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='delete'].selected")
		}
	}
	
	/**
	 * Takes a node and creates a delete command.
	 */
	function createDeleteStep(e) {
		var id = e.getAttribute("id");
		var ui = e.getAttribute("k9-ui");
		
		return {
			fragmentId: id,
			type: 'ADLDelete',
			cascade: ui.includes('cascade')
		};
	}

	function performDelete(cm) {
		const steps = Array.from(selector())
			.forEach(e => createDeleteStep(e));
		
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
		
		const e = selector();
		
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


