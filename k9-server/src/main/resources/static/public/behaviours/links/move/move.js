/**
 * This handles moving a block from one place to another on the diagram, via drag and drop.
 * You can't drop into an element unless it has 
 */
import { parseInfo, isTerminator } from "/public/bundles/api.js";
import { getSVGCoords, getElementPageBBox } from '/public/bundles/screen.js';
import { getBeforeId } from '/public/bundles/ordering.js';

/**
 * Keeps track of any links we've animated moving.
 */
var moveLinks = [];

const SVG_PATH_REGEX = /[MLQTCSAZ][^MLQTCSAZ]*/gi;

export function initTerminatorDropCallback(transition) {
	

	return function(dragTargets, evt, dropTargets) {
		if (dropTargets.length == 1) {
			Array.from(dragTargets).forEach(dt => {
				if (isTerminator(dt)) {
					transition.push(  {
						type: 'SetAttr',
						fragmentId: dt.getAttribute('id'),
						name: 'reference',
						value: dropTargets[0].getAttribute('id')
					});
				}	
			});
			return true;
		} 
	}
}



/**
 * This shows the user where links will go.
 */
export function initTerminatorMoveCallback() {

	return function(dragTargets, evt) {
	
		dragTargets.forEach(dt => {
			if (isTerminator(dt)) {
				const debug = parseInfo(dt);
				const coords = getSVGCoords(evt);
				
				const id = debug.terminates;
				const linkElem = document.getElementById(id);
				
				const paths = linkElem.querySelectorAll("[k9-animate=link]");
				Array.from(paths).forEach(path => {
					
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
				});
			}
		});
	}	
}




