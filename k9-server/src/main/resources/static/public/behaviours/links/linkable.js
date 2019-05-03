import { getMainSvg } from '/public/bundles/screen.js';
import { Linker } from '/public/classes/linker.js';

/**
 * Contains the functionality for linking drawing links between selected elements 
 * and a target.
 */
export function initLinkable(selector)  {
	
	function move(event) {
		linker.move(event);
	}
	
	function end(event) {
		linker.end(event);
	}
	
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[id][k9-elem]");
		}
	}

	window.addEventListener('load', function(event) {
		selector().forEach(function(v) {
			v.removeEventListener("mousemove", move);
			v.removeEventListener("mouseup", end);
			v.addEventListener("mousemove", move);
			v.addEventListener("mouseup", end);
		})
	})
}
