import { getSVGCoords, getMainSvg } from '/public/bundles/screen.js';
import { handleTransformAsStyle, getKite9Target, isConnected, getParentElement } from '/public/bundles/api.js';
import { ensureCss } from '/public/bundles/css.js';
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
	
	ensureCss(css);
	
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

export function initDragableDragLocator(isDragable) {

	if (isDragable == undefined) {
		isDragable = function(v) {
			var out = v.getAttribute("k9-ui");
			return out == null ? false : out.includes("drag");
		} 
	}
	
	return function(evt) {
		var v = evt.currentTarget;
		
		while (v) {
			if (isDragable(v)) {
				return  [ v ];
			} else if (v.tagName == 'svg') {
				return [];
			} 
			
			v = v.parentNode;
		}

		return [];

	}
}

export function initCompleteDragable(transition) {
	
	return function() {
		transition.postCommandList();
	}
}

export function initDragableDropLocator() {
	
	const svg = getMainSvg();
	
	function canDropHere(dragTarget, dropTarget) {
		if (dropTarget == null) {
			return false;
		}
		
		var out = dropTarget.getAttribute("k9-ui");
		if (!out.includes("drop")) {
			return false;
		}
//		
//		if (isTerminator(dragTarget)) {
//			return (isConnected(dropTarget));
//		}
//		
		return true;
	};

	return function(dragTargets, evt) {
		var dropTarget = getKite9Target(evt.target);

		while (dropTarget) {
			const ok = dragTargets
				.map(dt => canDropHere(dt, dropTarget))
				.reduce((a,b) => a&&b, true);
		
			if (ok) {
				return dropTarget;
			} else {
				dropTarget = getParentElement(dropTarget);
			}
		}
		
		return null;
	}	
}

export function initDragableDropCallback(transition) {
	
	return function(dragTargets, evt, dropTarget) {
		if (dropTarget) {
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

