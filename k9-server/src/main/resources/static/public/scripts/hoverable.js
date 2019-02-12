

var selectedElement;

function mouseover(event) {
	var v = event.currentTarget;
	var classes = v.classList;
	if (!classes.contains("mouseover")) {
		classes.add("mouseover");
		
		if (selectedElement != undefined) {
			selectedElement.classList.remove("mouseover")
			selectedElement = v;
		}
	 }
	
	// prevents parent elements from highlighting too.
	event.stopPropagation();
}

function mouseout(event) {
	var v = event.currentTarget;
	var classes = v.classList;
	classes.remove("mouseover");
	if (selectedElement == v) {
		selectedElement = undefined;
	}
}

window.addEventListener('load', function() {
	document.querySelector("div.main").querySelectorAll("[id]").forEach(function(v) {
    	v.removeEventListener("mouseover", mouseover);
    	v.addEventListener("mouseover", mouseover);
    	v.removeEventListener("mouseout", mouseout);
    	v.addEventListener("mouseout", mouseout);
    })
})