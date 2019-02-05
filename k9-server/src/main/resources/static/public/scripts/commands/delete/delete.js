import { getContextMenu, registerContextMenuCallback, destroyContextMenu } from "../context-menu.js";

/**
 * Provides a delete option for the context menu
 */
registerContextMenuCallback(function(domElement) {
	
	const selectedElements = document.querySelectorAll("[id].selected");
	
	if (selectedElements.length > 0) {
	
		var htmlElement = getContextMenu(domElement);
		
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		
		img.setAttribute("title", "Delete");
		img.setAttribute("src", "../scripts/commands/delete/delete.svg");
		img.addEventListener("click", function(event) {
			console.log("delete pressed");
			destroyContextMenu();
		});
	}
});
