// Adds .selected class when the user mouseups over an element.
// Adds .lastSelected class to a single element, which is the last one clicked on

export function initSelectable(selector, css) {
	
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
	
	if (css == undefined) {
		css = '/public/behaviours/selectable/selectable.css';
	}
	
	if (!document.getElementById(css)) {
	    var head  = document.getElementsByTagName('head')[0];
	    var link  = document.createElement('link');
	    link.id   = css;
	    link.rel  = 'stylesheet';
	    link.type = 'text/css';
	    link.href = css;
	    link.media = 'all';
	    head.appendChild(link);
	}
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("div.main *[id]");
		}
	}
	
	window.addEventListener('load', function() {
		
	    selector().forEach(function(v) {
	    	// set up mousedown listeners.
	    	v.removeEventListener("mousedown", mouseup);
	    	v.addEventListener("mousedown", mouseup);
	    })
	})
	
}



