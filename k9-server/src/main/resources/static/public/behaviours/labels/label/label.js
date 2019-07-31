import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection, createUniqueId } from '/public/bundles/api.js';

export function createInsertContainerLabelStep(e, templateUri, transition) {
	transition.push({
		"type": 'Copy',
		"fragmentId": e.getAttribute('id'),
		"uriStr": templateUri,
		"deep" : true
	});
}

export function unlabelledContainerSelector() {
	const labelables = Array.from(getMainSvg().querySelectorAll("[k9-ui~=label][k9-info*=connected].selected"));
	
	// we need to exclude elements that already have labels.
	return labelables.filter(e => 
		Array.from(e.children)
			.map(e => e.getAttribute("k9-elem"))
			.filter(s => s == "label")
			.length ==0);
}

export function unlabelledLinkEndSelector() {
	const labelables = Array.from(getMainSvg().querySelectorAll("[k9-ui~=label][k9-info*=terminator].selected"));
	
	return labelables.filter(e => {
		const info = parseInfo(e);
		const end = info.end;
		const linkId = info.terminates;
		const link = getMainSvg().getElementById(linkId);
		
		const label = link.querySelector("[k9-elem=label][end="+end+"]");
		
		return label == undefined;
	});
}

export function createInsertLinkLabelStep(e, templateUri, transition) {
	const info = parseInfo(e);
	const end = info.end;
	const linkId = info.terminates;
	const newId = createUniqueId();
	
	transition.push({
		"type": 'Copy',
		"fragmentId": linkId,
		"uriStr": templateUri,
		"newId" : newId,
		"deep" : true
	});
	
	transition.push({
		"type": "SetAttr",
		"fragmentId": newId,
		"name": "end",
		"value" : end
	});
}


export function initLabelContextMenuCallback(transition, templateUri, selector, action) {
	
	/**
	 * Provides a label option for the context menu
	 */
	return function(event, contextMenu) {
		
		const selectedElements = hasLastSelected(selector());
		
		if (selectedElements.length > 0) {
		
			var htmlElement = contextMenu.get(event);
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			
			img.setAttribute("title", "Add Label");
			img.setAttribute("src", "/public/behaviours/labels/label/label.svg");
			img.addEventListener("click", function(e2, selector) {
				contextMenu.destroy();
				selectedElements.forEach(e => action(e, templateUri, transition));
				transition.postCommandList();
			});
		}
	}
}

export function initContainerLabelContextMenuCallback(transition, templateUri, selector, action) {
	if (selector == undefined) {
		selector = unlabelledContainerSelector;
	}
	
	if (action == undefined) {
		action = createInsertContainerLabelStep;
	}

	return initLabelContextMenuCallback(transition, templateUri, selector, action);
	
}

export function initLinkLabelContextMenuCallback(transition, templateUri, selector, action) {
	if (selector == undefined) {
		selector = unlabelledLinkEndSelector;
	}
	
	if (action == undefined) {
		action = createInsertLinkLabelStep;
	}

	return initLabelContextMenuCallback(transition, templateUri, selector, action);
	
}