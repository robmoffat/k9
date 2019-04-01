import { getMainSvg, getElementPageBBox } from '/public/bundles/screen.js';
import { parseInfo, createUniqueId, getChangeUri, getContainingDiagram, reverseDirection, getExistingConnections, getKite9Target } from '/public/bundles/api.js';

function getElementsInAxis(coords, horiz) {
	
	const out = Array.from(document.querySelectorAll("div.main svg [id][align~='yes']"))
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
		link.style.stroke = '#b7c0fe';
		link.style.strokeWidth = '4px';
		link.style.strokeDasharray="4";
		link.setAttributeNS(null, 'pointer-events', 'none');
		svg.appendChild(link);
	}
	
	link.setAttribute("d", "M"+fx+" "+fy+" L "+tx+" "+ty);
}

export function createAutoConnectDragableDropCallback(transition, templateUri) {
	
	function undoAlignment(transition, e) {
		const alignOnly = e.classList.contains("align");
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
			const link = parsed['link'];
			const ids = link.split(" ");
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
					linkId: linkId,
					fromId: id_from,
					toId: id_to,
					uriStr: templateUri
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
				const link = parsed['link'];
				const ids = link.split(" ");
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

export function createAutoConnectDragableMoveCallback() {
	
	var maxDistance = 100;
	var width, height;
	
	function canAutoConnect(e, dropTarget) {
		var info = parseInfo(getKite9Target(dropTarget));
		var layout = info.layout;
		
		if ((layout == null) || (layout == 'null')) {
			var info = parseInfo(e);
			return info['connect'];
		} else {
			// we don't do auto-connect inside directed containers. Too confusing.
			return false;
		}
		
		
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

export function initAutoConnectContextMenuCallback(transition) {
	
	function setDirection(e, direction, contextMenu) {
		contextMenu.destroy();
		const diagramId = getContainingDiagram(e).getAttribute("id");
		const id = e.getAttribute("id")

		const alignOnly = e.classList.contains("align");
		
		if (alignOnly && (direction == 'null')) {
			transition.postCommands([{
					type: 'ADLDelete',
					fragmentId: e.getAttribute("id"),
					cascade: true
			}], getChangeUri());
		} else {
			if (direction == 'null') {
				// causes the attribute to be removed.
				direction = null;	
			} 
			
			transition.postCommands([{
				fragmentId: id,
				type: 'SetAttr',
				name: 'drawDirection',
				value: direction
			},{
				type: 'Move',
				fragmentId: diagramId,
				moveId: id,
			}], getChangeUri());
		}
	}
	
	function drawDirection(htmlElement, direction, reverse) {
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		
		if (direction != "null") {
			direction = reverse ? reverseDirection(direction) : direction;
			img.setAttribute("title", "Link Direction ("+direction+")");
			img.setAttribute("src", "/public/commands/autoconnect/"+direction.toLowerCase()+".svg");
		} else {
			img.setAttribute("title", "Link Direction (undirected)");
			img.setAttribute("src", "/public/commands/autoconnect/undirected.svg");				
		}
		
		return img;
	}
	
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const e = document.querySelector("[id].lastSelected.selected");
		const debug = parseInfo(e);
		const direction = debug.direction;
		
		if (debug.link) {
			const contradicting = debug.contradicting == "yes";
			const reverse = contradicting ? false : (debug.direction == 'LEFT' || debug.direction == 'UP');
			
			var htmlElement = contextMenu.get(event);
			var img = drawDirection(htmlElement, direction, reverse);
			if (contradicting) {
				img.style.backgroundColor = "#ff5956";
			}
			
			function handleClick() {
				img.removeEventListener("click", handleClick)
				img.style.opacity = "0.5";
				
				// remove the other stuff from the context menu
				Array.from(htmlElement.children).forEach(e => {
					if (e != img) {
						htmlElement.removeChild(e);
					}
				});
				
				var sep = document.createElement("img");
				htmlElement.appendChild(sep);
				sep.style.opacity = "0.5";
				sep.setAttribute("src", "/public/commands/autoconnect/separator.svg")
				sep.setAttribute("class",'decoration');
				
				["null", "UP", "DOWN", "LEFT", "RIGHT"].forEach(s => {
					var img2 = drawDirection(htmlElement, s, reverse);
					img2.addEventListener("click", () => setDirection(e, s, contextMenu));
				});
			}
			
			img.addEventListener("click", handleClick);
		}
	};
}