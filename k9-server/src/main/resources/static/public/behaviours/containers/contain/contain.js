import { hasLastSelected, createUniqueId } from '/public/bundles/api.js';
import { getMainSvg } from '/public/bundles/screen.js';
import { getBeforeId } from '/public/bundles/ordering.js';


function defaultContainSelector() {
	return getMainSvg().querySelectorAll("[k9-ui~=insert].selected");
}

function defaultContainableSelector(palettePanel) {
	return palettePanel.querySelectorAll("[id][k9-palette~=contain]");	
}

/**
 * Provides functionality so that when the user clicks on a 
 * palette element it is inserted into the document.
 */
export function initContainPaletteCallback(transition, containableSelector, containSelector) {
	
	if (containableSelector == undefined) {
		containableSelector = defaultContainableSelector;
	}
	
	if (containSelector == undefined) {
		containSelector = defaultContainSelector;
	}
	
	return function(palette, palettePanel) {
		
		function getElementUri(e) {
			var paletteId = palettePanel.getAttribute("id");
			var id = e.getAttribute("id");
			return palettePanel.getAttribute("k9-palette-uri")+".xml#"+id.substring(0, id.length - paletteId.length);	
		}

		function createInsertStep(e, drop, newId) {
			return {
				"type": 'Copy',
				"uriStr": getElementUri(drop),
				"beforeFragmentId" : e.getAttribute("id"),
				"newId": newId,
				"deep" : true
			}
		}
		
		function createContainStep(e, toId) {
			return {
				"type": 'Move',
				"fragmentId": toId,
				"moveId" : e.getAttribute('id'),
			}
		}
	
		function click(event) {
			if (palette.getCurrentSelector() == 'contain') {
				// create the container element
				const droppingElement = palette.get().querySelector("[id].mouseover");
				const newId = createUniqueId();
				const selectedElements = containSelector();
				const lastElement = hasLastSelected(selectedElements, true);
				transition.push(createInsertStep(lastElement, droppingElement, newId));
				
				// now move everything else into it
				Array.from(selectedElements).forEach(e => transition.push(createContainStep(e, newId)));
				palette.destroy();		
				transition.postCommandList();
				event.stopPropagation();
			}
		}
	
		containableSelector(palettePanel).forEach(function(v) {
	    	v.removeEventListener("click", click);
	    	v.addEventListener("click", click);
		})
	}
}
	
/**
 * Adds contain option into context menu
 */
export function initContainContextMenuCallback(palette, selector) {
	
	if (selector == undefined) {
		selector = defaultContainSelector;
	}
	
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const selectedElements = hasLastSelected(selector());
		
		if (selectedElements.length > 0) {
			contextMenu.addControl(event, "/public/behaviours/containers/contain/contain.svg", "Contain", 
				function(e2) {
					contextMenu.destroy();
					palette.open(event, "contain");
				});
		}
	}
}



