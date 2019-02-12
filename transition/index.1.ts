/**
 * This file contains basic functionality to allow SVG diagrams to have behaviours and 
 * animate between different versions.
 */

import { shape, render, timeline, play } from 'wilderness';
import { plainShapeObject } from 'wilderness-dom-node';

f

function reconcileText(fromElement: any, toElement: any) {
	
	fromElement.textContent = toElement.textContent;
	
}

function reconcileElement(fromElement: any, toElement: any) {

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
			const newToElement = toElement.children[ti];
			const newFromElement = document.createElementNS(newToElement.namespaceURI, newToElement.tagName)
			fromElement.appendChild(newFromElement);
			reconcileElement(newFromElement, newToElement)
		} 
	}
}

const canMorph = ["rect", "path","g"];

function merge(fromElement: any, toElement:any) {
	
	if (-1 != canMorph.indexOf(toElement.tagName)) {
		
		
		const kf1 = plainShapeObject(fromElement)
		const kf2 = plainShapeObject(toElement)
		const shapeOptions = { replace: fromElement }
	
		const morph = shape(kf1, kf2, shapeOptions)
		
		const playbackOptions = {
			alternate: true,
			duration: 50000,
			iterations: 1
		}
		
		const animation = timeline(morph, playbackOptions)
		
		render(document.querySelector('svg'), animation)
		
		return 
	} else {
		//reconcileElement(fromElement, toElement);
	}
}

export function transition(documentElement) {
	merge(document.querySelector("body svg"), documentElement);
	
	// force the load event to occur again
	var evt = document.createEvent('Event');  
	evt.initEvent('load', false, false);  
	window.dispatchEvent(evt);
}
