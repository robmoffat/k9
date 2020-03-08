import { getKite9Target, number, createUniqueId, hasLastSelected, isConnected } from '/public/bundles/api.js';
import { getSVGCoords, getElementPageBBox, getMainSvg } from '/public/bundles/screen.js';
import { drawBar, clearBar } from  '/public/bundles/ordering.js';
import { icon, numeric, change, form } from '/public/bundles/form.js';

function getLayout(e) {
	if (e==null) {
		return 'none';
	} else {
		var l = e.getAttribute("layout");
		l = l == null ? "none" : l;
		return l;
	}
}

export function initCellCreator(templateUri, transition) {

	return function (parentId, x, y, newId) {

		transition.push({
			type: 'Copy',
			fragmentId: parentId,
			uriStr: templateUri,
			newId: newId,
			deep : true
		}); 
		
		transition.push({
			type: 'SetStyle',
			fragmentId: newId,
			name: "kite9-occupies",
			value: x+" "+y
		}); 

		return newId;
	}
}

export function initLayoutContextMenuCallback(transition, cellCreator, cellSelector, selector) {

	if (cellSelector == undefined) {
		cellSelector = function (e) {
			return getMainSvg().querySelectorAll("[id='" + e.getAttribute("id") + "'] > [k9-info~='connected;']");
		}
	}

	function setLayout(elems, layout, contextMenu, existing) {
		contextMenu.destroy();

		Array.from(elems).forEach(e => {

			const id = e.getAttribute("id")

			if (existing == layout) {
				return;
			}

			if (existing == 'grid') {
				// remove all the grid cells within
				cellSelector(e).forEach(f => {
					transition.push({
						type: 'ADLDelete',
						fragmentId: f.getAttribute("id"),
						cascade: false
					});
				});

				// remove other grid attributes
				['kite9-grid-rows', 'kite9-grid-columns', 'kite9-grid-size'].forEach(v => transition.push({
					fragmentId: id,
					type: 'SetStyle',
					name: v
				}))
			}

			var firstCellId;
			var newId = createUniqueId();
			var num = 0;

			if (layout == 'grid') {
				for (var x = 0; x < cols; x++) {
					for (var y = 0; y < rows; y++) {
						var cellId = cellCreator(id, x, y, newId+"-"+(num++));
						if (firstCellId == null) {
							firstCellId = cellId;
						}
					}
				}


				// move all the existing contents into the first cell
				cellSelector(e).forEach(f => {
					transition.push({
						type: 'Move',
						fragmentId: firstCellId,
						moveId: f.getAttribute("id"),
					});
				});
			}

			transition.push({
				fragmentId: id,
				type: 'SetAttr',
				name: 'layout',
				value: layout == 'none' ? null : layout
			});

		});

		transition.postCommandList();
	}

	function drawLayout(event, cm, layout, selected) {
		if (layout == "null") {
			layout = "none";
		}
		
		var out = cm.addControl(event, "/public/behaviours/containers/layout/" + layout.toLowerCase() + ".svg",
				 "Layout (" + layout + ")",
				 undefined);
		
		var img = out.children[0];
		img.style.borderRadius = "0px";

		if (selected == layout) {
			img.setAttribute("class", "selected");
		}

		return img;
	}

	var rows = 2;
	var cols = 2;

	if (selector == undefined) {
		selector = function () {
			return getMainSvg().querySelectorAll("[id][k9-ui~=layout].selected");
		}
	}

	/**
	 * Provides a layout option for the context menu
	 */
	return function (event, contextMenu) {

		const e = hasLastSelected(selector());

		if (e.length> 0) {
			var htmlElement = contextMenu.get(event);
			const layout = getLayout(hasLastSelected(e, true));
			var img = drawLayout(event, contextMenu, layout);

			function handleClick() {
				// remove the other stuff from the context menu
				contextMenu.clear();

				["none", "right", "down", "horizontal", "vertical", "left", "up"].forEach(s => {
					var img2 = drawLayout(event, contextMenu, s, layout);
					if (layout != s) {
						img2.addEventListener("click", () => setLayout(e, s, contextMenu, layout));
					} 
					
				});

				var hr = document.createElement("hr");
				htmlElement.appendChild(hr);
				
				htmlElement.appendChild(form([
					change(
						numeric('Rows', rows, { 'disabled' : layout=='grid'}), 
						(evt) => rows = number(evt.target.value)),
					change(
						numeric('Cols', cols, { 'disabled' : layout=='grid'}), 
						(evt) => cols = number(evt.target.value))
					]));
				
				var img2 = drawLayout(event, contextMenu, 'grid', layout);
				if (layout != 'grid') {
					img2.addEventListener("click", () => setLayout(Array.from(selector()), 'grid', contextMenu, layout));
				}
				
			}

			img.addEventListener("click", handleClick);
		}
	};
}


export function initLayoutMoveCallback() {

	function updateBar(event, inside, horiz) {
		var fx, fy, tx, ty;

		var { x, y } = getSVGCoords(event);

		var contain = getElementPageBBox(inside);

		if (horiz) {
			fx = contain.x;
			tx = contain.x + contain.width;
			fy = y;
			ty = y;
		} else {
			fx = x;
			tx = x;
			fy = contain.y;
			ty = contain.y + contain.height;
		}
		drawBar(fx, fy, tx, ty);
	}
	
	return function (dragTargets, event, dropTargets, barDirectionOverrideHoriz) {
		var connectedDropTargets = dropTargets.filter(dt => isConnected(dt));
		
		if (connectedDropTargets.length == 1) {
			const dropInto = connectedDropTargets[0];
			const layout = getLayout(dropInto).toUpperCase();
			if (barDirectionOverrideHoriz != undefined) {
				updateBar(event, dropInto, barDirectionOverrideHoriz);
				return;
			} else if ((layout == 'UP') || (layout == 'DOWN') || (layout == "VERTICAL")) {
				// draw the horizontal bar
				updateBar(event, dropInto, true);
				return;
			} else if ((layout == 'LEFT') || (layout == 'RIGHT') || (layout == 'HORIZONTAL')) {
				updateBar(event, dropInto, false);
				return;
			} 
		}
		
		clearBar();

	}

}