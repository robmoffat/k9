/**
 * Drag and drop is controlled by the 'drag' attribute:
 *  - 'none': the element can't be dragged.   
 *  - 'nodrop':  you can't drop here.
 */

var dragTarget = null;
var svg = null;
var shapeOrigin = null;
var dragOrigin = null;

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
	return (!dragAttribute(v).includes("none")) && (v.hasAttribute("id") && v.hasAttribute("kite9-elem"));
}

function isDropable(v) {
	return (!dragAttribute(v).includes("nodrop")) && (v.hasAttribute("id") && v.hasAttribute("kite9-elem"));
}

function getDragTarget(v) {
	if (isDragable(v)) {
		return v;
	} else {
		return getDragTarget(v.parentNode);
	}
}

function getDropTarget(v) {
	if (isDropable(v)) {
		return v;
	} else if (v == svg) {
		return null;
	} else {
		return getDropTarget(v.parentNode);
	}
}

function moveToFront(dr) {
	if (dr == null) {
		return;
	}
	
	if (dr.hasAttribute("kite9-elem")) {
		dr.parentElement.appendChild(dr);
	}
	
	moveToFront(dr.parentElement);
	
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

	if (svg == dragTarget) {
		// you cannot drag the background itself, so ignore any attempts to mouse down on it
		dragTarget = null;
		return;
	}

	// move this element to the "top" of the display, so it is (almost)
	//    always over other elements (exception: in this case, elements that are
	//    "in the folder" (children of the folder group) with only maintain
	//    hierarchy within that group
	moveToFront(dragTarget)

	// turn off all pointer events to the dragged element, this does 2 things:
	//    1) allows us to drag text elements without selecting the text
	//    2) allows us to find out where the dragged element is dropped (see Drop)
	dragTarget.setAttributeNS(null, 'pointer-events', 'none');

	// we need to find the current position and translation of the grabbed element,
	//    so that we only apply the differential between the current location
	//    and the new location
	shapeOrigin = getDifferentialTranslate(dragTarget);
	dragOrigin = getTrueCoords(evt);
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
	}

}

function drop(evt) {
	// if we aren't currently dragging an element, don't do anything
	if (dragTarget) {
		var targetElement = getDropTarget(evt.target);

		// turn the pointer-events back on, so we can grab this item later
		dragTarget.setAttributeNS(null, 'pointer-events', 'all');
		
		if (targetElement == null) {
			// can't drop here.
			dragTarget.setAttributeNS(null, 'transform', 'translate(' + shapeOrigin.x + ','
					+ shapeOrigin.y + ')');
			
		} else {
			alert(dragTarget.id + ' has been dropped on top of ' + targetElement.id);
		}

		dragTarget = null;
	}
};

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