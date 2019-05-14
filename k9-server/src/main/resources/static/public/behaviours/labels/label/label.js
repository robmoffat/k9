import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection } from '/public/bundles/api.js';

export function initLabelContextMenuCallback(transition, templateUri, selector) {
	
	if (selector == undefined) {
		selector = function () {
			const labelables = Array.from(getMainSvg().querySelectorAll("[k9-ui~=label].selected"));
			
			// we need to exclude elements that already have labels.
			return labelables.filter(e => e.querySelectorAll("[k9-elem=label]").length ==0);
		}
	}
	
	function createInsertLabelStep(parent) {
		return {
			"type": 'Copy',
			"fragmentId": parent.getAttribute('id'),
			"uriStr": templateUri
		}
	}
	
	
	/**
	 * Provides a label option for the context menu
	 */
	return function(event, contextMenu) {
		
		const selectedElements = hasLastSelected(selector());
		
		if (selectedElements.length > 0) {
		
			var htmlElement = contextMenu.get(event);
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			
			img.setAttribute("title", "Add Label");
			img.setAttribute("src", "/public/behaviours/labels/label/label.svg");
			img.addEventListener("click", function(e2, selector) {
				contextMenu.destroy();
				selectedElements.forEach(e => transition.push(createInsertLabelStep(e)));
				transition.postCommandList();
			});
		}
	}
}