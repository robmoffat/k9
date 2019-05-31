
function initInsertContextMenuCallback(transition, overlay, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='grid'].selected")
		}
	}
	
	/**
	 * Provides overlays for adding rows, columns
	 */
	return function(event, cm) {
		
		const e = hasLastSelected(selector());
		
		if (e.length > 0) {
			var htmlElement = cm.get(event);
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Delete");
			img.setAttribute("src", "/public/behaviours/selectable/delete/delete.svg");
			img.addEventListener("click", () => performDelete(cm));
		}
	}
	
	
}