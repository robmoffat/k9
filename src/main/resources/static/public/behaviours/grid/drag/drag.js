import { parseInfo, getParentElement, isConnected, isDiagram, getContainerChildren } from '/public/bundles/api.js';
import { drawBar, getBefore, clearBar } from '/public/bundles/ordering.js';
import { getElementPageBBox, getSVGCoords } from '/public/bundles/screen.js';
import { getMainSvg } from '/public/bundles/screen.js';
import { isCell, getOrdinal, getOrdinals  } from '/public/behaviours/grid/common-grid.js'; 

function isEmptyGrid(e) {
	var out = e.getAttribute("k9-info");
	if (out.includes("layout: GRID;")) {
		return calculateOccupation(e).length==0;
	} 
	
	return false;
}

/**
 * This will only allow you to drag cells from the container which is the mover.
 */
export function initCellDragLocator(selector) {
	
	if (selector == undefined) {
		selector = function() {
			moveCache = {};
			const allCells = Array.from(document.querySelectorAll("[id][k9-ui~='cell'].selected, [id][k9-ui~='cell'].mouseover"))
			if (allCells.length > 0) {
				const mover = allCells.filter(dt => dt.classList.contains("lastSelected"))[0];
				const container = mover.parentElement;
				const cellsInContainer = allCells.filter(c => c.parentElement == container);
				return cellsInContainer;
			} else {
				return [];
			}
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
		
		if ((!isEmptyGrid(dropTarget)) && (!isCell(dropTarget))) {
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

const RIGHT = 1;
const UP = 2;
const DOWN = 3;
const LEFT = 4;


var moveCache = {
	area: null,
	mover: null, 
	container: null,
	occupation: [],
	side: null
}

function calculateOccupation(container) {
	
	if (moveCache.container == container) {
		return moveCache.occupation;
	}
	
	var occupation = [];
	
	Array.from(container.children).forEach(e => {
		const details = parseInfo(e);
		if ((details != null) && details['grid-x']) {
			const gridX = details['grid-x'];
			const gridY = details['grid-y'];
			
			if (!e.classList.contains('grid-temporary')) {
				occupation.push({x: gridX, y: gridY})
			}
		}
	});
	
	moveCache.container = container;
	moveCache.occupation = occupation;
	moveCache.side = undefined;
	
	return occupation;
}

export function initCellMoveCallback() {
	
	function calculateArea(dragTargets) {
		
		function up1(area, change) {
			area[0] = Math.min(area[0], change[0]);
			area[1] = Math.max(area[1], change[1]);
		}
		
		const mover = dragTargets.filter(dt => dt.classList.contains("lastSelected"))[0];
		
		if (moveCache.mover == mover) {
			return moveCache.area;
		}
		
		const moverInfo = parseInfo(mover);
		const moverX = moverInfo ? moverInfo['grid-x'][0] : 0;
		const moverY = moverInfo ? moverInfo['grid-y'][0] : 0;
		
		var out = {
			x: [10000, -10000],
			y: [10000, -10000],
			items: {}
		}
		
		dragTargets.forEach(dt => {
			const id = dt.getAttribute("id");
			if (dt.container == mover.container) {
				const dragInfo = parseInfo(dt);
				const dragX = dragInfo['grid-x'];
				const dragY = dragInfo['grid-y'];
				
				up1(out.x, [dragX[0]-moverX, dragX[1]-moverX]);
				up1(out.y, [dragY[0]-moverY, dragY[1]-moverY]);
				
				out.items[id] = {
					dx: [ dragX[0] - moverX, dragX[1] - moverX ],
					dy: [ dragY[0] - moverY, dragY[1] - moverY ] 
				}
			} 
		});
		
		moveCache.mover = mover;
		moveCache.area = out;
		
		console.log("Area: "+JSON.stringify(out));
		return out;
		
	}

	function overlaps(dragTargets, dropTargets) {
		
		function intersects(r1, r2) {
			const startIn = (r1[0] >= r2[0]) && (r1[0] < r2[1]);
			const endIn = (r1[1] > r2[0]) && (r1[1] <= r2[1]);
			return startIn || endIn;
		}
		
		const container = dropTargets[0].parentElement;
		const containerId = container.getAttribute("id");
		var area = calculateArea(dragTargets);
		var occupation = calculateOccupation(container);
		
		const dropInfo = parseInfo(dropTargets[0]);
		const dropX = dropInfo['grid-x'];
		const dropY = dropInfo['grid-y'];
		
		for(var i = 0; i < dragTargets.length; i++) {
			const dt = dragTargets[i];
			const id = dt.getAttribute("id");
			const item = area.items[id];
			const movedItem = { 
					dx: [item.dx[0] + dropX[0], item.dx[1] + dropX[0]],
					dy: [item.dy[0] + dropY[0], item.dy[1] + dropY[0]]
			}
			if (item != undefined) {
				for(var j = 0; j < occupation.length; j++) {
					const occ = occupation[j];
					const out = intersects(movedItem.dx, occ.x) && 
						intersects(movedItem.dy, occ.y);
					if (out) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
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
	
	return function (dragTargets, event, dropTargets) {
		const cellDropTargets = dropTargets.filter(dt => isCell(dt));
		const cellDragTargets = dragTargets.filter(dt => isCell(dt));
		const gridDropTargets = dropTargets.filter(dt => isEmptyGrid(dt));
		const pos = getSVGCoords(event);
		if (cellDragTargets.length == dragTargets.length) {
			if (cellDropTargets.length == 1) {
				if (overlaps(cellDragTargets, cellDropTargets)) {
					// draw a bar on the closest side 
					const box = getElementPageBBox(cellDropTargets[0]);
					moveCache.side = closestSide(pos, box);
					switch (moveCache.side) {
					case UP:
						return drawBar(box.x, box.y, box.x + box.width, box.y);
					case DOWN:
						return drawBar(box.x, box.y+box.height, box.x + box.width, box.y+box.height);
					case LEFT:
						return drawBar(box.x, box.y, box.x, box.y+box.height);
					case RIGHT:
						return drawBar(box.x + box.width, box.y, box.x + box.width, box.y+box.height);
					}			
				} else {
					moveCache.side = null;
				}
			} else if (gridDropTargets.length == 1) {
				moveCache.container = gridDropTargets[0];
				moveCache.occupation = [];
				calculateArea(cellDragTargets);
			}
		}
		

		clearBar();
	}
	
}


export function initCellDropCallback(transition) {
	
	function getPush(area, to, xOrdinals, yOrdinals) {
		if ((moveCache.side == UP) || (moveCache.side==DOWN)) {
			const d = area.y[1]- area.y[0];
			const place = (moveCache.side == DOWN) ? to.y+1 : to.y;
			to.y = place - area.y[0];
			const from = getOrdinal(place, yOrdinals);
			const dist = getOrdinal(place + d, yOrdinals) - from;
			return { from: from, horiz: false, push: dist};
		} else {
			const d = area.x[1]- area.x[0];
			const place = (moveCache.side == RIGHT) ? to.x+1 : to.x;
			to.x = place - area.x[0];
			const from = getOrdinal(place, xOrdinals);
			const dist = getOrdinal(place + d, xOrdinals) - from;
			return { from: from, horiz: true, push: dist};
		}
	}
	
	return function(dragTargets, evt, dropTargets) {
		const cellDropTargets = dropTargets.filter(dt => isCell(dt));
		const cellDragTargets = dragTargets.filter(dt => isCell(dt));
		const gridDropTargets = dropTargets.filter(dt => isEmptyGrid(dt));
	
		if (cellDragTargets.length != dragTargets.length) {
			// can't drop here
			return;
		}
		
		if ((cellDropTargets.length == 1) || (gridDropTargets.length == 1)) {
			// we're going to drop here.
			const cellDrop = cellDropTargets.length == 1;
			const container = moveCache.container
			const containerId = container.getAttribute("id");
			const {xOrdinals, yOrdinals} = getOrdinals(container);
			
			const dropInfo = cellDrop ? parseInfo(dropTargets[0]) : {};
			const dropX = cellDrop ? dropInfo['grid-x'] : [ 0, 0 ];
			const dropY = cellDrop ? dropInfo['grid-y'] : [ 0, 0 ];
			var to = {x: dropX[0],y: dropY[0] };
			
			if ((moveCache.side) && (container == moveCache.container)) {
				var { from, horiz, push } = getPush(moveCache.area, to, xOrdinals, yOrdinals);
				console.log("Push: "+from+" "+horiz+" "+push);
				
				transition.push({
					type: 'ADLMoveCells',
					fragmentId: containerId,
					from: from,
					horiz: horiz,
					push: push
				})
			}
			
			dragTargets.forEach(dt => {
				const id = dt.getAttribute("id");
				
				// move into container
				transition.push({
					type: 'Move',
					fragmentId: containerId,
					moveId: id,
				});
				
				if (cellDrop) {
					const item = moveCache.area.items[id];
					if (item) {
						const position = getOrdinal(to.x+item.dx[0],xOrdinals) + " " + 
						getOrdinal(to.x+item.dx[1]-1,xOrdinals) + " " + 
						getOrdinal(to.y+item.dy[0],yOrdinals) + " " + 
						getOrdinal(to.y+item.dy[1]-1,yOrdinals);
					
					
						// set the position of the cell
						transition.push({
							type: 'SetStyle',
							fragmentId:  id,
							name: 'kite9-occupies',
							value: position
						})
					}					
				}

				
			});
			
		}
	}
}





