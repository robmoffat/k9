import { parseTransform } from './api.js';

var svg;

export function getMainSvg() {
	if (svg == undefined) {
		svg = document.querySelector("div.main svg");
	}
	return svg;
}


export function getHtmlCoords(evt) {
	var out =  {x: evt.pageX, y: evt.pageY};
	return out;
}

export function getSVGCoords(evt) {
	var out = getHtmlCoords(evt);
	var transform = getMainSvg().style.transform;
	var t = parseTransform(transform);
	out.x = out.x / t.scaleX;
	out.y = out.y / t.scaleY;
	return out;
}

export function getElementPageBBox(e) {
	const mtrx = e.getCTM();
	const bbox = e.getBBox();
	return {
		x: mtrx.e + bbox.x,
		y: mtrx.f + bbox.y,
		width: bbox.width,
		height: bbox.height
	}
}