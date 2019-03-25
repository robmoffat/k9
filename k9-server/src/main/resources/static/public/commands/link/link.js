
export function initLinkContextMenuCallback(transition, templateLinkFunction) {
	
	
	
	
}

function getTemplateLinkSVG(svg) {
	const template = document.querySelector("div.main [k9-info*=link][id]");
	linkTemplateUri = "#"+template.getAttribute("id");
	return template;
}

function getLinkTarget(v) {
	if (v.hasAttribute("k9-elem") && v.hasAttribute("id")) {
		if (parseInfo(v).connect) {
			return v;
		} else {
			return null;
		}
	} else if (v == svg) {
		return null;
	} else {
		return getLinkTarget(v.parentNode);
	}
}

function setPath(e, from, to) {
	const path = e.querySelector(".indicator-path path");
	path.setAttribute("d", "M"+from.x+" "+from.y+ "L"+to.x+" "+to.y);
}

function startDrawLink(evt) {
	destroyContextMenu();
	const selectedElements = document.querySelectorAll("[id][k9-info*='connect:'].selected");
	const template = getTemplateLinkSVG();
	const svg = getMainSvg();
	
	Array.from(selectedElements).forEach(e => {
		var newLink = template.cloneNode(true);
		svg.appendChild(newLink);
		newLink.setAttribute("temp-from", e.getAttribute("id"));
		newLink.setAttribute("id", createUniqueId())
		const bbox = getElementPageBBox(e)
		var from = { x: bbox.x + bbox.width/2, y: bbox.y + bbox.height/2 };
		drawing.push(newLink);
		newLink.start = from;
		newLink.setAttributeNS(null, 'pointer-events', 'none');
		setPath(newLink, from, mouseCoords);
	});
	
}



var drawing = [];
var svg;
var linkTemplateUri;
var mouseCoords;

const SVG_PATH_REGEX = /[MLQTCSAZ][^MLQTCSAZ]*/gi;

function draw(evt) {
	mouseCoords = getHtmlCoords(evt);
	drawing.forEach(e => setPath(e, e.start, mouseCoords));
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

function removeDrawingLinks() {
	drawing.forEach(e => {
		e.parentElement.removeChild(e);
	});
	drawing = [];
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
				uriStr: linkTemplateUri,
				fromId: e.getAttribute("temp-from"),
				toId: linkTargetId,
				linkId: e.getAttribute("id")
			}});
		
		postCommands(commands, getChangeUri());
		drawing = [];
	}
	
	evt.stopPropagation();
}


/**
 * Provides a delete option for the context menu
 */
registerActionableCallback(function(event) {
	
	const e = document.querySelector("[id].lastSelected.selected");
	const debug = parseInfo(e);
	
	if (debug.connect) {
		var htmlElement = getContextMenu(event);
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		img.setAttribute("title", "Draw Link");
		img.setAttribute("src", "/public/commands/link/link.svg");
		img.addEventListener("click", e => startDrawLink(event, e));
	}
});

/**
 * This should only be called once.  Adds the delete-key shortcut.
 */
document.addEventListener('keydown', function(event) {
	if (event.key == 'l') {
		startDrawLink(event, event);
	}
	
	if (event.key == 'Escape') {
		removeDrawingLinks();
	}
});

window.addEventListener('load', function(event) {

	svg = getMainSvg();

	svg.querySelectorAll("[id][k9-elem][k9-info*=connect]").forEach(function(v) {
		v.removeEventListener("mousemove", draw);
		v.removeEventListener("mouseup", end);
		v.addEventListener("mousemove", draw);
		v.addEventListener("mouseup", end);
	})
})
