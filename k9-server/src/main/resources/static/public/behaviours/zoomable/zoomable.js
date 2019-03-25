import { getMainSvg } from '/public/bundles/screen.js';

var magnification = 1;
const main = document.querySelector("div.main");
const svg = getMainSvg();

function setScale(mag) {
	svg.style.transition = "transform .2s";
	svg.style.transformOrigin = "0 0";
	main.style.transition = "all .2s";
	main.style.overflow = "hidden";
	magnification = mag;
	svg.style.transform = "scale("+magnification+")";
	main.style.width = (svg.width.baseVal.value * magnification)+"px";
	main.style.height = (svg.height.baseVal.value * magnification)+"px";
}

export function zoomableInstrumentationCallback(nav) {
	document.body.style.margin = "0";
	
	var zoomIn = nav.querySelector(".zoom-in");
	var zoomOut = nav.querySelector(".zoom-out");
	
	
	setScale(1);
	
	if (zoomIn == undefined) {
		var e = document.createElement("img");
	      e.setAttribute("class", "zoom-in");
	      e.setAttribute("src", "/public/behaviours/zoomable/zoom_in.svg");
	      e.addEventListener("click", () => setScale(magnification + .3));
	      nav.appendChild(e);
	}
	
	if (zoomOut == undefined) {
		var e = document.createElement("img");
	      e.setAttribute("class", "zoom-out");
	      e.setAttribute("src", "/public/behaviours/zoomable/zoom_out.svg");
	      e.addEventListener("click", () => setScale(magnification - .3));
	      nav.appendChild(e);
	}
	
	window.addEventListener('load', function(event) {
		setScale(magnification);
	})
}

export function zoomableTransitionCallback(resp) {
	setScale(magnification);
}