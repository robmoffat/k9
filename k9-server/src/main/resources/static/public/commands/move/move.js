/**
 * This handles moving a block from one place to another on the diagram, via drag and drop.
 * You can't drop into an element unless it has 
 */
import { registerDragableDropCallback, registerDragableMoveCallback, getDropTarget, dragAttribute, canDropAllHere } from "/public/behaviours/dragable/dragable.js";
import { transition, postCommands } from "/public/bundles/transition.js"
import { getChangeUri, parseInfo } from "/public/bundles/api.js";
import { getTrueCoords } from '/public/bundles/screen.js';


function isTerminator(debug) {
	return debug.terminates != undefined;
}

function createMoveCommand(drag, drop) {
	if (!isTerminator(parseInfo(drag))) {
		return {
			type: 'Move',
			fragmentId: drop.getAttribute('id'),
			moveId: drag.getAttribute('id')
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

/**
 * Keeps track of any links we've animated moving.
 */
var moveLinks = [];

registerDragableDropCallback(function(dragTargets, evt) {
	var dropTarget = getDropTarget(evt.target);
	
	if (canDropAllHere(dragTargets, dropTarget)) {
		const commands = Array.from(dragTargets).map(dt => createMoveCommand(dt, dropTarget));
		postCommands(commands, getChangeUri());
		moveLinks = [];
		return true;
	} else {
		moveLinks.forEach(m => {
			m.setAttribute("d", m.getAttribute("d-old"));
			m.setAttributeNS(null, 'pointer-events', 'all');
		})
		moveLinks = [];
		console.log("Can't dropped in " + dropTarget.id);
		return false;
	}
});

/*
 * This shows the user where the elements will be dropped, within a layout container.
 */
registerDragableMoveCallback(function(dragTargets, evt) {
	
	
	
	
});

const SVG_PATH_REGEX = /[MLQTCSAZ][^MLQTCSAZ]*/gi;



/*
 * This shows the user where links will go.
 */
registerDragableMoveCallback(function(dragTargets, evt) {
	
	dragTargets.forEach(dt => {
		const debug = parseInfo(dt);
		if (isTerminator(debug)) {
			const coords = getTrueCoords(evt);
			
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

});





