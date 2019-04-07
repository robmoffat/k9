import { getSVGCoords, getMainSvg } from '/public/bundles/screen.js';
import { handleTransformAsStyle, getKite9Target, isTerminator, isLink, isConnected } from '/public/bundles/api.js';

export function initDragable(moveCallbacks, dropCallbacks, selector, isDragable, canDropHere) {
	
	var svg = null;

	// keeps track of the current drag
	var dragOrigin = null;
	var state = null;
	var shapeLayer = null;
	var mouseDown = false;
	var delta = null;

	function dragAttribute(v) {
		var out = v.getAttribute("drag");
		return out == null ? "" : out;
	}
	
	if (isDragable == undefined) {
		isDragable = function(v) {
			var out = v.getAttribute("k9-ui");
			return out == null ? false : out.includes("drag");
		} 
	}

	function getDragTarget(v) {
		if (isDragable(v)) {
			return v;
		} else if (v == null) {
			return null;
		} else {
			return getDragTarget(v.parentNode);
		}
	}

	function getTranslate(dt) {
		if (dt == svg) {
			return {x: 0, y: 0};
		}
		
		var transMatrix = dt.getCTM();

		var out = {
				x: Number(transMatrix.e), 
				y: Number(transMatrix.f)
			};
		
		//console.log(dt+" "+out);
		return out;
	}

	function getDifferentialTranslate(dt) {
		var transMatrix = getTranslate(dt);
		var parentMatrix = getTranslate(dt.parentElement);
		
		var out = {
			x: transMatrix.x - parentMatrix.x, 
			y: transMatrix.y - parentMatrix.y
		}
		
		return out;
	}

	function beginMove(evt) {
		var out = []
		
		svg.querySelectorAll("[id].selected,.mouseover").forEach(e => {
			
			e = getDragTarget(e);
			
			if (e) {
				
				handleTransformAsStyle(e);
				
				if (shapeLayer == null) {
					shapeLayer = document.createElementNS("http://www.w3.org/2000/svg", "g");
					shapeLayer.setAttributeNS(null, 'pointer-events', 'none');
					shapeLayer.setAttribute("id", "--moveLayer");
					svg.appendChild(shapeLayer);
				}
				
				var shapeOrigin = getTranslate(e);
				var embeddedShapeOrigin = getDifferentialTranslate(e);
				
				out.push({
					dragTarget: e,
					dragParent: e.parentElement,
					dragBefore: e.nextSibling,
					shapeOrigin: shapeOrigin,
					embeddedShapeOrigin: embeddedShapeOrigin
				})
				
				shapeLayer.appendChild(e);
				e.setAttributeNS(null, 'pointer-events', 'none');
				e.style.setProperty('transform', 'translateX(' + shapeOrigin.x + 'px) translateY('
						+ shapeOrigin.y + 'px)');
			}
		});
		
		if (out.length > 0) {
			dragOrigin = getSVGCoords(evt);
			
			// make sure the order of the state is such that we don't run 
			// into trouble with insertBefore.
			
			var keepGoing = true;
			while (keepGoing) {
				keepGoing = false;
				var ordered = out.map(s => s.dragTarget);
				for (var i = 0; i < out.length; i++) {
					const antecedent = ordered.indexOf(out[i].dragBefore);
					if (antecedent > i) {
						var removed = out.splice(antecedent, 1);
						out.unshift(removed[0]);
						keepGoing = true;
					} 
				}
			}
			
			state = out;
		}
		
	}

	function grab(evt) {
		mouseDown = true;
	}
	 
	function drag(evt) {
		if (!mouseDown) {
			return;
		}
		
		if (!state) {
			beginMove(evt);
		}
		
		if (state) {
			// calculate move in true coords
			var trueCoords = getSVGCoords(evt);
			delta = {
					x:  trueCoords.x - dragOrigin.x, 
					y:  trueCoords.y - dragOrigin.y
			};

			// apply a new tranform translation to the dragged element, to display
			//    it in its new location
			shapeLayer.style.setProperty('transform', 'translateX(' + delta.x + 'px) translateY('
					+ delta.y + 'px)');
			
			moveCallbacks.forEach(mc => mc(state.map(s => s.dragTarget), evt));
		} 
	}

	function endMove(reset) {
		if (state) {
			state.forEach(s => {
				s.dragParent.insertBefore(s.dragTarget, s.dragBefore);
				
				var x = s.embeddedShapeOrigin.x + ( reset ? 0 : delta.x );
				var y = s.embeddedShapeOrigin.y + ( reset ? 0 : delta.y );
				
				s.dragTarget.style.setProperty('transform', 'translateX(' + x + 'px) translateY('
						+ y + 'px)');
				s.dragTarget.setAttributeNS(null, 'pointer-events', 'all');
			})
			
			state = null;
			svg.removeChild(shapeLayer);
			svg.style.cursor = null;
			shapeLayer = null;
			dragOrigin = null;
		}
	}

	function drop(evt) {
		// if we aren't currently dragging an element, don't do anything
		if (state) {
			const dragTargets = state.map(s => s.dragTarget);
			const dropTarget = getKite9Target(evt.target);
			const canDrop = canDropAllHere(dragTargets, dropTarget)
			var result = dropCallbacks
				.map(dc => dc(dragTargets, evt, canDrop, dropTarget, isDragable))
				.reduce((a,b) => (a | b), false);
			
			endMove(!result);
		}
		
		mouseDown = false;
		svg.style.cursor = undefined;
	};

	/**
	 * In this case, we look for the 'drop' attribute, and check that there is 
	 * some intersection with the items in the 'drag' attribute.
	 */
	if (canDropHere == undefined) {
		
		function intersects(a, b) {
		    return a.map(e => b.indexOf(e) > -1).reduce((a,b) => a||b, false);
		}

		canDropHere = function(dragTarget, dropTarget) {
			if (dropTarget == null) {
				return false;
			}
			
			var out = dropTarget.getAttribute("k9-ui");
			if (!out.includes("drop")) {
				return false;
			}
			
			if (isTerminator(dragTarget)) {
				return (isConnected(dropTarget));
			}
			
			return true;
		};
	}

	function canDropAllHere(dragTargets, dropTarget) {
		return dragTargets
			.map(dt => canDropHere(dt, dropTarget))
			.reduce((a,b) => a&&b, true);
	}

	if (moveCallbacks == undefined) { moveCallbacks = [] };
	if (dropCallbacks == undefined) { dropCallbacks = [] }

	dropCallbacks.push(function(dragTargets, evt) {

		var targetElement = getKite9Target(evt.target);
		
		if (targetElement == null) {
			endMove(true);
		} else {
			console.log(dragTargets + ' has been dropped on top of ' + targetElement.id);
		}

		return false;
	});

	moveCallbacks.push(function(dragTargets, evt) {
		const dropTarget = getKite9Target(evt.target);
		
		const canDrop = canDropAllHere(dragTargets, dropTarget);
		
		if (canDrop) {
			svg.style.cursor = "grabbing";
		} else {
			svg.style.cursor = "not-allowed";
		}
	});
	
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[id][k9-elem]");
		}
	}
	
	window.addEventListener('load', function(event) {

		svg = getMainSvg();
		svg.removeEventListener("mousemove", drag);
		svg.removeEventListener("mouseup", drop);
		svg.addEventListener("mousemove", drag);
		svg.addEventListener("mouseup", drop);

		selector().forEach(function(v) {
			v.removeEventListener("mousemove", drag);
			v.removeEventListener("mouseup", drop);
			v.removeEventListener("mousedown", grab);
			v.addEventListener("mousedown", grab);
			v.addEventListener("mousemove", drag);
			v.addEventListener("mouseup", drop);
		})
	})
}




