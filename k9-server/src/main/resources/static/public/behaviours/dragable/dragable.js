import { getTrueCoords } from '/public/bundles/screen.js';

/**
 * Drag and drop is controlled by the 'drag' attribute:
 *  - 'yes': the element can be dragged.   
 *  - 'target':  you can drop here.
 *  - 'from': it's drag-able, but it's the from-end of a link
 *  - 'to: it's drag-able, but it's the to-end of a link.
 *  - 'link': you can link to it.
 */

var svg = null;

// keeps track of the current drag
var dragOrigin = null;
var state = null;
var shapeLayer = null;
var mouseDown = false;
var delta = null;

var moveCallbacks= [];
var dropCallbacks = [];


export function dragAttribute(v) {
	var out = v.getAttribute("drag");
	return out == null ? "" : out;
}

function isDragable(v) {
	return dragAttribute(v).includes("yes");
}


function getDragTarget(v) {
	if (isDragable(v)) {
		return v;
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
	
	console.log(dt+" "+out);
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
		if (isDragable(e)) {
			
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
			e.setAttributeNS(null, 'transform', 'translate(' + shapeOrigin.x + ','
					+ shapeOrigin.y + ')');
		}
	});
	
	if (out.length > 0) {
		dragOrigin = getTrueCoords(evt);
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
		var trueCoords = getTrueCoords(evt);
		var changeTrueCoords = {
				x:  trueCoords.x - dragOrigin.x, 
				y:  trueCoords.y - dragOrigin.y
		};
		
		// calculate move in shape coords
		delta = {
				x: changeTrueCoords.x * svg.currentScale,
				y: changeTrueCoords.y * svg.currentScale
		}

		// apply a new tranform translation to the dragged element, to display
		//    it in its new location
		shapeLayer.setAttributeNS(null, 'transform', 'translate(' + delta.x + ','
				+ delta.y + ')');
		
		moveCallbacks.forEach(mc => mc(state.map(s => s.dragTarget), evt));
	} 
}

export function endMove(reset) {
	if (state) {
		state.forEach(s => {
			s.dragParent.insertBefore(s.dragTarget, s.dragBefore);
			
			var x = s.embeddedShapeOrigin.x + ( reset ? 0 : delta.x );
			var y = s.embeddedShapeOrigin.y + ( reset ? 0 : delta.y );
			
			s.dragTarget.setAttributeNS(null, 'transform', 'translate(' + x + ','
					+ y + ')');	
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
			
		var result = dropCallbacks
			.map(dc => dc(state.map(s => s.dragTarget), evt))
			.reduce((a,b) => (a | b), false);
		
		endMove(!result);
	}
	
	mouseDown = false;
	svg.style.cursor = undefined;
};

/**
 * Allows you to say what should happen as we move the element.
 */
export function registerDragableMoveCallback(p) {
		
	moveCallbacks.push(p);	
}

/**
 * Allows you to say what should happen as we drop the element.
 * One function must return true, or the object goes back to it's 
 * original spot.
 */
export function registerDragableDropCallback(p) {
		
	dropCallbacks.push(p);	
}


window.addEventListener('load', function(event) {

	svg = document.querySelector("div.main svg");
	svg.removeEventListener("mousemove", drag);
	svg.removeEventListener("mouseup", drop);
	svg.addEventListener("mousemove", drag);
	svg.addEventListener("mouseup", drop);

	svg.querySelectorAll("[id][kite9-elem]").forEach(function(v) {
		console.log("Adding e.l. to " + v.getAttribute("id"))
		v.removeEventListener("mousemove", drag);
		v.removeEventListener("mouseup", drop);
		v.removeEventListener("mousedown", grab);
		if (isDragable(v)) {
			v.addEventListener("mousedown", grab);
			v.addEventListener("mousemove", drag);
			v.addEventListener("mouseup", drop);
		}
	})
})

export function getDropTarget(v) {
	if (v.hasAttribute("kite9-elem") && v.hasAttribute("id")) {
		return v;
	} else if (v == svg) {
		return null;
	} else {
		return getDropTarget(v.parentNode);
	}
}

function intersects(a, b) {
    return a.map(e => b.indexOf(e) > -1).reduce((a,b) => a||b, false);
}

export function canDropHere(dragTarget, dropTarget) {
	if (dropTarget == null) {
		return false;
	}
	
	var dragAtt = dragAttribute(dragTarget);
	var dropAtt = dropTarget.getAttribute("drop");
	
	if (dropAtt != null) {
		
		// look for matching ids between the two
		const dragAtts = dragAtt.split(" ");
		const dropAtts = dropAtt.split(" ");
		return intersects(dragAtts, dropAtts);
	} 
	
	return false;
}

export function canDropAllHere(dragTargets, dropTarget) {
	return dragTargets
		.map(dt => canDropHere(dt, dropTarget))
		.reduce((a,b) => a&&b, true);
}


registerDragableDropCallback(function(dragTargets, evt) {

	var targetElement = getDropTarget(evt.target);
	
	if (targetElement == null) {
		endMove(true);
	} else {
		console.log(dragTargets + ' has been dropped on top of ' + targetElement.id);
	}

	return false;
});

registerDragableMoveCallback(function(dragTargets, evt) {
	const dropTarget = getDropTarget(evt.target);
	
	const canDrop = canDropAllHere(dragTargets, dropTarget);
	
	if (canDrop) {
		svg.style.cursor = "grabbing";
	} else {
		svg.style.cursor = "not-allowed";
	}
});
