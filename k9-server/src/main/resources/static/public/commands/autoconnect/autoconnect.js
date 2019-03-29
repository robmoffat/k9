import { getMainSvg, getElementPageBBox } from '/public/bundles/screen.js';

function getElementsInAxis(coords, horiz) {
	
	const out = Array.from(document.querySelectorAll("div.main svg [id][k9-info*='connect:']"))
		.filter(e => {
			var {x, y, width, height} = getElementPageBBox(e);
			
			if (!horiz) {
				return ((y <= coords) && (y+height >= coords));
			} else {
				return ((x <= coords) && (x+width >= coords));
			}
		});
	
	return out;
}

var link = null;
var link_to = undefined;
var link_d = undefined;

function clearLink() {
	if (link != null) {
		link.parentNode.removeChild(link);
		link = null;
	}
}

function updateLink(tx, ty, frompos, link_d) {
    var fx, fy;
    
    if (link_d == 'LEFT') {
        fy = ty;
        fx = frompos.x;
    } else if (link_d == 'RIGHT') {
        fy = ty;
        fx = frompos.x + frompos.width;  
    } else if (link_d == 'UP') {
    	fx = tx;
    	fy = frompos.y;
    } else {
    	fx = tx;
    	fy = frompos.y + frompos.height;
    }
   
	if (link == null) {
		var svg = getMainSvg();
		link = document.createElementNS("http://www.w3.org/2000/svg", "path");
		svg.appendChild(link);
	}
	
	link.style.stroke = 'black';
	link.setAttribute("d", "M"+fx+" "+fy+" L "+tx+" "+ty);
}


export function createAlignDragableDropCallback(transition) {
	
	return function(dragTargets, event) {
		
	}
}


export function createAlignDragableMoveCallback() {
	
	var maxDistance = 100;
	var width, height;
	
		/**
	 * This function looks for stuff to connect to and shows links on screen to demonstrate this
	 */
	return function(dragTargets, event) {
		
		function alreadyDragging(e) {
			if (dragTargets.indexOf(e) != -1) {
				return true;
			} 
			
			if (e.parentNode == null) {
				return false;
			} else {
				return alreadyDragging(e.parentNode);
			}			
		}
		
		function outside(a, b) {
			return ((a.x + a.width < b.x) 
					|| (a.x > b.x + b.width)
					|| (a.y + a.height < b.y) 
					|| (a.y > b.y + b.height));
		}

		if (dragTargets.length > 1) {
			clearLink();
			link_to = undefined;
			return;
		}
		
		var draggingElement = dragTargets[0];
		
		var pos = getElementPageBBox(draggingElement);
		
		var x = pos.x + (pos.width / 2);
		var y = pos.y + (pos.height /2);

		var best = undefined;
		var best_dist = undefined;
		var best_d  = undefined;
		
		getElementsInAxis(y, false).forEach(function(k, c) {
			if (!alreadyDragging(k)) {
				var v = getElementPageBBox(k);
				
				if (outside(pos, v) && (y <= v.y + v.height) && (y >= v.y)) {
					// intersection on y position
					var d, dist;
					if (v.x + v.width < x) {
						dist = pos.x - v.x - v.width;
						d = 'RIGHT';
					} else if (v.x > x) {
						dist = v.x - pos.x - pos.width;
						d = 'LEFT';
					} else {
						dist = maxDistance +1;
						d = null;
					}
								
					if (best_dist) {
						if (dist > best_dist) {
							return;
						}
					}
						
					best = k;
					best_dist = dist;
					best_d = d;
				}
			}
		});
			
		getElementsInAxis(x, true).forEach(function(k, c) {
			if (!alreadyDragging(k)) {
				var v = getElementPageBBox(k);

				if (outside(pos, v) && (x <= v.x+v.width) && (x >= v.x)) {
					// intersection on x position
					var d, dist;
					if (v.y + v.height < y) {
						dist = pos.y - v.y - v.height;
						d = 'DOWN';
					} else if (v.y > y) {
						dist = v.y - pos.y - pos.height;
						d = 'UP';
					} else {
						dist = maxDistance +1;
						d = null;
					}
					
					if (best_dist) {
						if (dist > best_dist) {
							return;
						}
					}
						
						
					best = k;
					best_dist = dist;
					best_d = d;
				}
			}
		});

		
		if (best_dist > maxDistance){
			best = undefined;
		}
				
		if (best === undefined) {
			clearLink();
		} else if (best === link_to) {
			link_d = best_d;
			updateLink(x, y, getElementPageBBox(best), link_d);	
		} else {
			clearLink();
			link_to = best;
			link_d = best_d;
			updateLink(x, y, getElementPageBBox(best), link_d);
		}
	}

}