/**
 * This file contains basic functionality to allow SVG diagrams to have behaviours and 
 * animate between different versions.
 */

import { shape, render, timeline, play } from '../libraries/wilderness.js' 
import "../libraries/jquery.min.js";


function reconcileAttributes(fromElement, toElement) {
	
	var toAtts = Array.from(toElement.attributes).map(a => a.name);
	var fromAtts = Array.from(fromElement.attributes).map(a => a.name);
	var toRemove = fromAtts.filter(a => -1 == toAtts.indexOf(a));
	
	toRemove.forEach(a => fromElement.removeAttribute(a));
	
	toAtts.forEach(a => {
		fromElement.setAttribute(a, toElement.getAttribute(a));
	});
}

function reconcileText(fromElement, toElement) {
	
	fromElement.textContent = toElement.textContent;
	
}

function merge(fromElement, toElement) {
	reconcileAttributes(fromElement, toElement);
	
	var fi = 0;
	var ti = 0;
	
	while (fromElement.childElementCount > toElement.childElementCount) {
		fromElement.children[fromElement.childElementCount-1].remove();
	}
	
	if (toElement.childElementCount == 0) {
		reconcileText(fromElement, toElement);
	}
	
	while (ti < toElement.childElementCount) {
		
		if (fi < fromElement.childElementCount) {
			// element on both sides.
			
			var newFromElement = fromElement.children[fi];
			var newToElement = toElement.children[ti];
			
			if (newFromElement.tagName == newToElement.tagName) {
				merge(newFromElement, newToElement);
				fi++;
				ti++;
			} else {
				console.log("Replacing: "+newFromElement.tagName+" "+newToElement)
				fromElement.insertBefore(newToElement, newFromElement)
				newFromElement.remove();
				fi++;
			}
			
		} else {
			var newToElement = toElement.children[ti];
			var newFromElement = document.createElementNS(newToElement.namespaceURI, newToElement.tagName)
			fromElement.appendChild(newFromElement);
			merge(newFromElement, newToElement)
		} 
	}
}

export function transition(documentElement) {
	merge(document.querySelector("body svg"), documentElement);
	
	// force the load event to occur again
	var evt = document.createEvent('Event');  
	evt.initEvent('load', false, false);  
	window.dispatchEvent(evt);
}