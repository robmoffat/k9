import { getChangeUri } from '/public/bundles/api.js';

/**
 * Provides functionality so that when the user clicks on a 
 * palette element it is inserted into the document.
 */
export function initInsertPaletteCallback(transition) {
	
	return function(palette, event) {

	
		function createInsertStep(e, id) {
			return {
				"type": 'Copy',
				"fragmentId": e.getAttribute('id'),
				"uriStr": palette.getUri()+".xml#"+id
			}
		}
	
		function click(event) {
			const selectedElements = document.querySelectorAll("[id][k9-info~='layout:'].selected");
			const droppingElement = document.querySelector("[id].mouseover")
			const data = Array.from(selectedElements).map(e => createInsertStep(e, droppingElement.getAttribute("id")));
			palette.destroy();		
			transition.postCommands(data, getChangeUri());
			event.stopPropagation();
		}
	
		palette.get().querySelectorAll("[id][k9-elem]").forEach(function(v) {
	    	v.removeEventListener("click", click);
	    	v.addEventListener("click", click);
		})
	}
}
	
/**
 * Adds insert option into context menu
 */
export function initInsertContextMenuCallback(palette) {
	
	document.addEventListener('keydown', function(event) {
		if (event.key == 'Escape') {
			palette.destroy();
		}
	});
	
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const selectedElements = document.querySelectorAll("[id][k9-info~='layout:'].lastSelected.selected");
		
		if (selectedElements.length > 0) {
		
			var htmlElement = contextMenu.get(event);
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			
			img.setAttribute("title", "Insert");
			img.setAttribute("src", "/public/commands/insert/insert.svg");
			img.addEventListener("click", function(event) {
				contextMenu.destroy();
				palette.open(event);
			});
		}
	}
}



