import { parseInfo} from "/public/bundles/api.js";
import { getSVGCoords, getElementPageBBox } from '/public/bundles/screen.js';

function doSort(container, horiz, c, ignore) {
	var sorted = Array.from(container.children)
		.filter(e => ignore.indexOf(e) == -1)
		.filter(e => e.hasAttribute("id"))
		.filter(e => e.hasAttribute("k9-info"))
		.map((e, i) => {
			const box = getElementPageBBox(e);
			const pos = horiz ? (box.x + box.width) : (box.y + box.height);
			const val = c < 0 ? (0 - c - pos) : (pos - c);
			const out = {
				p: val,
				connected: e.getAttribute("k9-info").includes("rectangular: connected;"),
				index: i,
				e: e
			};
			console.log(out);
			return out;
		})
		.filter(pos => (pos.p >= 0) || (!pos.connected))
		.sort((a, b) => (!a.connected || !b.connected) ? a.index - b.index : a.p - b.p);

	if (sorted.length == 0) {
		return null;
	} else {
		return sorted[0].e.getAttribute("id");
	}
}

export function getBeforeId(container, evt, ignore) {
	const info = parseInfo(container);
	const layout = info.layout;
	const pos = getSVGCoords(evt);

	switch (layout) {
		case 'null':
		case 'RIGHT':
		case 'HORIZONTAL':
			return doSort(container, true, pos.x, ignore);
		case 'DOWN':
		case 'VERTICAL':
		case 'GRID': 
			return doSort(container, false, pos.y, ignore);
		case 'LEFT':
			return doSort(container, true, -pos.x, ignore);
		case 'UP':
			return doSort(container, false, -pos.y, ignore);
		
		default:
			return null;
	}
}