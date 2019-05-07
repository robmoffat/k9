import { getContainingDiagram, createUniqueId, getExistingConnections, parseInfo, hasLastSelected, reverseDirection } from "/public/bundles/api.js";
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
				const existingDirection = debug.direction;

				if (existingDirection != 'null') {
					steps.push({
						fragmentId: c.getAttribute("id"),
						type: 'SetAttr',
						name: 'drawDirection',
						value: null
					})	
				}
				
				if (toUseId == null) {
					toUseId = c.getAttribute("id");
					// check to see if we need to reverse the align
					const parsed = parseInfo(c);
					const link = parsed['link'];
					const ids = link.split(" ");
					const reversed = ids[0] == to.getAttribute("id");
					direction = reversed ? reverseDirection(direction) : direction;
				}
			}
		})
		
		if (toUseId == null) {
			// create an align element
			steps.push({
				fragmentId: getContainingDiagram(from).getAttribute("id"),
				type: 'CopyLink',
				newId: linkId,
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
		var selectedElements = Array.from(selector());
		
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
		transition.postCommands(steps);
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
		
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[id][k9-ui~='align'].selected");
		}
	}
	
	/**
	 * Provides an align option for the context menu
	 */
	return function(event, cm) {
		
		const e = hasLastSelected(selector());
		
		if (e.length > 1) {
			var htmlElement = cm.get(event);
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Horizontal Align");
			img.setAttribute("src", "/public/behaviours/links/align/align-horiz.svg");
			img.addEventListener("click", () => performAlign(cm, true));
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Vertical Align");
			img.setAttribute("src", "/public/behaviours/links/align/align-vert.svg");
			img.addEventListener("click", () => performAlign(cm, false));
		}
	}
}


