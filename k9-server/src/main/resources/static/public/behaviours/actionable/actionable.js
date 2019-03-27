/**
 * Allows the context menu to appear when the user clicks an element with an id
 */    
export function initActionable(contextMenu, selector) {

	/**
	 * Displays a context menu when the user clicks on an element.
	 */
	function click(event) {
		contextMenu.destroy();
		contextMenu.handle(event);
		event.stopPropagation();
	}
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("div.main [id]");
		}
	}

	window.addEventListener('load', function() {
		
		selector().forEach(function(v) {
	    	// set up listeners
	    	v.removeEventListener("click", click);
	    	v.addEventListener("click", click);
	    })
	});

}