import { hasLastSelected } from '/public/bundles/api.js';
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


export function initReplacePaletteCallback(transition, type, rules, replaceChoiceSelector, replaceSelector, createReplaceStep) {
	
	if (replaceChoiceSelector == undefined) {
		replaceChoiceSelector = initDefaultReplaceChoiceSelector(type);
	}
	
	if (replaceSelector == undefined) {
		replaceSelector = initDefaultReplaceSelector(type);
	}
	
	if (createReplaceStep == undefined) {
		
		
		createReplaceStep = function(transition, e, drop, palettePanel) {			
			const paletteId = palettePanel.getAttribute("id");
			const id = drop.getAttribute("id");
			const uri = palettePanel.getAttribute("k9-palette-uri")+".xml#"+id.substring(0, id.length - paletteId.length);	
			
			transition.push({
				"type": 'ADLReplace',
				"fragmentId": e.getAttribute('id'),
				"uriStr": uri,
				...rules
			});
		}
	}
	
	return function(palette, palettePanel) {
		function click(event) {
			if (palette.getCurrentSelector() == type) {
				const selectedElements = replaceSelector();
				const droppingElement = palette.get().querySelector("[id].mouseover")
				Array.from(selectedElements).forEach(e => createReplaceStep(transition, e, droppingElement, palettePanel));
				palette.destroy();		
				transition.postCommandList();
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
			contextMenu.addControl(event, "/public/behaviours/selectable/replace/replace.svg",
					"Replace", 
					function(e2, selector) {
				contextMenu.destroy();
				palette.open(event, type);
			});
		}
	}
}



