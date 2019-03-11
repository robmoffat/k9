import { getContextMenu, registerContextMenuCallback, destroyContextMenu } from "/public/commands/context-menu.js";
import { transition, postCommands } from "/public/bundles/transition.js"
import { SHA1 } from "/public/bundles/sha1.js";
import { getChangeUri } from "/public/bundles/api.js";

/**
 * Takes a node and creates a delete command.
 */
function createDeleteStep(id, keepChildren) {
//	var serializer = new XMLSerializer();
//	var string = serializer.serializeToString(e);
//	var hash = SHA1(string);
//	console.log("String: "+string);
//	console.log("Hash: "+hash);
	
	return {
		fragmentHash: '', 
		fragmentId: id,
		type: 'Delete'
	};
}

function getReferences(id) {
	return Array.from(document.querySelectorAll("[reference="+id+"]"))
		.map(e => e.parentElement)
		.map(e => e.getAttribute("id"));
}

function onlyUnique(value, index, self) { 
    return self.indexOf(value) === index;
}


/**
 * Provides a delete option for the context menu
 */
registerContextMenuCallback(function(event) {
	
	const selectedElements = document.querySelectorAll("[id].selected");
	
	if (selectedElements.length > 0) {
	
		var htmlElement = getContextMenu(event);
		
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		
		img.setAttribute("title", "Delete");
		img.setAttribute("src", "/public/commands/delete/delete.svg");
		img.addEventListener("click", function(event) {
			const ids = Array.from(selectedElements).map(e => e.getAttribute('id'));
			const refIds = ids.flatMap(id => getReferences(id));
			const allIds = ids.concat(refIds).filter(onlyUnique);
			const steps = allIds.map(id => createDeleteStep(id));
			
			destroyContextMenu();
			postCommands(steps, getChangeUri());
			console.log("delete complete");
		});
	}
});
