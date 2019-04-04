import { getChangeUri, hasLastSelected } from '/public/bundles/api.js';
import { getMainSvg } from '/public/bundles/screen.js';

function defaultInsertSelector() {
	return getMainSvg().querySelectorAll("[k9-ui~=insert].selected");
}

function defaultInsertableSelector(palette) {
	return palette.get().querySelectorAll("[id][k9-palette~=insertable]");	
}


/**
 * Provides functionality so that when the user clicks on a 
 * palette element it is inserted into the document.
 */
export function initInsertPaletteCallback(transition, insertableSelector, insertSelector) {
	
	if (insertableSelector == undefined) {
		insertableSelector = defaultInsertableSelector;
	}
	
	if (insertSelector == undefined) {
		insertSelector = defaultInsertSelector;
	}
	
	return function(palette, event) {

		function createInsertStep(e, id) {
			return {
				"type": 'Copy',
				"fragmentId": e.getAttribute('id'),
				"uriStr": palette.getUri()+".xml#"+id.substring(0, id.length-palette.getId().length)
			}
		}
	
		function click(event) {
			const selectedElements = insertSelector();
			const droppingElement = palette.get().querySelector("[id].mouseover")
			const data = Array.from(selectedElements).map(e => createInsertStep(e, droppingElement.getAttribute("id")));
			palette.destroy();		
			transition.postCommands(data, getChangeUri());
			event.stopPropagation();
		}
	
		insertableSelector(palette).forEach(function(v) {
	    	v.removeEventListener("click", click);
	    	v.addEventListener("click", click);
		})
	}
}
	
/**
 * Adds insert option into context menu
 */
export function initInsertContextMenuCallback(palette, selector) {
	
	document.addEventListener('keydown', function(event) {
		if (event.key == 'Escape') {
			palette.destroy();
		}
	});
	
	if (selector == undefined) {
		selector = defaultInsertSelector;
	}
	
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const selectedElements = hasLastSelected(selector());
		
		if (selectedElements.length > 0) {
		
			var htmlElement = contextMenu.get(event);
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			
			img.setAttribute("title", "Insert");
			img.setAttribute("src", "/public/commands/insert/insert.svg");
			img.addEventListener("click", function(event, selector) {
				contextMenu.destroy();
				palette.open(event);
			});
		}
	}
}



