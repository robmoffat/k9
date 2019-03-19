
var svg;

export function getTrueCoords(evt) {
	// find the current zoom level and pan setting, and adjust the reported
	//    mouse position accordingly
	var newScale = svg.currentScale;
	var translation = svg.currentTranslate;
	return {
		x:  (evt.clientX - translation.x) / newScale,
		y:  (evt.clientY - translation.y) / newScale
	}
}

window.addEventListener('load', function(event) {
	svg = document.querySelector("div.main svg");
});

export function getMainSvg() {
	return svg;
}


export function getHtmlCoords(evt) {
	return {x: evt.pageX, y: evt.pageY };
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