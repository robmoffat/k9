import { getContextMenu, registerContextMenuCallback, destroyContextMenu } from "../context-menu.js";
import { transition } from "../../../bundles/transition.js"
import '../../../libraries/jquery.min.js'


function createInsertStep(e, url) {
	return {
		"type": 'INSERT',
		"arg1": e.getAttribute('id'),
		"arg2": url
	}
}

function getUri() {
	const href = document.URL;
	return href.replace(".html", ".xml")
}

/**
 * Creates the context menu within the main svg element,
 * positioning it relative to the event that created it.
 */
function getPalette(event) {
	var palette = document.querySelector("#palette");
	if (palette) {
		return palette;
	} else {
		palette = document.createElement("div");
		palette.setAttribute("id", "palette");
		palette.setAttribute("class", "palette");
		document.querySelector("body").appendChild(palette);
		
		$(palette).load("http://localhost:8080/public/landing/one.svg", function(rt, textStatus, request) {
			
			
			
			
		});
		
		return palette;
	}
	
}


/**
 * Provides a delete option for the context menu
 */
registerContextMenuCallback(function(event) {
	
	const selectedElements = document.querySelectorAll("[id].selected.editable");
	
	if (selectedElements.length > 0) {
	
		var htmlElement = getContextMenu(event);
		
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		
		img.setAttribute("title", "Edit");
		img.setAttribute("src", "../scripts/commands/palette/palette.svg");
		img.addEventListener("click", function(event) {
			
			getPalette(event);
			
			
		});
	}
});
