/**
 * Drag and drop is controlled by the 'drag' attribute:
 *  - 'yes': the element can be dragged.   
 *  - 'target':  you can drop here.
 *  - 'from': it's drag-able, but it's the from-end of a link
 *  - 'to: it's drag-able, but it's the to-end of a link.
 *  - 'link': you can link to it.
 */

var dragTarget = null;
var dragParent = null;
var dragBefore = null;
var svg = null;
var shapeOrigin = null;
var embeddedShapeOrigin = null;
var dragOrigin = null;

var moveCallbacks= [];
var dropCallbacks = [];

function getTrueCoords(evt) {
	// find the current zoom level and pan setting, and adjust the reported
	//    mouse position accordingly
	var newScale = svg.currentScale;
	var translation = svg.currentTranslate;
	return {
		x:  (evt.clientX - translation.x) / newScale,
		y:  (evt.clientY - translation.y) / newScale
	}
}

function dragAttribute(v) {
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

function grab(evt) {
	dragTarget = getDragTarget(evt.target);
	dragParent = dragTarget.parentElement;
	dragBefore = dragTarget.nextSibling;

	if (svg == dragTarget) {
		// you cannot drag the background itself, so ignore any attempts to mouse down on it
		dragTarget = null;
		return;
	}


	// turn off all pointer events to the dragged element, this does 2 things:
	//    1) allows us to drag text elements without selecting the text
	//    2) allows us to find out where the dragged element is dropped (see Drop)
	dragTarget.setAttributeNS(null, 'pointer-events', 'none');

	// we need to find the current position and translation of the grabbed element,
	//    so that we only apply the differential between the current location
	//    and the new location
	embeddedShapeOrigin = getDifferentialTranslate(dragTarget);
	shapeOrigin = getTranslate(dragTarget);
	dragOrigin = getTrueCoords(evt);
	
	// pop the element out
	svg.appendChild(dragTarget)
	dragTarget.setAttributeNS(null, 'transform', 'translate(' + shapeOrigin.x + ','
			+ shapeOrigin.y + ')');
}

function drag(evt) {

	// if we don't currently have an element in tow, don't do anything
	if (dragTarget) {
		// calculate move in true coords
		var trueCoords = getTrueCoords(evt);
		var changeTrueCoords = {
				x:  trueCoords.x - dragOrigin.x, 
				y:  trueCoords.y - dragOrigin.y
		};
		
		// calculate move in shape coords
		var changeShapeCoords = {
				x: changeTrueCoords.x * svg.currentScale,
				y: changeTrueCoords.y * svg.currentScale
		}
		
		var newX = changeShapeCoords.x + shapeOrigin.x;
		var newY = changeShapeCoords.y + shapeOrigin.y;

		// apply a new tranform translation to the dragged element, to display
		//    it in its new location
		dragTarget.setAttributeNS(null, 'transform', 'translate(' + newX + ','
				+ newY + ')');
		
		moveCallbacks.forEach(mc => mc(dragTarget, evt));
	}

}

export function returnDragTarget() {
	dragParent.insertBefore(dragTarget, dragBefore);
	dragTarget.setAttributeNS(null, 'transform', 'translate(' + embeddedShapeOrigin.x + ','
			+ embeddedShapeOrigin.y + ')');
	
}

function drop(evt) {
	// if we aren't currently dragging an element, don't do anything
	if (dragTarget) {
			
		var result = dropCallbacks
			.map(dc => dc(dragTarget, evt))
			.reduce((a,b) => (a | b), false);
		
		if (!result) {
			returnDragTarget();
		}

		// turn the pointer-events back on, so we can grab this item later
		dragTarget.setAttributeNS(null, 'pointer-events', 'all');
		dragTarget = null;
	}
};

/**
 * Allows you to say what should happen as we move the element.
 */
export function registerMoveCallback(p) {
		
	moveCallbacks.push(p);	
}

/**
 * Allows you to say what should happen as we drop the element.
 * One function must return true, or the object goes back to it's 
 * original spot.
 */
export function registerDropCallback(p) {
		
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
		v.removeEventListener("mousedown", grab);
		v.removeEventListener("mousemove", drag);
		v.removeEventListener("mouseup", drop);
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


registerDropCallback(function(dragTarget, evt) {

	var targetElement = getDropTarget(evt.target);
	
	if (targetElement == null) {
		returnDragTarget();
	} else {
		alert(dragTarget.id + ' has been dropped on top of ' + targetElement.id);
	}

	return false;
})