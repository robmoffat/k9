// Adds .selected class when the user mousedowns over an element.



function mousedown(event) {
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
			if ((v != null) && (v.hasAttribute("id"))) {
				v.classList.remove("selected")
			}
		}
		
	} else {
		classes.remove("selected")
	}
	
	event.stopPropagation();
}

window.addEventListener('load', function() {
	
    document.querySelectorAll("[id]").forEach(function(v) {
    	// set up mousedown listeners.
    	v.removeEventListener("mousedown", mousedown);
    	v.addEventListener("mousedown", mousedown);
    })
})