import { parseInfo, getParentElement, isConnected, isDiagram, getContainerChildren } from '/public/bundles/api.js';
import { drawBar, getBefore, clearBar } from '/public/bundles/ordering.js';
import { getElementPageBBox } from '/public/bundles/screen.js';


// defines different types of drag we can do, based on the 
// elements being dragged.
const NORMAL = 0;		// not cells, not dropping into tables.
const MIXED = 1;		// mix of cells and not-cells.		
const CELLS = 2;		// just cells, possibly from multiple tables
const RECT = 3 			// a rectangle of cells from one table.
const TABLES = 4;		// just tables selected

var cellsInType;
var rectDimensions;

/**
 * For the selected elements, returns one of the values above.
 */
function determineSelected(targets) {
	var cols = new Set();
	var rows = new Set();
	var inTables = new Set();
	var others = false;
	var cells  = 0;
	var selectedTables = new Set();
	
	targets.forEach(t => {
		const info = parseInfo(t);
		if (info['grid-x']) {
			const xr = info['grid-y'];
			const yc = info['grid-x'];
			
			for (var x = yc[0]; x < yc[1]; x++) {
				cols.add(x);
			}
				
			for (var y = xr[0]; y < xr[1]; y++) {
				rows.add(y);
			}
			
			inTables.add(getParentElement(t));
			cells++;
		} else if (info['grid-size']){
			selectedTables.add(t);
		} else {
			// contains non-cells.
			others = true;
		}
	});
	
	if ((inTables.size == 0) && (selectedTables.size > 0)) {
		return {shape: TABLES };
	} else if (inTables.size == 0) {
		return {shape: NORMAL };
	} else if (others) {
		return {shape: MIXED };
	} else if (inTables.size == 1) {
		if (cells == cols.size * rows.size) {
			return {
				shape: RECT, 
				dimensions: [cols.size, rows.size]
			};
		} 
	}	
	
	return {shape: CELLS};
}

/**
 * Modifies the drag locator to track what type of drag we are about to do.
 */
export function initCellDragLocator(mainDragLocator) {
	
	return function(evt) {
		var dragTargets = mainDragLocator(evt);
		var {shape, dimensions} = determineSelected(dragTargets);
		cellsInType = shape;
		rectDimensions = dimensions;
		return dragTargets;
	}
}

/**
 * Modifies the drop locator to say:  if we have cells selected, you can't drop in a cell, you
 * have to drop in a table.
 */
export function initCellDropLocator(mainDropLocator) {
		
	function dragIds(dragTargets) {
		return dragTargets
			.map(dt => dt.getAttribute("id"))
			.reduce((a,b) => a+","+b, "");
	}
	
	return function(dragTargets, evt) {
		const dropTargets = mainDropLocator(dragTargets, evt);
		
		if (dropTargets.length != 1) {
			return [];	// you can't drop to multiple/no places.
		}
		
		const { shape }  = determineSelected(dropTargets);
		const cellsOutType = shape;
		
		switch (cellsInType) {
		case NORMAL: 
		case TABLES:
			switch (cellsOutType) {
			case NORMAL:
			case CELLS:
			case RECT:
				// normal drop approach
				return dropTargets;
			default:
				// prevents dropping of not-cells into 
				return [];
			}
		case MIXED:
			return [];	// you can't do anything with mixed.
		case RECT:
		case CELLS:
			switch (cellsOutType) {
			case NORMAL:
			case MIXED:
				return [];
			case TABLES:
				return dropTargets;
			case RECT:
			case CELLS:
				return dropTargets.map(dt => getParentElement(dt));
			}
		}
	}
}

function mainDropTarget(dropTargets) {
	const connectedDropTargets = dropTargets.filter(t => isConnected(t) || isDiagram(t));
	if (connectedDropTargets.length == 1) {
		return connectedDropTargets[0];
	} else {
		return null;
	}
}

/**
 * These keeps track of whereabouts we are going to drop.
 */
var before;

/**
 * Handles the drawing of the bar when the layout is grid.
 */
