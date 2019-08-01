import { hasLastSelected, getParentElement, parseInfo, createUniqueId } from '/public/bundles/api.js';
import { nextOrdinal, getOrdinals  } from '/public/behaviours/grid/common-grid.js'; 


export function initCellAppendContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='cell'].selected")
		}
	}
	
	function drawAppendOperation(htmlElement, append) {
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		img.setAttribute("title", "Append (" + append + ")");
		img.setAttribute("src", "/public/behaviours/grid/append/" + append.toLowerCase() + ".svg");
		img.style.borderRadius = "0px";
		return img;
	}
	
	function doAppend(container, selectedElements, side) {
		const { xOrdinals, yOrdinals } = getOrdinals(container);
		const lastSelected = hasLastSelected(selectedElements, true);
		
		var ordinalChangeMap = {};
		var ordinalItems = {};
		var itemPositions = {}; 
		var horiz = false;
		
		selectedElements.forEach(e => {
			const info = parseInfo(e);
			const position = info['position'];
			itemPositions[e.getAttribute("id")] = position;
			var pos; 
			switch (side) {
			case 'up':
				pos = position[2];
				break;
			case 'down':
				pos = nextOrdinal(position[3], yOrdinals);
				break;
			case 'left':
				pos = position[0];
				horiz = true;
				break;
			case 'right':
				pos = nextOrdinal(position[1], xOrdinals);
				horiz = true;
				break;			
			}
			ordinalChangeMap[pos] = pos;
			ordinalItems[pos] = ordinalItems[pos] ? [ ...ordinalItems[pos], e ] : [ e ];
		})
		
		// 2. Perform move operations to make space with
		const order = Object.keys(ordinalChangeMap).sort((a,b) => a-b);
		const ordinals = horiz ? xOrdinals : yOrdinals;
		
		order.forEach(o => {
			const from = ordinalChangeMap[o];
			const containerId = container.getAttribute("id");

			// first, move the other ordinals down.
			const position = ordinalChangeMap[o];
			const change = nextOrdinal(ordinalChangeMap[o], ordinals) - ordinalChangeMap[o];
			order.forEach(o2 =>  { 
				if (o2 >= o) { 
					ordinalChangeMap[o2] = ordinalChangeMap[o2] + change;
				}
			});
			
			// apply the move command on the server
			transition.push({
				type: 'ADLMoveCells',
				fragmentId: containerId,
				from: from,
				horiz: horiz,
				push: change
			})
			
			// now, introduce a line of cells in position
			ordinalItems[o].forEach(item => {
				
				const newId = createUniqueId();
				
				transition.push({
					type: 'Copy',
					fragmentId: containerId,
					uriStr: '#'+item.getAttribute("id"),
					newId: newId,
					deep: false
				})
				
				const itemPos = itemPositions[item.getAttribute("id")];
				const newPos = [
					horiz ? position : itemPos[0],
					horiz ? position : itemPos[1],
					!horiz ? position : itemPos[2],
					!horiz ? position : itemPos[3]
				];
				
				// set the position of the cell
				transition.push({
					type: 'SetStyle',
					fragmentId:  newId,
					name: 'kite9-occupies',
					value: newPos[0] + ' ' + newPos[1] + ' ' + newPos[2] + ' ' + newPos[3]
				})
			});
		})
	}
	
	function appendsCells(e, s, cm) {
		cm.destroy();
		e = Array.from(e);
		const parents = e.map(i => i.parentElement);
		const containers = [...new Set(parents)];
		containers.forEach(c => {
			doAppend(c, e.filter(i => i.parentElement == c), s);			
		})
		transition.postCommandList();
	}
	
	
	/**
	 * Provides overlays for adding cells
	 */
	return function(event, cm) {
		
		const e = hasLastSelected(selector());
		
		if (e.length > 0) {
			var htmlElement = cm.get(event);
			
			function handleClick() {
				// remove the other stuff from the context menu
				Array.from(htmlElement.children).forEach(e => {
					htmlElement.removeChild(e);
				});

				["right", "down", "left", "up"].forEach(s => {
					var img2 = drawAppendOperation(htmlElement, s);
					img2.addEventListener("click", () => appendsCells(selector(), s, cm));
				});
			}
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			img.setAttribute("title", "Delete");
			img.setAttribute("src", "/public/behaviours/grid/append/append.svg");
			img.addEventListener("click", () => handleClick());
		}
	}
	
	
}