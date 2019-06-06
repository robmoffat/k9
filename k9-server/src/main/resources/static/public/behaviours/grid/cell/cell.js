import { parseInfo, getParentElement, isConnected, isDiagram } from '/public/bundles/api.js';
import { drawBar, getBefore, clearBar } from '/public/bundles/ordering.js';
import { getElementPageBBox } from '/public/bundles/screen.js';


// defines different types of drag we can do, based on the 
// elements being dragged.
const NORMAL = 0;		// not cells, not dropping into tables.
const MIXED = 1;		// mix of cells and not-cells.		
const CELLS = 2;		// just cells, possibly from multiple tables
const COLUMNS = 3;		// whole columns of a single table selected.
const TABLES = 4;		// just tables selected

/**
 * For the selected elements, returns one of the values above.
 */
function determineSelected(targets) {
	var cols = {}
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
				// ensure row
				var row = cols[x];
				if (row == undefined) {
					row = new Set();
					cols[x] = row;
				}
				
				// populate row
				for (var y = xr[0]; y < xr[1]; y++) {
					row.add(y);
				}
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
		return TABLES;
	} else if (inTables.size == 0) {
		return NORMAL;
	} else if (others) {
		return MIXED;
	} else if (inTables.size == 1) {
		// cells from a single table
		const table = inTables.entries().next().value[0];
		const size = parseInfo(table)['grid-size'];
		const incompleteCols = Object.values(cols).map(c => c.size).filter(c => c < size[1]).length;

		if (incompleteCols == 0) {
			return COLUMNS;
		} 
	}	
	
	return CELLS;
}

var cellsInType;

/**
 * Modifies the drag locator to track what type of drag we are about to do.
 */
export function initCellDragLocator(mainDragLocator) {
	
	return function(evt) {
		var dragTargets = mainDragLocator(evt);
		cellsInType = determineSelected(dragTargets);
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
		
		const cellsOutType = determineSelected(dropTargets);
		
		switch (cellsInType) {
		case NORMAL: 
		case TABLES:
			switch (cellsOutType) {
			case NORMAL:
			case CELLS:
				// normal drop approach
				return dropTargets;
			default:
				// prevents dropping of not-cells into 
				return [];
			}
		case MIXED:
			return [];	// you can't do anything with mixed.
		case COLUMNS:
		case CELLS:
			switch (cellsOutType) {
			case NORMAL:
			case MIXED:
				return [];
			case TABLES:
				return dropTargets;
			case COLUMNS:
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
			const box = getElementPageBBox(dropTarget);
			x = box.x + box.width;
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
			const box = getElementPageBBox(dropTarget);
			ys = box.y;
			ye = box.y + box.height;
			x = box.x + box.width;
		}
		
		drawBar(x, ys, x, ye);
	}
	
	return function(dragTargets, event, dropTargets) {
		switch (cellsInType) {
		case COLUMNS:
			columnBar(dragTargets, event, dropTargets);
			break;
		case CELLS:
			cellBar(dragTargets, event, dropTargets);
			break;
		default:
			return mainCallback(dragTargets, event, dropTargets);
		}
	}
	
}


export function initCellDropCallback(transition, mainCallback) {
	
	return function(dragTargets, evt, dropTargets) {
		
		switch (cellsInType) {
		case COLUMNS:
			break;
		case CELLS:
			const dropTarget = mainDropTarget(dropTargets);
			var beforeId = before == null ? null : before.getAttribute("id");
			Array.from(dragTargets).forEach(dt => {
				if (isConnected(dt)) {
					transition.push( {
						type: 'Move',
						fragmentId: dropTarget.getAttribute('id'),
						moveId: dt.getAttribute('id'),
						beforeFragmentId: beforeId
					});
				}	
			});
			
			before = null;
			clearBar();
			return true;

		default:
			return mainCallback(dragTargets, evt, dropTargets);
		}
	}
	
}
