import { parseInfo, getKite9Target, number, createUniqueId, hasLastSelected } from '/public/bundles/api.js';
import { getSVGCoords, getElementPageBBox, getMainSvg } from '/public/bundles/screen.js';

export function initCellCreator(templateUri, transition) {

	return function (parentId, x, y) {

		var newId = createUniqueId();

		transition.push({
			type: 'Copy',
			fragmentId: parentId,
			uriStr: templateUri,
			newId: newId
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

			if (existing == 'GRID') {
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

			if (layout == 'null') {
				layout = 'NONE';
			}

			var firstCellId;

			if (layout == 'GRID') {
				for (var x = 0; x < cols; x++) {
					for (var y = 0; y < rows; y++) {
						var cellId = cellCreator(id, x, y);
						if (firstCellId == null) {
							firstCellId = cellId;
						}
					}
				}

				// add the grid size
				transition.push({
					fragmentId: id,
					type: 'SetStyle',
					name: 'kite9-grid-size',
					value: rows + " " + cols
				});

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
				type: 'SetStyle',
				name: 'kite9-layout',
				value: layout
			});

		});

		transition.postCommandList();
	}

	function drawLayout(htmlElement, layout, selected) {
		var img = document.createElement("img");
		htmlElement.appendChild(img);

		if (layout == "null") {
			layout = "none";
		}
		img.setAttribute("title", "Layout (" + layout + ")");
		img.setAttribute("src", "/public/commands/layout/" + layout.toLowerCase() + ".svg");
		img.style.borderRadius = "0px";

		if (selected == layout) {
			img.setAttribute("class", "selected");
		}

		return img;
	}

	var rows = 2;
	var cols = 2;

	function addField(htmlElement, name, value, change) {
		var container = document.createElement("div");
		var label = document.createElement("label");
		var input = document.createElement("input");
		htmlElement.appendChild(container);
		container.setAttribute("class", "field");
		container.appendChild(label);
		container.appendChild(input);
		input.setAttribute("id", "id-" + name);
		input.setAttribute("name", name);
		input.setAttribute("value", value);
		input.setAttribute("type", "numeric");
		input.addEventListener("change", change);
		label.setAttribute("for", name);
		label.textContent = name + ":";
	}

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
			const debug = parseInfo(hasLastSelected(e, true));
			var img = drawLayout(htmlElement, debug.layout);

			function handleClick() {
				// remove the other stuff from the context menu
				Array.from(htmlElement.children).forEach(e => {
					htmlElement.removeChild(e);
				});

				["NONE", "RIGHT", "DOWN", "HORIZONTAL", "VERTICAL", "LEFT", "UP"].forEach(s => {
					var img2 = drawLayout(htmlElement, s, debug.layout);
					img2.addEventListener("click", () => setLayout(e, s, contextMenu, debug.layout));
				});

				var hr = document.createElement("hr");
				htmlElement.appendChild(hr);

				var img2 = drawLayout(htmlElement, 'GRID', debug.layout);
				img2.addEventListener("click", () => setLayout(Array.from(selector()), 'GRID', contextMenu, debug.layout));

				addField(htmlElement, "rows", rows, (evt) => rows = number(evt.target.value));
				addField(htmlElement, "cols", cols, (evt) => cols = number(evt.target.value));

			}

			img.addEventListener("click", handleClick);
		}
	};



}

export function initLayoutDragableMoveCallback() {

	var bar = null;

	function clearBar() {
		if (bar != null) {
			bar.parentNode.removeChild(bar);
			bar = null;
		}
	}

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

		if (bar == null) {
			var svg = getMainSvg();
			bar = document.createElementNS("http://www.w3.org/2000/svg", "path");
			bar.style.stroke = '#b7c0fe';
			bar.style.strokeWidth = '6px';
			bar.setAttributeNS(null, 'pointer-events', 'none');
			svg.appendChild(bar);
		}

		bar.setAttribute("d", "M" + fx + " " + fy + " L " + tx + " " + ty);
	}

	return function (dragTargets, event) {

		const dropInto = getKite9Target(event.target);
		const info = parseInfo(dropInto);
		const layout = info.layout;
		if ((layout == 'UP') || (layout == 'DOWN') || (layout == "VERTICAL")) {
			// draw the horizontal bar
			updateBar(event, dropInto, true);
		} else if ((layout == 'LEFT') || (layout == 'RIGHT') || (layout == 'HORIZONTAL')) {
			updateBar(event, dropInto, false);
		} else {
			clearBar();
		}
	}

}