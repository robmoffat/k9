
export function zoomableInstrumentationCallback(nav) {
	//document.body.style.width = "100%";
	document.body.style.margin = "0";
	//document.body.style.overflow = "auto";
	
	var magnification = 1;
	var zoomIn = nav.querySelector(".zoom-in");
	var zoomOut = nav.querySelector(".zoom-out");
	
	var main = document.querySelector("div.main");
	var svg = main.querySelector("svg");
	svg.style.transition = "transform .2s";
	svg.style.transformOrigin = "0 0";
	main.style.transition = "all .2s";
	main.style.overflow = "hidden";
	
	function setScale(change) {
		magnification = magnification + change;
		svg.style.transform = "scale("+magnification+")";
		main.style.width = (svg.width.baseVal.value * magnification)+"px";
		main.style.height = (svg.height.baseVal.value * magnification)+"px";
	}
	
	setScale(0);
	
	if (zoomIn == undefined) {
		var e = document.createElement("img");
	      e.setAttribute("class", "zoom-in");
	      e.setAttribute("src", "./zoom_in.svg");
	      e.addEventListener("click", () => setScale(.3));
	      nav.appendChild(e);
	}
	
	if (zoomOut == undefined) {
		var e = document.createElement("img");
	      e.setAttribute("class", "zoom-out");
	      e.setAttribute("src", "./zoom_out.svg");
	      e.addEventListener("click", () => setScale(-.3));
	      nav.appendChild(e);
	}
	
}