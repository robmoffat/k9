import { getContextMenu, registerContextMenuCallback, destroyContextMenu } from "../context-menu.js";
import { transition } from "../../../bundles/transition.js"


export function initEditContextMenuCallback(transtion) {
	

	function createEditStep(e, text) {
		return {
			"type": 'EDIT',
			"arg1": e.getAttribute('id'),
			"arg2": text
		}
	}

	function getUri() {
		const href = document.URL;
		return href.replace(".html", ".xml")
	}	
	
	return function(event, cm) {
		
		const selectedElements = document.querySelectorAll("[id].selected.editable");
		
		if (selectedElements.length > 0) {
		
			var htmlElement = cm.get(event);
			
			var img = document.createElement("img");
			htmlElement.appendChild(img);
			
			img.setAttribute("title", "Edit");
			img.setAttribute("src", "../scripts/commands/edit/edit.svg");
			img.addEventListener("click", function(event) {
				
				const defaultText = document.querySelector(".lastSelected text").textContent;
							
				const newText = prompt("Enter New Text", defaultText)
				
				const steps = Array.from(selectedElements).map(e => createEditStep(e, newText));
				
				const data = {
					input: {
						uri: getUri()
					},
					steps: steps
				};
				
				cm.destroy();
				$.post({
					url: '/api/v1/command',
					data: JSON.stringify(data),
					dataType: 'xml',
					headers: {
						"Accept": "image/svg+xml"
					},
					contentType:"application/json; charset=utf-8",
					success: function(ob, status, jqXHR) {
						.transition.transition(ob.documentElement);
					}
				
				});
				
				console.log("edit complete");
			});
		}
		
		
		
	}
	
	
}

