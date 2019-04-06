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

export function getSVGCoords(evt, draw) {
	var out = getHtmlCoords(evt);
	var transform = getMainSvg().style.transform;
	var t = parseTransform(transform);
	out.x = out.x / t.scaleX;
	out.y = out.y / t.scaleY;
	
	if (draw) {
		var el = document.createElementNS("http://www.w3.org/2000/svg", "ellipse")
		el.setAttribute("cx", out.x);
		el.setAttribute("cy", out.y);
		el.setAttribute("rx", "4px");
		el.setAttribute("ry", "4px");
		getMainSvg().appendChild(el);
	}
	
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