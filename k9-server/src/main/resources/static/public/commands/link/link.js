import { getContextMenu, registerActionableCallback, destroyContextMenu } from "/public/behaviours/actionable/actionable.js";
import { transition, postCommands } from "/public/bundles/transition.js"
import { getChangeUri, parseDebug } from "/public/bundles/api.js";
import { getHtmlCoords, getMainSvg } from '/public/bundles/screen.js';


function getTemplateLinkSVG(svg) {
	const template = document.querySelector("div.main [debug*=link][id]");
	linkTemplateUri = "#"+template.getAttribute("id");
	return template;
}

export function getLinkTarget(v) {
	if (v.hasAttribute("kite9-elem") && v.hasAttribute("id")) {
		if (parseDebug(v).connect) {
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

function setPath(evt, e) {
	var endPoint = getHtmlCoords(evt);
	const path = e.querySelector(".indicator-path path");
	path.setAttribute("d", "M"+startPoint.x+" "+startPoint.y+ "L"+endPoint.x+" "+endPoint.y);
}

function startDrawLink(evt, evt2) {
	destroyContextMenu();
	const selectedElements = document.querySelectorAll("[id][debug*='connect:'].selected");
	const template = getTemplateLinkSVG();
	const coords = getHtmlCoords(evt);
	startPoint = coords;
	
	const svg = getMainSvg();
	
	Array.from(selectedElements).forEach(e => {
		var newLink = template.cloneNode(true);
		svg.appendChild(newLink);
		newLink.setAttribute("temp-from", e.getAttribute("id"));
		drawing.push(newLink);
		setPath(evt2, newLink);
	});
	
}

var drawing = [];
var startPoint = null;
var svg;
var linkTemplateUri;

const SVG_PATH_REGEX = /[MLQTCSAZ][^MLQTCSAZ]*/gi;

function draw(evt) {
	drawing.forEach(e => setPath(evt, e));
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
	} else if (elem.hasAttribute("kite9-elem")) {
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
		drawing.forEach(e => {
			e.parentElement.removeChild(e);
		});
		drawing = [];
	} else {
		const commands = drawing.map(e => 
			{ return {
				type: "CreateLink",
				fragmentId: diagramId,
				uriStr: linkTemplateUri,
				fromId: e.getAttribute("temp-from"),
				toId: linkTargetId
			}});
		
		postCommands(commands, getChangeUri());
		drawing.forEach(e => {
			e.parentElement.removeChild(e);
		});
		drawing = [];
	}
	
}


/**
 * Provides a delete option for the context menu
 */
registerActionableCallback(function(event) {
	
	const e = document.querySelector("[id].lastSelected");
	const debug = parseDebug(e);
	
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
});

window.addEventListener('load', function(event) {

	svg = getMainSvg();

	svg.querySelectorAll("[id][kite9-elem][debug*=connect]").forEach(function(v) {
		v.removeEventListener("mousemove", draw);
		v.removeEventListener("mouseup", end);
		v.addEventListener("mousemove", draw);
		v.addEventListener("mouseup", end);
	})
})
