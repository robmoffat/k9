import { SHA1 } from "/public/bundles/sha1.js";
import { getChangeUri, getContainingDiagram, createUniqueId } from "/public/bundles/api.js";
import { getMainSvg, getElementPageBBox } from '/public/bundles/screen.js';


export function initAlignContextMenuCallback(transition, createLink, selector) {
	
	if (createLink == undefined) {
		createLink = function(d, fromId, toId) {
			return "<svg:svg xmlns:svg='http://www.w3.org/2000/svg'><g xmlns='http://www.kite9.org/schema/adl' id='"+createUniqueId()+"' class='link invisible' drawDirection='"+d+"'>"+
				"<from reference='"+fromId+"' />"+
				"<to reference='"+toId+"' />"+
				"</g></svg:svg>";
		}
	}
	
	/**
	 * 
	 * Takes a node and creates a delete command.
	 */
	function createAlignStep(from, to, direction) {
		return {
			fragmentId: getContainingDiagram(from).getAttribute("id"),
			type: 'AppendXML',
			newState: createLink(direction, from.getAttribute("id"), to.getAttribute("id"))
		};
	}

	function performAlign(cm, horiz) {
		var selectedElements = Array.from(document.querySelectorAll("div.main svg [id][k9-info*='connect:'].selected"));
		
		selectedElements.sort((a, b) => {
			var apos = getElementPageBBox(a);
			var bpos = getElementPageBBox(b);
			
			if (horiz) {
				var d = (apos.x + (apos.width / 2)) - ( bpos.x + (bpos.width / 2))
			} else {
				var d = (apos.y + (apos.height / 2)) - ( bpos.y + (bpos.height / 2))
			}
			
			return d;
		});
		
		var steps = [];
		
		for (var i = 0; i < selectedElements.length-1; i++) {
			var from = selectedElements[i];
			var to = selectedElements[i+1];
			steps.push(createAlignStep(from, to, horiz ? "RIGHT" : "DOWN"));
		}
		
		cm.destroy();
		transition.postCommands(steps, getChangeUri());
	}

	/**
	 * This should only be called once.  Adds the delete-key shortcut.
	 */
	document.addEventListener('keydown', function(event) {
		if (event.key == 'h') {
		    performAlign(true);
		}
		
		if (event.key == 'v') {
			performAlign(false);
		}
	});
		
	/**
	 * Provides a delete option for the context menu
	 */
	return function(event, cm) {
		
		const e = document.querySelectorAll("div.main svg [id][k9-info*='connect:'].selected");
		
		if (e.length > 1) {
			var htmlElement = cm.get(event);
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Horizontal Align");
			img.setAttribute("src", "/public/commands/align/align-horiz.svg");
			img.addEventListener("click", () => performAlign(cm, true));
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Vertical Align");
			img.setAttribute("src", "/public/commands/align/align-vert.svg");
			img.addEventListener("click", () => performAlign(cm, false));
		}
	}
}


