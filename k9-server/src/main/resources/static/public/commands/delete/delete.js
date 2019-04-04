import { SHA1 } from "/public/bundles/sha1.js";
import { getChangeUri, hasLastSelected } from "/public/bundles/api.js";


export function initDeleteContextMenuCallback(transition, selector, cascade) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='delete'].selected")
		}
	}
	
	if (cascade == undefined) {
		cascade = function(e) {
			var ui = e.getAttribute("k9-ui");
			return (ui == undefined ? "" : ui).includes('cascade')
		}
	}
	
	/**
	 * Takes a node and creates a delete command.
	 */
	function createDeleteStep(e) {
		var id = e.getAttribute("id");
		
		return {
			fragmentId: id,
			type: 'ADLDelete',
			cascade: cascade(e)
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
		
		const e = hasLastSelected(selector());
		
		if (e.length > 0){
			var htmlElement = cm.get(event);
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Delete");
			img.setAttribute("src", "/public/commands/delete/delete.svg");
			img.addEventListener("click", () => performDelete(cm));
		}
	}
}


