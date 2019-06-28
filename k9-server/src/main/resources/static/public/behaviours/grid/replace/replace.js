import { getMainSvg } from '/public/bundles/screen.js';
import { createUniqueId, parseInfo, getKite9Target } from '/public/bundles/api.js';
/**
 * Allows you to select both cells and temporary grid elements.
 */
export function replaceCellSelector() {
	return getMainSvg().querySelectorAll("[k9-ui~=cell].selected, [k9-info*='layout: GRID'] > .grid-temporary.selected");
}

function initDefaultReplaceChoiceSelector(type) {
	return function(palettePanel) {
		return palettePanel.querySelectorAll("[id][k9-palette~="+type+"]");	
	}
}

export function initGridReplacePaletteCallback(transition, type, replaceChoiceSelector, replaceSelector) {
	
	if (replaceChoiceSelector == undefined) {
		replaceChoiceSelector = initDefaultReplaceChoiceSelector(type);
	}
	
	
	if (replaceSelector == undefined) {
		replaceSelector = replaceCellSelector;
	}
	
	function createReplaceStep (transition, e, drop, palettePanel) {	
		const paletteId = palettePanel.getAttribute("id");
		const id = drop.getAttribute("id");
		const uri = palettePanel.getAttribute("k9-palette-uri")+".xml#"+id.substring(0, id.length - paletteId.length);	

		if (e.classList.contains("grid-temporary")) {
			
			const newId = createUniqueId();
			const parent = getKite9Target(e.parentElement);
			const rectInfo = parseInfo(e);
			const newOccupies = rectInfo["grid-x"][0]+ " " + rectInfo["grid-x"][1] + " " +
				rectInfo["grid-y"][0]+ " " + rectInfo["grid-y"][1] + " ";
			
			transition.push({
				"type": 'Copy',
				"fragmentId": parent.getAttribute('id'),
				"uriStr": uri,
				"newId": newId
			});
			
			transition.push({
				"type" : "SetStyle",
				"fragmentId" : newId,
				"name" : "kite9-occupies",
				"value" : newOccupies
			})
		} else {
			// normal replace
			transition.push({
				"type": 'ADLReplace',
				"fragmentId": e.getAttribute('id'),
				"uriStr": uri,
				"approach": 'SHALLOW', 
				"keptAttributes": ['id', 'style']
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
	
