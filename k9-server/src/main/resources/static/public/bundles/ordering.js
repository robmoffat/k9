import { parseInfo} from "/public/bundles/api.js";
import { getSVGCoords, getElementPageBBox, getMainSvg } from '/public/bundles/screen.js';

function doSort(contents, horiz, c, ignore) {
	var sorted = contents
		.map((e, i) => {
			const box = getElementPageBBox(e);
			const pos = horiz ? (box.x + box.width/2) : (box.y + box.height/2);
			const val = c < 0 ? (0 - c - pos) : (pos - c);
			const out = {
				p: val,
				connected: e.getAttribute("k9-info").includes("rectangular: connected;"),
				index: i,
				e: e
			};
			return out;
		})
		.filter(pos => (pos.p >= 0) || (!pos.connected))
		.sort((a, b) => (!a.connected || !b.connected) ? a.index - b.index : a.p - b.p);

	if (sorted.length == 0) {
		return null;
	} else {
		return sorted[0].e;
	}
}

export function getBefore(container, evt, ignore) {
	
	const info = parseInfo(container);
	const layout = info.layout;
	const pos = getSVGCoords(evt);
	
	const allChildren = Array.from(container.children)
		.filter(e => ignore.indexOf(e) == -1)
		.filter(e => e.hasAttribute("id"))
		.filter(e => e.hasAttribute("k9-info"));


	switch (layout) {
		case 'null':
		case 'RIGHT':
		case 'HORIZONTAL':
			return doSort(allChildren, true, pos.x, ignore);
		case 'DOWN':
		case 'VERTICAL':
			return doSort(allChildren, true, pos.x, ignore);
		case 'LEFT':
			return doSort(allChildren, true, -pos.x, ignore);
		case 'UP':
			return doSort(allChildren, false, -pos.y, ignore);
		case 'GRID': 
			// just compare with elements on the current line.
			const intersectingChildren = allChildren
				.filter(e => {
					const box = getElementPageBBox(e);
					return (box.y <= pos.y) && (box.y + box.height >= pos.y);
				});
			
			const out = doSort(intersectingChildren, true, pos.x, ignore);
			
			if (out == null) {
				const lastOnLine = intersectingChildren[intersectingChildren.length-1];
				const idx = allChildren.indexOf(lastOnLine);
				
				if (idx < allChildren.length-1) {
					return allChildren[idx+1];
				}
			} else {
				return out;
			}
			
		default:
			return null;
	}
}


export function getBeforeId(container, evt, ignore) {
	
	const before = getBefore(container, evt, ignore);
	if (before == null) {
		return null;
	} else {
		return before.getAttribute("id");
	}
}


var bar = null;

export function clearBar() {
	if (bar != null) {
		bar.parentNode.removeChild(bar);
		bar = null;
	}
}

export function drawBar(fx, fy, tx, ty) {
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
