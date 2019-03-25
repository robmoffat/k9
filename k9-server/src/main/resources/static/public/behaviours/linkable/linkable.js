/**
 * Contains the functionality for linking drawing links between selected elements 
 * and a target.
 */
export function initLinkable(linker)  {
	
	var svg = document.querySelector("div.main svg");

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

	window.addEventListener('load', function(event) {
		svg.querySelectorAll("[id][k9-elem][k9-info*=connect]").forEach(function(v) {
			v.removeEventListener("mousemove", move);
			v.removeEventListener("mouseup", end);
			v.addEventListener("mousemove", move);
			v.addEventListener("mouseup", end);
		})
	})
}







/**
 * Returns the highest element with the kite9 namespace.
 */
function getContainingDiagram(elem) {
	if (elem == null) {
		return null;
	}
	const pcd = getContainingDiagram(elem.parentElement);
	if (pcd) {
		return pcd;
	} else if (elem.hasAttribute("k9-elem")) {
		return elem;
	}
}


function end(evt) {
	if (drawing.length == 0) {
		return;
	}
	
	const linkTarget = getLinkTarget(evt.target);
	const diagramId = getContainingDiagram(linkTarget).getAttribute("id");
	const linkTargetId = linkTarget.getAttribute("id");
	
	if (linkTarget == null) {
		removeDrawingLinks();
	} else {
		const commands = drawing.map(e => 
			{ return {
				type: "CreateLink",
				fragmentId: diagramId,
				uriStr: linkTemplateUri(),
				fromId: e.getAttribute("temp-from"),
				toId: linkTargetId,
				linkId: e.getAttribute("id")
			}});
		
		postCommands(commands, getChangeUri());
		drawing = [];
	}
	
	evt.stopPropagation();
}

