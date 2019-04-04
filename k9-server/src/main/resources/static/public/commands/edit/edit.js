import { getChangeUri, hasLastSelected } from '/public/bundles/api.js';

export function initEditContextMenuCallback(transition, selector, defaultSelector) {
	
	function createEditStep(e, text) {
		return {
			"type": 'SetText',
			"fragmentId": e.getAttribute('id'),
			"newText": text
		}
	}
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='edit'].selected");
		}
	}
	
	if (defaultSelector == undefined) {
		defaultSelector = function(e) {
			const text = e.querySelector("[k9-ui~='text']");
			return (text != null) ? text : e;
		}
	}

	/**
	 * Provides a text-edit option for the context menu
	 */
	return function(event, cm) {
		
		const selectedElements = hasLastSelected(selector());
		
		if (selectedElements.length > 0) {
		
			var htmlElement = cm.get(event);
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			
			img.setAttribute("title", "Edit Text");
			img.setAttribute("src", "/public/commands/edit/edit.svg");
			
			img.addEventListener("click", function(event) {
				const defaultText = defaultSelector(hasLastSelected(selectedElements, true)).textContent.trim();
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
