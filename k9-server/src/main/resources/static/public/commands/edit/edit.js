import { getChangeUri } from '/public/bundles/api.js';

export function initEditContextMenuCallback(transition) {
	
	function createEditStep(e, text) {
		return {
			"type": 'SetText',
			"fragmentId": e.getAttribute('id'),
			"newText": text
		}
	}

	/**
	 * Provides a text-edit option for the context menu
	 */
	return function(event, cm) {
		
		const selectedElements = document.querySelectorAll("[id].selected.text");
		
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
