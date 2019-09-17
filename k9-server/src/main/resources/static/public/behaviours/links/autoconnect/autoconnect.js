import { getMainSvg, getElementPageBBox } from '/public/bundles/screen.js';
import { parseInfo, createUniqueId, getContainingDiagram, reverseDirection, getExistingConnections, getKite9Target, getCommonContainer } from '/public/bundles/api.js';

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
		link.style.stroke = '#b7c0fe';
		link.style.strokeWidth = '4px';
		link.style.strokeDasharray="4";
		link.setAttributeNS(null, 'pointer-events', 'none');
		svg.appendChild(link);
	}
	
	link.setAttribute("d", "M"+fx+" "+fy+" L "+tx+" "+ty);
}

export function initAutoConnectDropCallback(transition, templateUri) {
	
	function undoAlignment(transition, e) {
		const alignOnly = e.classList.contains("kite9-align");
		if (alignOnly) {
			transition.push({
				type: 'ADLDelete',
				fragmentId: e.getAttribute("id"),
				cascade: true
			});
		} else {
			transition.push({
				type: 'SetAttr',
				fragmentId: e.getAttribute("id"),
				name: 'direction'
			})
			
			return true;
		}		
	}
	
	function ensureNoDirectedLeavers(id, d1) {
		getExistingConnections(id).forEach(e => {
			const parsed = parseInfo(e);
			const d = parsed['direction'];
			const ids = parsed['link'];
			const reversed = ids[0] == id;
			const dUse = reversed ? reverseDirection(d1) : d1;
			
			if (d==dUse) {
				undoAlignment(transition, e);
			} 
		});
	}
		
	return function(dragTargets, event) {		
		if (link_to) {
			// create links between the selected object and the link_to one
			var id_from = dragTargets[0].getAttribute("id");
			var id_to = link_to.getAttribute("id");
			var existingLinks = getExistingConnections(id_from, id_to);

			ensureNoDirectedLeavers(id_from, link_d);
			const diagramId = getContainingDiagram(link_to).getAttribute("id");
			
			existingLinks = existingLinks.filter(e => undoAlignment(transition, e));
			
			if (existingLinks.length == 0) {
				// create a new link
				const linkId = createUniqueId();
				transition.push({
					fragmentId: diagramId,
					type: 'CopyLink',
					newId: linkId,
					fromId: id_from,
					toId: id_to,
					uriStr: templateUri,
					deep: true
				});
				
				transition.push({
					fragmentId: linkId,
					type: 'SetAttr',
					name: 'drawDirection',
					value: reverseDirection(link_d)
				});
			} else {
				const firstLink = existingLinks[0];
				const parsed = parseInfo(firstLink);
				const ids = parsed['link'];
				const reversed = ids[0] == id_to;
				const direction = reversed ? link_d : reverseDirection(link_d);
				transition.push({
					fragmentId: firstLink.getAttribute("id"),
					type: 'SetAttr',
					name: 'drawDirection',
					value: direction
				});
				transition.push({
					type: 'Move',
					fragmentId: diagramId,
					moveId: firstLink.getAttribute("id"),
				})
			}			
		}
	}
}

export function initAutoConnectMoveCallback(selector, canAutoConnect) {
	
	var maxDistance = 100;
	var width, height;
	
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[id][k9-ui~='autoconnect']");
		}
	}
	
	if (canAutoConnect == undefined) {
		canAutoConnect = function(moving, inside, linkTo) {
			
			if (moving) {
				var ui = moving.getAttribute("k9-ui");
				ui == undefined ? "" : ui;
				
				if (!ui.includes("autoconnect")) {
					return false;
				}
			}
			
			if (inside) {
				// check that we are allowed to auto-connect inside
				const target = getKite9Target(inside);
				const info = parseInfo(target);
				const layout = info.layout;
				if ((layout != null) && (layout != 'null')) {
					return false;
				}
				
				
				if (linkTo) {
					const target = getKite9Target(linkTo);
					const commonContainer = getCommonContainer(inside, linkTo);
					const commonInfo = parseInfo(commonContainer);
					const commonLayout = commonInfo.layout;
					if ((commonLayout != null) && (commonLayout != 'null')) {
						return false;
					}
				}
			}
		
			return true;
		}
	}
	
	function getElementsInAxis(coords, horiz) {
		
		const out = Array.from(selector())
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
		
		if (!canAutoConnect(draggingElement, event.target)) {
			clearLink();
			link_to = undefined;
			return;
		}
		
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
			link_to = undefined;
		} else if (!canAutoConnect(draggingElement, event.target, best)) {
			clearLink();
			link_to = undefined;
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

