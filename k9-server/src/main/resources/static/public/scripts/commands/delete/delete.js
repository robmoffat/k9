import { getContextMenu, registerContextMenuCallback, destroyContextMenu } from "../context-menu.js";
import { transition } from "../../../bundles/transition.js"

function createDeleteStep(e) {
	return {
		"type": 'DELETE',
		"arg1": e.getAttribute('id')
	}
}

function getUri() {
	const href = document.URL;
	return href.replace(".html", ".xml")
}

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
			const steps = Array.from(selectedElements).map(e => createDeleteStep(e));
			
			const data = {
				input: {
					uri: getUri()
				},
				steps: steps
			};
			
			destroyContextMenu();
			$.post({
				url: '/api/v1/command',
				data: JSON.stringify(data),
				dataType: 'xml',
				headers: {
					"Accept": "image/svg+xml"
				},
				contentType:"application/json; charset=utf-8",
				success: function(ob, status, jqXHR) {
					transition(ob.documentElement);
				}
			
			});
			
			console.log("delete complete");
		});
	}
});
