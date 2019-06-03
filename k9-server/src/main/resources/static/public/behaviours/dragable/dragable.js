import { getSVGCoords, getMainSvg } from '/public/bundles/screen.js';
import { handleTransformAsStyle, getKite9Target, isConnected, isDiagram, getParentElement } from '/public/bundles/api.js';
import { getBeforeId } from '/public/bundles/ordering.js';


export function initDragable(dragger, selector, css) {
	
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[id][k9-elem]");
		}
	}

	if (css == undefined) {
		css = '/public/behaviours/dragable/dragable.css';
	}
	
	window.addEventListener('load', function(event) {

		var svg = getMainSvg();
		svg.removeEventListener("mousemove", (e) => dragger.drag(e));
		svg.removeEventListener("mouseup", (e) => dragger.drop(e));
		svg.addEventListener("mousemove", (e) => dragger.drag(e));
		svg.addEventListener("mouseup", (e) => dragger.drop(e));

		selector().forEach(function(v) {
			v.removeEventListener("mousemove", (e) => dragger.drag(e));
			v.removeEventListener("mouseup", (e) => dragger.drop(e));
			v.removeEventListener("mousedown", (e) => dragger.grab(e));
			v.addEventListener("mousedown", (e) => dragger.grab(e));
			v.addEventListener("mousemove", (e) => dragger.drag(e));
			v.addEventListener("mouseup", (e) => dragger.drop(e));
		})
	})
}

function basicIsDragable(v) {
	var out = v.getAttribute("k9-ui");
	return out == null ? false : out.includes("drag");
}

/**
 * Returns the objects that are selected being dragged
 */
export function initDragableDragLocator(selector) {
	
	if (selector == undefined) {
		selector = function() {
			return Array.from(document.querySelectorAll("[id][k9-ui~='drag'].selected, [id][k9-ui~='drag'].mouseover"))
		}
	}
	
	return function() {
		var out = selector();
		return out;
	}
 	
}

export function initCompleteDragable(transition) {
	
	return function() {
		transition.postCommandList();
	}
}

export function initDragableDropLocator() {
	
	const svg = getMainSvg();
	
	var lastDropTarget = null;
	var lastDragIds = null;
	
	function dragIds(dragTargets) {
		return dragTargets
			.map(dt => dt.getAttribute("id"))
			.reduce((a,b) => a+","+b, "");
	}
	
	function canDropHere(dragTarget, dropTarget) {
		if (dropTarget == null) {
			return false;
		}
		
		if (dragTarget==dropTarget) {
			return false;
		}
		
		if ((!isConnected(dropTarget)) && ((!isDiagram(dropTarget)))) {
			return false;
		}
		
		var out = dropTarget.getAttribute("k9-ui");
		if (!out.includes("drop")) {
			return false;
		}

		return true;
	};

	return function(dragTargets, evt) {
		var dropTarget = getKite9Target(evt.target);

		while (dropTarget) {
			const ok = dragTargets
				.map(dt => canDropHere(dt, dropTarget))
				.reduce((a,b) => a&&b, true);
		
			if (ok) {
				lastDragIds = dragIds(dragTargets);
				lastDropTarget = dropTarget;
				return [ dropTarget ];
			} else if (isConnected(dropTarget)) {
				dropTarget = getParentElement(dropTarget);
			} else {
				dropTarget = null;
			}
		}
		
		// if the drag targets are the same as before, we may be
		// hovering over a link, so keep dropTarget the same.
		if (lastDragIds == dragIds(dragTargets)) {
			return [ lastDropTarget ]
		} 
		
		return [ ];
	}	
}

export function initDragableDropCallback(transition) {
	
	return function(dragTargets, evt, dropTargets) {
		const connectedDropTargets = dropTargets.filter(t => isConnected(t) || isDiagram(t));
		
		if (connectedDropTargets.length == 1) {
			const dropTarget = connectedDropTargets[0];
			var beforeId = getBeforeId(dropTarget, evt, dragTargets);
			Array.from(dragTargets).forEach(dt => {
				if (isConnected(dt)) {
					transition.push( {
						type: 'Move',
						fragmentId: dropTarget.getAttribute('id'),
						moveId: dt.getAttribute('id'),
						beforeFragmentId: beforeId
					});
				}	
			});
			return true;
		} 
	}
}

