import { parseInfo, getChangeUri, getContainingDiagram, getAlignElementsAndDirections } from '/public/bundles/api.js';
import { getMainSvg } from '/public/bundles/screen.js';

export function initLinkContextMenuCallback(transition, linker) {
	
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const e = document.querySelector("[id].lastSelected.selected");
		const debug = parseInfo(e);
		
		if (debug.connect) {
			var htmlElement = contextMenu.get(event);
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Draw Link");
			img.setAttribute("src", "/public/commands/link/link.svg");
			const elements = document.querySelectorAll("div.main [id][k9-info~='connect:'].selected");
			img.addEventListener("click", e => {
				contextMenu.destroy();
				linker.start(Array.from(elements), e);
			});
		}
	};
}

var templateElement;
var templateUri;

export function initLinkPaletteCallback() {
	
	
	return function(palette, event) {

		function getElementUri(e) {
			var paletteId = palette.getId();
			var id = e.getAttribute("id");
			return palette.getUri()+".xml#"+id.substring(0, id.length - paletteId.length);	
		}
		
		function click(elem, event) {
			templateElement.classList.remove("selected");
			templateElement = elem;
			templateElement.classList.add("selected");
			templateUri = getElementUri(elem);
			palette.destroy();		
			event.stopPropagation();
		}
	
		palette.get().querySelectorAll("[id][k9-info~='link:']").forEach(function(v) {
	    	v.removeEventListener("click", (e) => click(v, e));
	    	v.addEventListener("click", (e) => click(v, e));
	    
	    	if (v.classList.contains("default")) {
	    		var id = v.getAttribute("id");
	    		templateUri = getElementUri(v);
	    		templateElement = v;
				templateElement.classList.add("selected");
	    	}
		})
		
	}
}

export function selectedLink() {
	return templateElement;
}

export function linkTemplateUri() {
	return templateUri;	
}
	

export function initLinkInstrumentationCallback(linkPalette) {
	
	return function(nav) {
		const name = 'linkmenu';
		var b = nav.querySelector("."+name);
		if (b == undefined) {
			var b = document.createElement("img");
			b.setAttribute("class", name);
			b.setAttribute("title", "Link Menu");
			b.setAttribute("src", "/public/commands/link/"+name+".svg");
			b.style.backgroundColor = '#EEEEEE';
			b.addEventListener("click", (evt) => linkPalette.open(evt));
		    nav.appendChild(b);
		}
	}	
}

export function initLinkLinkerCallback(transition, linkTemplateUri) {
	
	if (linkTemplateUri == undefined) {
		linkTemplateUri = function() {
			const template = getMainSvg().querySelector("div.main [k9-info*=link][id]");
			return "#"+template.getAttribute("id");
		}
	} 
	
	return function(linker, evt) {
		const linkTarget = linker.getLinkTarget(evt.target);
		const diagramId = getContainingDiagram(linkTarget).getAttribute("id");
		const linkTargetId = linkTarget.getAttribute("id");
		
		if (linkTarget == null) {
			linker.removeDrawingLinks();
		} else {
			linker.get().forEach(e => {
				var fromId = e.getAttribute("temp-from");
				var aligns = getAlignElementsAndDirections(fromId, linkTargetId);
				var linkId = e.getAttribute("id");
				
				transition.push({
					type: "CopyLink",
					fragmentId: diagramId,
					uriStr: linkTemplateUri(),
					fromId: fromId,
					toId: linkTargetId,
					linkId: linkId
				});
				
				/*
				 * If there is an align element, remove it and set the draw direction.
				 */
				
				if (aligns.length == 1) {
					var { element, direction } = aligns[0];
					transition.push({
						type: 'ADLDelete',
						fragmentId: element.getAttribute("id"),
						cascade: true
					});
					transition.push({
						type: 'SetAttr',
						fragmentId: linkId,
						name: 'drawDirection',
						value: direction
					})
				}
				
			});
			
			transition.postCommandList(getChangeUri());
			linker.clear();
		}
	};
	
}

