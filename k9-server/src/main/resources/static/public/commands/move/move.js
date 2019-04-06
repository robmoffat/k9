/**
 * This handles moving a block from one place to another on the diagram, via drag and drop.
 * You can't drop into an element unless it has 
 */
import { getChangeUri, parseInfo, isTerminator } from "/public/bundles/api.js";
import { getSVGCoords, getElementPageBBox } from '/public/bundles/screen.js';
import { getBeforeId } from '/public/bundles/ordering.js';

/**
 * Keeps track of any links we've animated moving.
 */
var moveLinks = [];

const SVG_PATH_REGEX = /[MLQTCSAZ][^MLQTCSAZ]*/gi;

export function createMoveDragableDropCallback(transition) {
	

	function createMoveCommand(drag, drop, evt, isDragable, dragTargets) {
		var beforeId = getBeforeId(drop, evt, dragTargets);
		if (!isTerminator(drag)) {
			return {
				type: 'Move',
				fragmentId: drop.getAttribute('id'),
				moveId: drag.getAttribute('id'),
				beforeFragmentId: beforeId
			}
		} else {
			return {
				type: 'SetAttr',
				fragmentId: drag.getAttribute('id'),
				name: 'reference',
				value: drop.getAttribute('id')
			}
		}	
	}
	
	return function(dragTargets, evt, okDrop, dropTarget, isDragable) {
		if (okDrop) {
			Array.from(dragTargets).forEach(dt => transition.push(createMoveCommand(dt, dropTarget, evt, isDragable, dragTargets)));
			moveLinks = [];
			return true;
		} else {
			// reset the links being moved.
			moveLinks.forEach(m => {
				m.setAttribute("d", m.getAttribute("d-old"));
				m.setAttributeNS(null, 'pointer-events', 'all');
			})
			moveLinks = [];
			console.log("Can't dropped in " + dropTarget.getAttribute("id"));
			return false;
		}
	}
}

/**
 * This shows the user where links will go.
 */
export function moveDragableMoveCallback(dragTargets, evt) {
	
	dragTargets.forEach(dt => {
		if (isTerminator(dt)) {
			const debug = parseInfo(dt);
			const coords = getSVGCoords(evt);
			
			const id = debug.terminates;
			const linkElem = document.getElementById(id);
			
			const path = linkElem.querySelector("[k9-indicator=path] path");
			if (path) {
				
				const d = path.getAttribute("d");
				
				if (moveLinks.indexOf(path) == -1) {
					moveLinks.push(path);
					path.setAttribute("d-old", d);
				}
				
				path.setAttributeNS(null, 'pointer-events', 'none');
				
				const commands = d.match(SVG_PATH_REGEX);
				var from, to;
				if (debug.end == 'from') {
					from = 'M'+coords.x+" "+coords.y; 
					to = commands[commands.length-1];
				} else {
					to = 'L'+coords.x+" "+coords.y; 
					from = commands[0];
				}
				
				path.setAttribute("d", from+" "+to);
			}
		}
	});
}

export function initCompleteDragable(transition) {
	
	return function() {
		transition.postCommandList(getChangeUri());
	}
}





