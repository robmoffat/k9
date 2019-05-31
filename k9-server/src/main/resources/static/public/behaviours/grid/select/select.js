import { hasLastSelected, isGridRowSelected, isGridColumnSelected, getParentElement } from '/public/bundles/api.js';


function initSelectContextMenuCallback(transition, overlay, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='grid'].selected")
		}
	}
	
	function performSelect(cm, event, horiz, elements) {
		elements.forEach(e => {
			const info = parseInfo(e);
			const range = horiz ? info['grid-y'] : info['grid-x'];
			const container = e.parentElement();
			
		});
	}
	
	/**
	 * Provides overlays for selecting rows, columns
	 */
	return function(event, cm) {
		
		const e = hasLastSelected(selector());
		
		if (e.length > 0) {
			var htmlElement = cm.get(event);
			
			if (!isGridColumnSelected(e)) {
				// add vertical selection
				var img = document.createElement("img");
				htmlElement.appendChild(img);
				img.setAttribute("title", "Select Column");
				img.setAttribute("src", "/public/behaviours/grid/select/vertical.svg");
				img.addEventListener("click", () => performSelect(cm, event, false, e));
				
			}
			
			if (!isGridRowSelected(e)) {
				// add horizontal selection
				var img = document.createElement("img");
				htmlElement.appendChild(img);
				img.setAttribute("title", "Select Row");
				img.setAttribute("src", "/public/behaviours/grid/select/horizontal.svg");
				img.addEventListener("click", () => performSelect(cm, event, true, e));
			}
		}
	}
	
	
}