// Adds .selected class when the user mouseups over an element.
// Adds .lastSelected class to a single element, which is the last one clicked on

function mouseup(event) {
	var v = event.currentTarget;
	var classes = v.classList;
	if (!classes.contains("selected")) {
		classes.add("selected");
		
		// unselect nested elements
		v.querySelectorAll(".selected").forEach(c => {
			c.classList.remove("selected")
		})
		
		// unselect parent elements
		while (v) {
			v = v.parentElement;
			if (v != null) {
				v.classList.remove("selected")
			}
		}
		
	} else {
		classes.remove("selected")
	}
	
	document.querySelectorAll(".lastSelected").forEach(c => {
		c.classList.remove("lastSelected")
	})
	
	classes.add("lastSelected")
	
	event.stopPropagation();
}

window.addEventListener('load', function() {
	
    document.querySelector("div.main").querySelectorAll("*[id]").forEach(function(v) {
    	// set up mousedown listeners.
    	v.removeEventListener("mousedown", mouseup);
    	v.addEventListener("mousedown", mouseup);
    })
})