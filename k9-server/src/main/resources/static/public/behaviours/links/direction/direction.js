import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection } from '/public/bundles/api.js';

export function initDirectionContextMenuCallback(transition, selector) {
	
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
			}]);
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
			}]);
		}
	}
	
	function drawDirection(htmlElement, direction, reverse, selected) {
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		
		if (direction != "null") {
			direction = reverse ? reverseDirection(direction) : direction;
			img.setAttribute("title", "Link Direction ("+direction+")");
			img.setAttribute("src", "/public/behaviours/links/direction/"+direction.toLowerCase()+".svg");
		} else {
			img.setAttribute("title", "Link Direction (undirected)");
			img.setAttribute("src", "/public/behaviours/links/direction/undirected.svg");				
		}
		
		if (selected == direction) {
			img.setAttribute("class", "selected");
		}
		
		return img;
	}
	
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[id][k9-info~='link:'].selected");
		}
	}
	
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const e = hasLastSelected(selector(), true);
		if (e) {
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
					Array.from(htmlElement.children).forEach(e => {
						htmlElement.removeChild(e);
					});
					
					["null", "UP", "DOWN", "LEFT", "RIGHT"].forEach(s => {
						var img2 = drawDirection(htmlElement, s, reverse, direction);
						img2.addEventListener("click", () => setDirection(e, s, contextMenu));
					});
				}
				
				img.addEventListener("click", handleClick);
			}
		}
	};
}