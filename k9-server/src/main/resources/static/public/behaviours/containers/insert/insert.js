import { hasLastSelected } from '/public/bundles/api.js';
import { getMainSvg } from '/public/bundles/screen.js';
import { getBeforeId } from '/public/bundles/ordering.js';


function defaultInsertSelector() {
	return getMainSvg().querySelectorAll("[k9-ui~=insert].selected");
}

function defaultInsertableSelector(palettePanel) {
	return palettePanel.querySelectorAll("[id][k9-palette~=insert]");	
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
	
	return function(palette, palettePanel) {
		
		function getElementUri(e) {
			var paletteId = palettePanel.getAttribute("id");
			var id = e.getAttribute("id");
			return palettePanel.getAttribute("k9-palette-uri")+".xml#"+id.substring(0, id.length - paletteId.length);	
		}

		function createInsertStep(e, drop) {
			const beforeId = getBeforeId(e, palette.getOpenEvent(), []);
			
			return {
				"type": 'Copy',
				"fragmentId": e.getAttribute('id'),
				"uriStr": getElementUri(drop),
				"beforeFragmentId" : beforeId,
				"deep" : true
			}
		}
	
		function click(event) {
			if (palette.getCurrentSelector() == 'insert') {
				const selectedElements = insertSelector();
				const lastSelectedElement = hasLastSelected(selectedElements);
				const droppingElement = palette.get().querySelector("[id].mouseover")
				const data = Array.from(selectedElements).map(e => createInsertStep(e, droppingElement));
				palette.destroy();		
				transition.postCommands(data);
				event.stopPropagation();
			}
		}
	
		insertableSelector(palettePanel).forEach(function(v) {
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
			img.setAttribute("src", "/public/behaviours/containers/insert/insert.svg");
			img.addEventListener("click", function(e2, selector) {
				contextMenu.destroy();
				palette.open(event, "insert");
			});
		}
	}
}



