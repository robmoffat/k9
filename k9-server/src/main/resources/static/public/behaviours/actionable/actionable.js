/**
 * Allows the context menu to appear when the user clicks an element with an id
 */    
export function initActionable(contextMenu) {

	/**
	 * Displays a context menu when the user clicks on an element.
	 */
	function click(event) {
		contextMenu.destroy();
		contextMenu.handle(event);
		event.stopPropagation();
	}

	window.addEventListener('load', function() {
		
		document.querySelector("div.main").querySelectorAll("[id]").forEach(function(v) {
	    	// set up listeners
	    	v.removeEventListener("click", click);
	    	v.addEventListener("click", click);
	    })
	});

}