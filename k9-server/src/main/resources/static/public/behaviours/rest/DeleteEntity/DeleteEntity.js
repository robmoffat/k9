import { hasLastSelected } from "/public/bundles/api.js";


export function initDeleteEntityContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='DeleteEntity'].selected")
		}
	}
	
	/**
	 * Takes a node and creates a delete command.
	 */
	function createDeleteStep(e, steps, cascade) {
		var id = e.getAttribute("id");
		
		steps.push({
			fragmentId: id,
			type: 'DeleteEntity',
			cascade: cascade
		});
		
		if (!cascade) {
			Array.from(e.children).forEach(f => {
				if (orphan(f)) {
					createDeleteStep(f, steps, false);
				}
			});
		}
	}

	function performDelete(cm) {
		var steps = [];
		selector().forEach(e => createDeleteStep(e, steps, cascade(e)));
		
		if (steps.length > 0) {
			cm.destroy();
			transition.postCommands(steps);
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
			img.setAttribute("src", "/public/behaviours/rest/DeleteEntity/delete.svg");
			img.addEventListener("click", () => performDelete(cm));
		}
	}
}


