import { getHtmlCoords } from '/public/bundles/screen.js';
import { ensureCss } from '/public/bundles/css.js';
import { icon } from '/public/bundles/form.js';

/**
 * Provides functionality for populating the context menu.  Takes a number of callbacks
 * that provide functionality when the user asks the context menu to appear.
 */
export class ContextMenu {

	constructor(cb) {
		this.callbacks = cb == undefined ? [] : cb;
		
		ensureCss('/public/classes/context-menu/context-menu.css')
	}
	
	/**
	 * Creates the context menu within the main svg element,
	 * positioning it relative to the event that created it.
	 */
	get(event) {
		var ctxMenu = document.querySelector("#contextMenu");
		if (ctxMenu) {
			return ctxMenu;
		} else {
			ctxMenu = document.createElement("div");
			ctxMenu.setAttribute("id", "contextMenu");
			ctxMenu.setAttribute("class", "contextMenu");
			//ctxMenu.addEventListener("click", () => this.destroy());
			
			const coords = getHtmlCoords(event);
			
			ctxMenu.style.left = (coords.x + 15)+"px";
			ctxMenu.style.top = (coords.y-20)+"px";
			
			//console.log(event);
			
			document.querySelector("body").appendChild(ctxMenu);
			return ctxMenu;
		}
	}
	
	/**
	 * Call this when the user clicks on an element that might need a context menu.
	 */
	handle(event) {
		this.callbacks.forEach(cb => cb(event, this));	
	}
	
	/**
	 * Removes the context menu from the screen.
	 */
	destroy() {
		const ctxMenu = document.querySelector("#contextMenu");
		if (ctxMenu) {
			ctxMenu.parentElement.removeChild(ctxMenu);
		}
	}

	addControl(event, imageUrl, title, clickListener) {
		var htmlElement = this.get(event);
		var out = icon('--cm-'+title, title, imageUrl, clickListener)
		htmlElement.appendChild(out);
		return out;
	}
	
	/**
	 * Removes all the content from the context menu
	 */
	clear(event) {
		var htmlElement = this.get(event);
		Array.from(htmlElement.children).forEach(e => {
			htmlElement.removeChild(e);
		});
	}

}
