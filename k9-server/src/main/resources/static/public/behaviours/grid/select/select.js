import { hasLastSelected, getParentElement, parseInfo } from '/public/bundles/api.js';


export function initSelectContextMenuCallback(selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='cell'].selected")
		}
	}
	
	function performSelect(cm, event, horiz, elements) {
		
		function intersects(r1, r2) {
			const startIn = (r1[0] >= r2[0]) && (r1[0] < r2[1]);
			const endIn = (r1[1] > r2[0]) && (r1[1] <= r2[1]);
			return startIn || endIn;
		}
		
		elements.forEach(e => {
			const info = parseInfo(e);
			const range = horiz ? info['grid-y'] : info['grid-x'];
			const container = e.parentElement;
			
			Array.from(container.children).forEach(f => {
				const details = parseInfo(f);
				if ((details != null) && details['grid-x']) {
					const intersect = horiz ? intersects(details['grid-y'], range) :
						intersects(details['grid-x'], range);
				
					if (intersect) {
						f.classList.add("selected");
					}
				}
			});
		});
	}
	
	/**
	 * Provides overlays for selecting rows, columns
	 */
	return function(event, cm) {
		
		const e = hasLastSelected(selector());
		
		if (e.length > 0) {
			var htmlElement = cm.get(event);
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Select Column");
			img.setAttribute("src", "/public/behaviours/grid/select/vertical.svg");
			img.addEventListener("click", () => performSelect(cm, event, false, selector()));

			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Select Row");
			img.setAttribute("src", "/public/behaviours/grid/select/horizontal.svg");
			img.addEventListener("click", () => performSelect(cm, event, true, selector()));
		}
	}
	
	
}