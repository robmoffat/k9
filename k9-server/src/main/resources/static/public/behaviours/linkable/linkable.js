/**
 * Contains the functionality for linking drawing links between selected elements 
 * and a target.
 */
export function initLinkable(linker, selector)  {
	
	/**
	 * This should only be called once.  Adds the delete-key shortcut.
	 */
	document.addEventListener('keydown', function(event) {
		if (event.key == 'l') {
			const selectedElements = document.querySelectorAll("[id][k9-info*='connect:'].selected");
			linker.start(Array.from(selectedElements), event);
		}
		
		if (event.key == 'Escape') {
			linker.removeDrawingLinks();
		}
	});
	
	function move(event) {
		linker.move(event);
	}
	
	function end(event) {
		linker.end(event);
	}
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("div.main svg [id][k9-elem][k9-info*=connect]");
		}
	}

	window.addEventListener('load', function(event) {
		selector().forEach(function(v) {
			v.removeEventListener("mousemove", move);
			v.removeEventListener("mouseup", end);
			v.addEventListener("mousemove", move);
			v.addEventListener("mouseup", end);
		})
	})
}