export function initCellMoveCallback(mainCallback) {
	
	function columnBar(dragTargets, event, dropTargets) {
		const dropTarget = mainDropTarget(dropTargets);
		
		if (dropTarget == null) {
			clearBar();
			return;
		}
		
		before = getBefore(dropTarget, event, dragTargets);
		const mainBox = getElementPageBBox(dropTarget);

		var x;
		var ys = mainBox.y, ye = mainBox.y+mainBox.height;
		if (before != null) {
			const box = getElementPageBBox(before);
			x = box.x;
		} else {
			const allChildren = getContainerChildren(dropTarget, dragTargets);
			const lastChild = allChildren[allChildren.length-1];
			if (lastChild) {
				const box = getElementPageBBox(lastChild);
				x = box.x + box.width;
			} else {
				return;
			}
		}
		
		drawBar(x, ys, x, ye);
	}
	
	function cellBar(dragTargets, event, dropTargets) {
		const dropTarget = mainDropTarget(dropTargets);
		
		if (dropTarget == null) {
			clearBar();
			return;
		}
		
		before = getBefore(dropTarget, event, dragTargets);
		var x;
		var ys, ye;
		if (before != null) {
			const box = getElementPageBBox(before);
			ys = box.y;
			ye = box.y + box.height;
			x = box.x;
		} else {
			const allChildren = getContainerChildren(dropTarget, dragTargets);
			const lastChild = allChildren[allChildren.length-1];
			if (lastChild) {
				const box = getElementPageBBox(lastChild);
				x = box.x + box.width;
				ys = box.y;
				ye = box.y + box.height;
			} else {
				return;
			}
		}
		
		drawBar(x, ys, x, ye);
	}
	
	return function(dragTargets, event, dropTargets) {
		switch (cellsInType) {
		case RECT:
			const dropTarget = mainDropTarget(dropTargets);
			if (dropTarget) {
				const info = parseInfo(dropTarget);
				const height = info['grid-size'][1];
				if (height == rectDimensions[1]) {
					return columnBar(dragTargets, event, dropTargets);
				}
			}
			
			// otherwise, drop through
			
		case CELLS:
			cellBar(dragTargets, event, dropTargets);
			break;
		default:
			return mainCallback(dragTargets, event, dropTargets);
		}
	}
	
}


export function initCellDropCallback(transition, mainCallback) {
	
	function columnFindBefore(col, row, dt, allChildren) {
		for (var i = 0; i < allChildren.length; i++) {
			// relying on children being in order
			const cand = allChildren[i];
			const info = parseInfo(cand);
			const dcol = info['grid-x'][0];
			const drow = info['grid-y'][0];
			if (drow > row) {
				return cand.getAttribute("id");
			} else if (drow == row) {
				if (dcol == col) {
					return cand.getAttribute("id");
				}
			}
		}
		
		return null;
	}
	
	return function(dragTargets, evt, dropTargets) {
		
		switch (cellsInType) {
		case RECT:
			const dropTargetC = mainDropTarget(dropTargets);
			const info = parseInfo(dropTargetC);
			const height = info['grid-size'][1];
			if (height == rectDimensions[1]) {
				const col = before ? parseInfo(before)['grid-x'][0] : parseInfo(dropTargetC)['grid-size'][0];
				const allChildren = getContainerChildren(dropTargetC, dragTargets);
				var row = 0;
				dragTargets.forEach(dt => {
					transition.push( {
						type: 'Move',
						fragmentId: dropTargetC.getAttribute('id'),
						moveId: dt.getAttribute('id'),
						beforeFragmentId: columnFindBefore(col, row++, dt, allChildren)
					});
					
				});
				
				before = null;
				clearBar();
				return true;
			}
			
			// drop through
			
		case CELLS:
			const dropTarget = mainDropTarget(dropTargets);
			const beforeId = before == null ? null : before.getAttribute("id");
			dragTargets.forEach(dt => {
				transition.push( {
					type: 'Move',
					fragmentId: dropTarget.getAttribute('id'),
					moveId: dt.getAttribute('id'),
					beforeFragmentId: beforeId
				});
				
			});
			
			before = null;
			clearBar();
			return true;

		default:
			return mainCallback(dragTargets, evt, dropTargets);
		}
	}
	
}
