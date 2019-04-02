import { SHA1 } from "/public/bundles/sha1.js";
import { getChangeUri, getContainingDiagram, createUniqueId, getExistingConnections, parseInfo } from "/public/bundles/api.js";
import { getMainSvg, getElementPageBBox } from '/public/bundles/screen.js';


export function initAlignContextMenuCallback(transition, templateUri, selector) {
	
	/**
	 * Aligns the two elements
	 */
	function createAlignStep(from, to, direction, steps ,linkId) {
		
		const conns = getExistingConnections(from.getAttribute("id"), to.getAttribute("id"));
		var toUseId = null;
		
		// tidy up any existing connections between these elements.
		conns.forEach(c => {
			const alignOnly = c.classList.contains("align");
			
			if (alignOnly) {
				// remove the old alignment
				steps.push({
					type: 'ADLDelete',
					fragmentId: c.getAttribute("id"),
					cascade: true
				});
			} else {
				const debug = parseInfo(c);
				const direction = debug.direction;

				if (direction != 'null') {
					steps.push({
						fragmentId: c.getAttribute("id"),
						type: 'SetAttr',
						name: 'drawDirection',
						value: null
					})	
				}
				
				if (toUseId == null) {
					toUseId = c.getAttribute("id");
				}
			}
		})
		
		if (toUseId == null) {
			// create an align element
			steps.push({
				fragmentId: getContainingDiagram(from).getAttribute("id"),
				type: 'CopyLink',
				linkId: linkId,
				fromId: from.getAttribute("id"),
				toId: to.getAttribute("id"),
				uriStr: templateUri
			});
		} else {
			linkId = toUseId;
		}
				
		steps.push({
			fragmentId: linkId,
			type: 'SetAttr',
			name: 'drawDirection',
			value: direction
		});
		
		return linkId;
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
		const linkId = createUniqueId();
		
		for (var i = 0; i < selectedElements.length-1; i++) {
			var from = selectedElements[i];
			var to = selectedElements[i+1];
			createAlignStep(from, to, horiz ? "RIGHT" : "DOWN", steps, linkId+"-"+i);
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
		
		const e = document.querySelectorAll("div.main svg [id][align*='yes'].selected");
		
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

