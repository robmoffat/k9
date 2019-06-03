import { parseInfo, getParentElement } from '/public/bundles/api.js';

// defines different types of drag we can do, based on the 
// elements being dragged.
const NORMAL = 0;		// not cells, not dropping into tables.
const MIXED = 1;		// mix of cells and not-cells.		
const CELLS = 2;		// just cells, possibly from multiple tables
const COLUMNS = 3;		// whole columns of a single table selected.
const TABLES = 4;		// just tables selected

function containsAll(numbers, range) {
	const notIncluded = Array.from(new Array(range).keys())
		.filter(k => !numbers.has(k));
	
	return notIncluded.length == 0;
}

function determineSelected(targets) {
	var rows = new Set();
	var inTables = new Set();
	var others = false;
	var cells  = 0;
	var selectedTables = new Set();
	
	targets.forEach(t => {
		const info = parseInfo(t);
		if (info['grid-x']) {
			const xr = info['grid-x'];
			for (var i = xr[0]; i < xr[1]; i++) {
				rows.add(i);
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
//	} else if (inTables.size == 1) {
//		// cells from a single table
//		const table = inTables.entries().next().value[0];
//		const size = parseInfo(table)['grid-size'];
//		const allRows = containsAll(rows, size[1]);
//
//		if (allRows && isSquare) {
//			return COLUMNS;
//		} 
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
			case COLUMNS:
				return [];
			case TABLES:
				return dropTargets;
			case CELLS:
				return dropTargets.map(dt => getParentElement(dt));
			}
		}
	}
	
}