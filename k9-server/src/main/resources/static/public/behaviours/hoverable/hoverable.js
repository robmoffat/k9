/**
 * Adds hoverable behaviour
 */

export function initHoverable(selector, css) {
	
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
	
	if (css == undefined) {
		css = '/public/behaviours/hoverable/hoverable.css';
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
		selector = function() { return document.querySelectorAll("div.main svg [id][k9-elem]"); }
	}
	
	window.addEventListener('load', function() {
		selector().forEach(function(v) {
	    	v.removeEventListener("mouseover", mouseover);
	    	v.addEventListener("mouseover", mouseover);
	    	v.removeEventListener("mouseout", mouseout);
	    	v.addEventListener("mouseout", mouseout);
	    })
	})
}





