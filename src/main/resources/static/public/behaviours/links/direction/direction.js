import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection } from '/public/bundles/api.js';

export function initDirectionContextMenuCallback(transition, selector) {
	
	function setDirection(e, direction, contextMenu) {
		contextMenu.destroy();
		const diagramId = getContainingDiagram(e).getAttribute("id");
		const id = e.getAttribute("id")

		const alignOnly = e.classList.contains("kite9-align");
		
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
	
	function drawDirection(event, cm, direction, selected) {
		var title, src;
		
		if (direction != "null") {
			title= "Link Direction ("+direction+")";
			src = "/public/behaviours/links/direction/"+direction.toLowerCase()+".svg";
		} else {
			title =  "Link Direction (undirected)";
			src =  "/public/behaviours/links/direction/undirected.svg";				
		}

		var a = cm.addControl(event, src, title);
		var img = a.children[0];
		
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
				const d2 = reverse ? reverseDirection(direction) : direction;
				var img = drawDirection(event, contextMenu, d2);
				if (contradicting) {
					img.style.backgroundColor = "#ff5956";
				}
				
				function handleClick() {
					contextMenu.clear(event);
					
					["null", "UP", "DOWN", "LEFT", "RIGHT"].forEach(s => {
						var img2 = drawDirection(event, contextMenu, s, d2);
						var s2 = reverse ? reverseDirection(s) : s;
						img2.addEventListener("click", () => setDirection(e, s2, contextMenu));
					});
				}
				
				img.addEventListener("click", handleClick);
			}
		}
	};
}