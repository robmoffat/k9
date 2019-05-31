import { getMainSvg } from '/public/bundles/screen.js';
import { ensureCss } from '/public/bundles/css.js';

/**
 * Adds hoverable behaviour
 */
export function initHoverable(selector, allowed, css) {
	
	var selectedElement;
	
	if (allowed == undefined) {
		allowed = function(v) {
			return true;
		}
	}
	
	function mouseover(event) {
		var v = event.currentTarget;
		if (allowed(v)) {
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
	}

	function mouseout(event) {
		var v = event.currentTarget;
		if (allowed(v)) {
			var classes = v.classList;
			classes.remove("mouseover");
			if (selectedElement == v) {
				selectedElement = undefined;
			}
		}
	}
	
	if (css == undefined) {
		css = '/public/behaviours/hoverable/hoverable.css';
	}
	
	ensureCss(css);

	if (selector == undefined) {
		selector = function() { return getMainSvg().querySelectorAll("[id][k9-elem]"); }
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





