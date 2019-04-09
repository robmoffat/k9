import { getChangeUri, hasLastSelected } from '/public/bundles/api.js';
import { getMainSvg } from '/public/bundles/screen.js';

/**
 * For replace to be generic, we need to have parameters to say which attributes to replace (or not)
 * and whether we are replacing contents.
 */

function initDefaultReplaceSelector(type) {
	return function() {
		return getMainSvg().querySelectorAll("[k9-ui~="+type+"].selected");
	}
}

function initDefaultReplaceChoiceSelector(type) {
	return function(palettePanel) {
		return palettePanel.querySelectorAll("[id][k9-palette~="+type+"]");	
	}
}


export function initReplacePaletteCallback(transition, type, rules, replaceChoiceSelector, replaceSelector) {
	
	if (replaceChoiceSelector == undefined) {
		replaceChoiceSelector = initDefaultReplaceChoiceSelector(type);
	}
	
	if (replaceSelector == undefined) {
		replaceSelector = initDefaultReplaceSelector(type);
	}
	
	return function(palette, palettePanel) {
		
		function getElementUri(e) {
			var paletteId = palettePanel.getAttribute("id");
			var id = e.getAttribute("id");
			return palettePanel.getAttribute("k9-palette-uri")+".xml#"+id.substring(0, id.length - paletteId.length);	
		}

		function createReplaceStep(e, drop) {			
			return {
				"type": 'ADLReplace',
				"fragmentId": e.getAttribute('id'),
				"uriStr": getElementUri(drop),
				...rules
			}
		}
	
		function click(event) {
			if (palette.getCurrentSelector() == type) {
				const selectedElements = replaceSelector();
				const droppingElement = palette.get().querySelector("[id].mouseover")
				const data = Array.from(selectedElements).map(e => createReplaceStep(e, droppingElement));
				palette.destroy();		
				transition.postCommands(data, getChangeUri());
				event.stopPropagation();
			}
		}
	
		replaceChoiceSelector(palettePanel).forEach(function(v) {
	    	v.removeEventListener("click", click);
	    	v.addEventListener("click", click);
		})
	}
	
	
	
}



/**
 * Adds insert option into context menu
 */
export function initReplaceContextMenuCallback(palette, type, selector) {
	
	if (selector == undefined) {
		selector = initDefaultReplaceSelector(type);
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
			
			img.setAttribute("title", "Replace");
			img.setAttribute("src", "/public/commands/replace/replace.svg");
			img.addEventListener("click", function(e2, selector) {
				contextMenu.destroy();
				palette.open(event, type);
			});
		}
	}
}



