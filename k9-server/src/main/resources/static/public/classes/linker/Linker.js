import { getSVGCoords, getElementPageBBox, getMainSvg } from '/public/bundles/screen.js' ;
import { createUniqueId, parseInfo } from '/public/bundles/api.js'; 

/**
 * Provides functionality for drawing links on the diagram, and keeping track of them.
 */
export class Linker {
	 
	constructor(callbacks, svgLinkTemplate, getLinkTarget) {
		if (svgLinkTemplate == undefined) {
			// code to use first found link as the template
			this.svgLinkTemplate = function(svg) {
				const template = getMainSvg().querySelector("[k9-info~='link:'][id]");
				return template;
			};
		} else {
			this.svgLinkTemplate = svgLinkTemplate;
		}
		
		this.svg = getMainSvg();
		this.drawing = [];
		
		this.callbacks = callbacks == undefined ? [] : callbacks;
		
		if (getLinkTarget == undefined) {
			this.getLinkTarget = function(v) {
				if (v.hasAttribute("k9-ui") && v.hasAttribute("id")) {
					if (v.getAttribute("k9-ui").includes('connect')) {
						return v;
					} else {
						return null;
					}
				} else if (v == getMainSvg()) {
					return null;
				} else {
					return this.getLinkTarget(v.parentNode);
				}
			}
		} else {
			this.getLinkTarget = getLinkTarget;
		}
		
	}
	
	start(selectedElements, evt) {
		const template = this.svgLinkTemplate();
		
		Array.from(selectedElements).forEach(e => {
			var newLink = template.cloneNode(true);
			this.svg.appendChild(newLink);
			newLink.setAttribute("temp-from", e.getAttribute("id"));
			newLink.setAttribute("id", createUniqueId());
			newLink.classList.remove("selected");

			const bbox = getElementPageBBox(e)
			var from = { x: bbox.x + bbox.width/2, y: bbox.y + bbox.height/2 };
			this.drawing.push(newLink);
			newLink.start = from;
			newLink.setAttributeNS(null, 'pointer-events', 'none');
			this.setPath(newLink, from, this.mouseCoords);
		});
	}
	
	setPath(e, from, to) {
		const path = e.querySelectorAll("[k9-indicator=path] path").forEach(e => {
			e.setAttribute("d", "M"+from.x+" "+from.y+ "L"+to.x+" "+to.y);
		});
	}
	
	move(evt) {
		this.mouseCoords = getSVGCoords(evt);
		this.drawing.forEach(e => this.setPath(e, e.start, this.mouseCoords));
	}

	removeDrawingLinks() {
		this.drawing.forEach(e => {
			e.parentElement.removeChild(e);
		});
		this.drawing = [];
	}

	end(evt) {
		if (this.drawing.length == 0) {
			return;
		}
		
		this.callbacks.forEach(cb => cb(this, evt));
		
		evt.stopPropagation();
	}
	
	get() {
		return this.drawing;
	}
	
	clear() {
		this.drawing = [];
	}
}