import { getTrueCoords } from '/public/bundles/screen.js';

/**
 * Displays a context menu when the user clicks on an element.
 */
function click(event) {
	destroyContextMenu();
	callbacks.forEach(cb => cb(event));	
	event.stopPropagation();
}

var callbacks = [];


/**
 * Call this function to register a plugin menu-item.
 * Context Menu only works if the SVG is embedded in an HTML page.
 */
export function registerActionableCallback(p) {
		
	callbacks.push(p);	
}

/**
 * Creates the context menu within the main svg element,
 * positioning it relative to the event that created it.
 */
export function getContextMenu(event) {
	var ctxMenu = document.querySelector("#contextMenu");
	if (ctxMenu) {
		return ctxMenu;
	} else {
		ctxMenu = document.createElement("div");
		ctxMenu.setAttribute("id", "contextMenu");
		ctxMenu.setAttribute("class", "contextMenu");
		
		const coords = getTrueCoords(event);
		
		ctxMenu.style.left = (coords.x)+"px";
		ctxMenu.style.top = (coords.y)+"px";
		
		console.log(event);
		
		document.querySelector("body").appendChild(ctxMenu);
		return ctxMenu;
	}
	
}

/**
 * Removes the context menu from the screen.
 */
export function destroyContextMenu() {
	const ctxMenu = document.querySelector("#contextMenu");
	if (ctxMenu) {
		ctxMenu.parentElement.removeChild(ctxMenu);
	}
}

window.addEventListener('load', function() {
	
	document.querySelector("div.main").querySelectorAll("[id]").forEach(function(v) {
    	// set up listeners
    	v.removeEventListener("click", click);
    	v.addEventListener("click", click);
    })
})


/**
 * Ensures that the context-menu css is loaded up.
 */
var cssId = 'context-menu';  
if (!document.getElementById(cssId)) {
    var head  = document.getElementsByTagName('head')[0];
    var link  = document.createElement('link');
    link.id   = cssId;
    link.rel  = 'stylesheet';
    link.type = 'text/css';
    link.href = '/public/behaviours/actionable/context-menu.css';
    link.media = 'all';
    head.appendChild(link);
}