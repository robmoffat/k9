import { parseInfo, getParentElement, isConnected, isDiagram, getContainerChildren } from '/public/bundles/api.js';
import { drawBar, getBefore, clearBar } from '/public/bundles/ordering.js';
import { getElementPageBBox, getSVGCoords } from '/public/bundles/screen.js';
import { getMainSvg } from '/public/bundles/screen.js';

function isGrid(e) {
	var out = e.getAttribute("k9-info");
	if (out.includes("layout: GRID;")) {
		return true;
	} 
	
	return false;
}

function isCell(e) {
	if (e.hasAttribute("k9-info")) {
		var out = e.getAttribute("k9-info");
		if (out.includes("grid-x")) {
			return true;
		} 
	}
	
	return false;
}

export function initCellDropCallback() {
	
	
}

/**
 * This will only allow you to drag cells.
 */
export function initCellDragLocator(selector) {
	
	if (selector == undefined) {
		selector = function() {
			return Array.from(document.querySelectorAll("[id][k9-ui~='cell'].selected, [id][k9-ui~='cell'].mouseover"))
		}
	}
	
	return function() {
		var out = selector();
		return out;
	}
}

export function initCellDropLocator() {
	
	const svg = getMainSvg();
	

	
	function dragIds(dragTargets) {
		return dragTargets
			.map(dt => dt.getAttribute("id"))
			.reduce((a,b) => a+","+b, "");
	}
	
	function canDropHere(dragTarget, dropTarget) {
		if (dropTarget == null) {
			return false;
		}
		
		if (dragTarget==dropTarget) {
			return false;
		}
		
		if (!isCell(dragTarget)) {
			return false;
		}
		
		if ((!isGrid(dropTarget)) && (!isCell(dropTarget))) {
			return false;
		}

		return true;
	};
	

	function getKite9GridTarget(v) {
		if (v.hasAttribute("k9-elem") && v.hasAttribute("id")) {
			return v;
		} else if (isCell(v)) {
			return v;
		} else if (v.tagName == 'svg') {
			return null;
		} else {
			return getKite9GridTarget(v.parentNode);
		}
	}

	return function(dragTargets, evt) {
		var dropTarget = getKite9GridTarget(evt.target);

		while (dropTarget) {
			const ok = dragTargets
				.map(dt => canDropHere(dt, dropTarget))
				.reduce((a,b) => a&&b, true);
		
			if (ok) {
				return [ dropTarget ];
			} else if (isConnected(dropTarget)) {
				dropTarget = getParentElement(dropTarget);
			} else {
				dropTarget = null;
			}
		}
	
		return [ ];
	}	
}

const LEFT = 0;
const RIGHT = 1;
const UP = 2;
const DOWN = 3;

var side = null;



export function initCellMoveCallback() {
	
	function closestSide(pos, box) {
		const ld = pos.x - box.x;
		const rd = box.x + box.width - pos.x;
		const ud = pos.y - box.y;
		const dd = box.y + box.height - pos.y;
		
		if ((ld <= rd) && (ld <= ud) && (ld <= dd)) {
			return LEFT;
		} else if ((rd <= ud) && (rd <= dd)) {
			return RIGHT;
		} else if ((ud <= dd)) {
			return UP;
		} else {
			return DOWN;
		}		
	}
	
	function closestGap(p, options) {
		var closest = 0;
		var dist = 100000;
		
		for (var i = 0; i < options.length; i++) {
			const newDist = Math.abs(options[i] - p);
			if (newDist < dist) {
				dist = newDist;
				closest = i;
			}
		}
		
		return closest;
	}
	
	
	return function (dragTargets, event, dropTargets) {
		const cellDropTargets = dropTargets.filter(dt => isCell(dt));
		const gridDropTargets = dropTargets.filter(dt => isGrid(dt));
		const pos = getSVGCoords(event);
		if (cellDropTargets.length == 1) {
			// draw a bar on the closest side 
			const box = getElementPageBBox(cellDropTargets[0]);
			side = closestSide(pos, box);
			switch (side) {
			case UP:
				return drawBar(box.x, box.y, box.x + box.width, box.y);
			case DOWN:
				return drawBar(box.x, box.y+box.height, box.x + box.width, box.y+box.height);
			case LEFT:
				return drawBar(box.x, box.y, box.x, box.y+box.height);
			case RIGHT:
				return drawBar(box.x + box.width, box.y, box.x + box.width, box.y+box.height);
			}
			
		} else if (gridDropTargets.length == 1) {
			// draw a bar for where we are nearest.
			const grid = gridDropTargets[0];
			const box = getElementPageBBox(grid);
			side = closestSide(pos, box);
			
			switch (side) {
			case UP:
			case DOWN:
				var info = parseInfo(grid);
				var relativeP = pos.x - box.x;
				var columns = info['cell-xs'];
				var i = closestGap(relativeP, columns);
				return drawBar(columns[i] + box.x, box.y, columns[i] + box.x, box.y+box.height);
			case LEFT:
			case RIGHT:
				var info = parseInfo(grid);
				var relativeP = pos.y - box.y;
				var columns = info['cell-ys'];
				var i = closestGap(relativeP, columns);
				return drawBar(box.x, columns[i] + box.y, box.x + box.width, columns[i] + box.y);
			}
		}

		clearBar();
	}
	
}
