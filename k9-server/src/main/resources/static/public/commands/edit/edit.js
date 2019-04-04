import { getChangeUri } from '/public/bundles/api.js';

export function initEditContextMenuCallback(transition, selector) {
	
	function createEditStep(e, text) {
		return {
			"type": 'SetText',
			"fragmentId": e.getAttribute('id'),
			"newText": text
		}
	}
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='text'].selected");
		}
	}

	/**
	 * Provides a text-edit option for the context menu
	 */
	return function(event, cm) {
		
		const selectedElements = selector();
		
		if (selectedElements.length > 0) {
		
			var htmlElement = cm.get(event);
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			
			img.setAttribute("title", "Edit Text");
			img.setAttribute("src", "/public/commands/edit/edit.svg");
			
			img.addEventListener("click", function(event) {
				
				const defaultText = document.querySelector(".lastSelected text").textContent;
				const newText = prompt("Enter New Text", defaultText);
				cm.destroy();
				
				if (newText) {
					const steps = Array.from(selectedElements).map(e => createEditStep(e, newText));
					transition.postCommands(steps, getChangeUri());
				}
				
				
			});
		}
	}
}
