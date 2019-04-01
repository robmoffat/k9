import { parseInfo, getChangeUri, getKite9Target } from '/public/bundles/api.js';
import { getSVGCoords, getElementPageBBox, getMainSvg } from '/public/bundles/screen.js';

export function initOrderingContextMenuCallback(transition) {
	
	function setLayout(e, layout, contextMenu) {
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
	
	function drawLayout(htmlElement, layout) {
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		
		if (layout == "null") {
			layout = "none";
		}
		img.setAttribute("title", "Layout ("+layout+")");
		img.setAttribute("src", "/public/commands/ordering/"+layout.toLowerCase()+".svg");
		img.style.backgroundColor = 'white';
		img.style.borderRadius = "0px";
		
		return img;
	}
	
	/**
	 * Provides a layout option for the context menu
	 */
	return function(event, contextMenu) {
		
		const e = document.querySelector("[id].lastSelected.selected");
		const debug = parseInfo(e);
		
		if (debug.layout) {			
			var htmlElement = contextMenu.get(event);
			var img = drawLayout(htmlElement, debug.layout);
			
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
				sep.setAttribute("src", "/public/commands/ordering/separator.svg")
				sep.setAttribute("class",'decoration');
				
				["none", "RIGHT", "DOWN", "HORIZONTAL", "VERTICAL", "GRID", "LEFT", "UP"].forEach(s => {
					var img2 = drawLayout(htmlElement, s);
					img2.addEventListener("click", () => setDirection(e, s, layout));
				});
			}
			
			img.addEventListener("click", handleClick);
		}
	};
	
	
	
}

export function initOrderingDragableMoveCallback() {
	
	var bar = null;

	function clearBar() {
		if (bar != null) {
			bar.parentNode.removeChild(bar);
			bar = null;
		}
	}

	function updateBar(event, inside, horiz) {
	    var fx, fy, tx, ty;
	    
	    var {x, y} = getSVGCoords(event);
	    
	    var contain = getElementPageBBox(inside);
	    
	    if (horiz) {
	        fx = contain.x;
	        tx = contain.x + contain.width;
	        fy = y;
	        ty = y;
	    } else {
	    	fx = x;
	    	tx = x;
	    	fy = contain.y;
	    	ty = contain.y + contain.height;
	    }
	   
		if (bar == null) {
			var svg = getMainSvg();
			bar = document.createElementNS("http://www.w3.org/2000/svg", "path");
			bar.style.stroke = '#b7c0fe';
			bar.style.strokeWidth = '6px';
			bar.setAttributeNS(null, 'pointer-events', 'none');
			svg.appendChild(bar);
		}
		
		bar.setAttribute("d", "M"+fx+" "+fy+" L "+tx+" "+ty);
	}
	
	return function(dragTargets, event) {		
		
		const dropInto = getKite9Target(event.target);
		const info = parseInfo(dropInto);
		const layout = info.layout;
		if ((layout == 'UP') || (layout == 'DOWN') || (layout == "VERTICAL ")) {
			// draw the horizontal bar
			updateBar(event, dropInto, true);
		} else if ((layout == 'LEFT') || (layout == 'RIGHT') || (layout == 'HORIZONTAL')) {
			updateBar(event, dropInto, false);			
		} else {
			clearBar();
		}
	}
	
}