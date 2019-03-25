import { parseInfo, getChangeUri } from '/public/bundles/api.js';
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
			const elements = document.querySelectorAll("[id].selected");
			img.addEventListener("click", e => {
				contextMenu.destroy();
				linker.start(Array.from(elements), e);
			});
		}
	};
}

export function initLinkLinkerCallback(transition, linkTemplateUri) {
	
	if (linkTemplateUri == undefined) {
		linkTemplateUri = function(svg) {
			const template = this.svg.querySelector("div.main [k9-info*=link][id]");
			return "#"+template.getAttribute("id");
		}
	} 
	
	return function(linker, evt) {
		const linkTarget = linker.getLinkTarget(evt.target);
		const diagramId = linker.getContainingDiagram(linkTarget).getAttribute("id");
		const linkTargetId = linkTarget.getAttribute("id");
		
		if (linkTarget == null) {
			linker.removeDrawingLinks();
		} else {
			const commands = linker.get().map(e => 
				{ return {
					type: "CreateLink",
					fragmentId: diagramId,
					uriStr: linkTemplateUri(),
					fromId: e.getAttribute("temp-from"),
					toId: linkTargetId,
					linkId: e.getAttribute("id")
				}});
			
			transition.postCommands(commands, getChangeUri());
			linker.clear();
		}
	};
	
}

