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
		const cellDragTargets = dragTargets.filter(dt => isCell(dt));
		const pos = getSVGCoords(event);
		if ((cellDropTargets.length == 1) && (cellDragTargets.length == dragTargets.length)) {
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
			
		} 

		clearBar();
	}
	
}


export function initCellDropCallback(transition) {
	
	function getOrdinals(container) {
		var xOrdinals = {
			max: Number.MIN_SAFE_INTEGER,
			min: Number.MAX_SAFE_INTEGER
		}
		var yOrdinals = {
			max: Number.MIN_SAFE_INTEGER,
			min: Number.MAX_SAFE_INTEGER
		};
		
		Array.from(container.children).forEach(e => {
			const details = parseInfo(e);
			if ((details != null) && (details['position']) && details['grid-x']) {
				const position = details['position'];
				const gridX = details['grid-x'];
				const gridY = details['grid-y'];
				
				xOrdinals[gridX[0]] = position[0];
				yOrdinals[gridY[0]] = position[2];
				xOrdinals.max = Math.max(xOrdinals.max, gridX[0]);
				xOrdinals.min = Math.min(xOrdinals.min, gridX[0]);
				yOrdinals.max = Math.max(yOrdinals.max, gridY[0]);
				yOrdinals.min = Math.min(yOrdinals.min, gridY[0]);
			}
		});
		
		return {
			xOrdinals: xOrdinals,
			yOrdinals: yOrdinals
		}
	}
	
	function isOccupied(container, x, y) {
		return Array.from(container.children)
			.map(e => {
				const details = parseInfo(e);
				if ((details != null) && (details['position']) && (!e.classList.contains('grid-temporary'))) {
					const minX = details['position'][0];
					const maxX = details['position'][1];
					const minY = details['position'][2];
					const maxY = details['position'][3];
					
					const out = ((minX<=x) && (maxX>=x) && (minY<=y) && (maxY>=y));
					return out;
				}
				
				return false;
			})
			.reduce((a,b) => a||b)
	}
	
	function calculateArea(dragTargets, moverX, moverY) {
		
		function up1(area, change) {
			area[0] = Math.min(area[0], change[0]);
			area[1] = Math.max(area[1], change[1]);
		}
		
		if (moverX == undefined) {
			moverX = [0, 0];
			moverY = [0, 0];
		}
		
		var out = {
			x: [10000, -10000],
			y: [10000, -10000],
			items: {}
		}
		
		dragTargets.forEach(dt => {
			const dragInfo = parseInfo(dt);
			const dragX = dragInfo['grid-x'];
			const dragY = dragInfo['grid-y'];
			
			up1(out.x, dragX);
			up1(out.y, dragY);
			
			out.items[dt.getAttribute("id")] = {
				dx: dragX[0] - moverX[0],
				dy: dragY[0] - moverY[0] 
			}
		});
		
		
		console.log("Area: "+JSON.stringify(out));
		return out;
		
	}
	
	function getPush(area, to, xOrdinals, yOrdinals) {
		if ((side == UP) || (side==DOWN)) {
			const d = area.y[1]- area.y[0];
			const place = to.y;
			const from = getOrdinal(place, yOrdinals);
			const dist = getOrdinal(place + d, yOrdinals) - from;
			return { from: from, horiz: false, push: dist};
		} else {
			const d = area.x[1]- area.x[0];
			const place = to.x;
			const from = getOrdinal(place, xOrdinals);
			const dist = getOrdinal(place + d, xOrdinals) - from;
			return { from: from, horiz: true, push: dist};
		}
	}
	
	function calculateTo(dropX, dropY) {
		switch (side) {
		case UP:
			return { x: dropX[0], y: dropY[0]};
		case DOWN:
			return { x: dropX[0], y: dropY[1]};
		case LEFT:
			return { x: dropX[0], y: dropY[0]};
		case RIGHT:
			return { x: dropX[1], y: dropY[0]};
		}
	}
	
	function getOrdinal(index, ordinals) {
		
		if (index < ordinals.min) {
			return ordinals[ordinals.min] - (index + ordinals.min);
		} else if (index >= ordinals.max) {
			return ordinals[ordinals.max] + (index - ordinals.max);
		} else {
			return ordinals[index];
		}
	}
	
	
	return function(dragTargets, evt, dropTargets) {
		const cellDropTargets = dropTargets.filter(dt => isCell(dt));
		const cellDragTargets = dragTargets.filter(dt => isCell(dt));
		const gridDropTargets = dropTargets.filter(dt => isGrid(dt));
		const mover = dragTargets.filter(dt => dt.classList.contains("lastSelected"))[0];
		const moverInfo = parseInfo(mover);
		const moverX = [ moverInfo['position'][0], moverInfo['position'][1] ];
		const moverY = [ moverInfo['position'][2], moverInfo['position'][3] ];
		
		if (cellDragTargets.length != dragTargets.length) {
			// can't drop here
			return;
		}

		if (cellDropTargets.length == 1) {
			// we're going to drop here.
			const container = cellDropTargets[0].parentElement;
			const containerId = container.getAttribute("id");

			const dropInfo = parseInfo(cellDropTargets[0]);
			const dropX = dropInfo['grid-x'];
			const dropY = dropInfo['grid-y'];
			console.log("drop: "+dropX+ " "+ dropY);
			
			const to = calculateTo(dropX, dropY);
			const area = calculateArea(dragTargets, moverInfo['grid-x'], moverInfo['grid-y']);
			const {xOrdinals, yOrdinals} = getOrdinals(container);
			
			var needsPush = false;
			const moveOperations = [];
			dragTargets.forEach(dt => {
				const id = dt.getAttribute("id");
				
				// move into container
				moveOperations.push({
					type: 'Move',
					fragmentId: containerId,
					moveId: id,
				});
				
				const item = area.items[id];
				const xOccupies = getOrdinal(to.x+item.dx,xOrdinals);
				const yOccupies = getOrdinal(to.y+item.dy,yOrdinals);
				
				if (isOccupied(container, xOccupies, yOccupies)) {
					needsPush = true;
				}
				
				// set the position of the cell
				moveOperations.push({
					type: 'SetStyle',
					fragmentId:  id,
					name: 'kite9-occupies',
					value: xOccupies+" "+yOccupies
				})
			});
			
			if (needsPush) {
				var { from, horiz, push } = getPush(area, to, xOrdinals, yOrdinals);
				if (push != 0) {
					console.log("Push: "+from+" "+horiz+" "+push);
					
					transition.push({
						type: 'ADLMoveCells',
						fragmentId: containerId,
						moveId: mover.getAttribute("id"),
						from: from,
						horiz: horiz,
						push: push
					})
				}
			}
			
			moveOperations.forEach(mo => transition.push(mo));
		}
	}
}

