/**
 * This handles moving a block from one place to another on the diagram, via drag and drop.
 * You can't drop into an element unless it has 
 */
import { getChangeUri, parseInfo } from "/public/bundles/api.js";
import { getSVGCoords } from '/public/bundles/screen.js';

/**
 * Keeps track of any links we've animated moving.
 */
var moveLinks = [];

const SVG_PATH_REGEX = /[MLQTCSAZ][^MLQTCSAZ]*/gi;


function isTerminator(debug) {
	return debug.terminates != undefined;
}

export function createMoveDragableDropCallback(transition) {
	
	function prepareSort(drop, isDragable, boolean horiz) {
		return Array.from(drop.children)
			.filter(e => isDragable(e))
			.map(e => {
				const box = getElementPageBBox(e);
				const pos = { 
						p: horiz ? (box.x + box.width) : (box.y + box.height),
						e: e };
			});
	}
	
	function getBeforeId(drop, evt, isDragable) {
		const info = parseInfo(drop);
		const layout = info.layout;
		
		const pos = getSVGCoords(evt);
		
		if (layout == 'null') {
			return null;
		}
		
		switch(layout) {
		  case 'null':
		  case 'HORIZONTAL':
		  case 'VERTICAL':
		   	return null;
		  case 'LEFT':
		    return prepareSort(drop, isDragable).map(pos => )
		  case 'RIGHT:
		  
		  case 'UP'
		  
		  case 'DOWN':
			  
		    break;
		  default:
		    // code block
		}
	}
	
	function createMoveCommand(drag, drop, evt, isDragable) {
		if (!isTerminator(parseInfo(drag))) {
			return {
				type: 'Move',
				fragmentId: drop.getAttribute('id'),
				moveId: drag.getAttribute('id'),
				beforeId: getBeforeId(drop, evt, isDragable)
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
			Array.from(dragTargets).forEach(dt => transition.push(createMoveCommand(dt, dropTarget, evt, isDragable)));
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
		const debug = parseInfo(dt);
		if (isTerminator(debug)) {
			const coords = getSVGCoords(evt);
			
			const id = debug.terminates;
			const linkElem = document.getElementById(id);
			
			const path = linkElem.querySelector(".indicator-path path");
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





