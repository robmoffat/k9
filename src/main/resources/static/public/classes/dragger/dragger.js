import { getSVGCoords, getMainSvg } from '/public/bundles/screen.js';
import { handleTransformAsStyle, getKite9Target, isTerminator, isLink, isConnected } from '/public/bundles/api.js';


/**
 * Manages state while dragging, as well as maintaining a --moveLayer <g> which holds all the elements
 * being dragged.
 * 
 * - moveCallbacks:  function called to notify of a move
 * - dropcallbacks:  function called to notify of a drop
 * - dragLocators: given a mouse event, figure out what is being dragged (or []).
 * - dropLocators: given a mouse event, return the drop target (or null).  Earlier calls take priority.
 */
export class Dragger {
	
	constructor(moveCallbacks, dropCallbacks, dragLocators, dropLocators) {
		this.svg = getMainSvg();

		// keeps track of the current drag
		this.dragOrigin = null;
		this.state = null;
		this.shapeLayer = null;
		this.mouseDown = false;
		this.delta = null;
		this.dropTargets = [];

		// plugged-in functionality.
		this.moveCallbacks = moveCallbacks;
		this.dropCallbacks = dropCallbacks;
		this.dropLocators = dropLocators;
		this.dragLocators = dragLocators;
	}	

	getTranslate(dt) {
		if (dt == this.svg) {
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

	getDifferentialTranslate(dt) {
		var transMatrix = this.getTranslate(dt);
		var parentMatrix = this.getTranslate(dt.parentElement);
		
		var out = {
			x: transMatrix.x - parentMatrix.x, 
			y: transMatrix.y - parentMatrix.y
		}
		
		return out;
	}

	
	beginMove(evt) {
		var out = []
		
		const elements = this.dragLocators.flatMap(dl => dl(evt));
		const uniqueElements = [...new Set(elements)];
			
		uniqueElements.forEach(e => {
			handleTransformAsStyle(e);
			
			if (this.shapeLayer == null) {
				this.shapeLayer = document.createElementNS("http://www.w3.org/2000/svg", "g");
				this.shapeLayer.setAttributeNS(null, 'pointer-events', 'none');
				this.shapeLayer.setAttribute("id", "--moveLayer");
				this.svg.appendChild(this.shapeLayer);
			}
			
			var shapeOrigin = this.getTranslate(e);
			var embeddedShapeOrigin = this.getDifferentialTranslate(e);
			
			out.push({
				dragTarget: e,
				dragParent: e.parentElement,
				dragBefore: e.nextSibling,
				shapeOrigin: shapeOrigin,
				embeddedShapeOrigin: embeddedShapeOrigin
			})
			
			this.shapeLayer.appendChild(e);
			e.setAttributeNS(null, 'pointer-events', 'none');
			e.style.setProperty('transform', 'translateX(' + shapeOrigin.x + 'px) translateY('
					+ shapeOrigin.y + 'px)');
		});
		
		if (out.length > 0) {
			this.dragOrigin = getSVGCoords(evt);
			
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
			
			this.state = out;
		}
		
	}

	grab(evt) {
		this.mouseDown = true;
	}
	 
	drag(evt) {
		if (!this.mouseDown) {
			return;
		}
		
		if (!this.state) {
			this.beginMove(evt);
		}
		
		if (this.state) {
			// calculate move in true coords
			var trueCoords = getSVGCoords(evt);
			this.delta = {
					x:  trueCoords.x - this.dragOrigin.x, 
					y:  trueCoords.y - this.dragOrigin.y
			};

			// apply a new tranform translation to the dragged element, to display
			//    it in its new location
			this.shapeLayer.style.setProperty('transform', 'translateX(' + this.delta.x + 'px) translateY('
					+ this.delta.y + 'px)');
			
			const dragTargets = this.state.map(s => s.dragTarget)
			
			var newDropTargets = [];
			this.dropLocators.forEach(dl => {
				newDropTargets = newDropTargets.concat(dl(dragTargets, evt));
			})
			
			this.updateDropTargets(newDropTargets);

			this.moveCallbacks.forEach(mc => mc(dragTargets, evt, this.dropTargets));
			
		} 
	}
	
	updateDropTargets(newDropTargets) {
		
		const leaving = this.dropTargets
			.filter(t => newDropTargets.indexOf(t) == -1)
			.forEach(t => t.classList.remove("dropping"));
		
		const joining = newDropTargets
			.filter(t => this.dropTargets.indexOf(t) == -1)
			.forEach(t => t.classList.add("dropping"));
		
		if (newDropTargets.length > 0) {
			this.svg.style.cursor = "grabbing";
		} else {
			this.svg.style.cursor = "not-allowed";
		}
		
		this.dropTargets = newDropTargets;
	}

	endMove(reset) {
		if (this.state) {
			while (this.state.length > 0) {
				for (var i = 0; i<this.state.length; i++) {
					const s = this.state[i];
					console.log("moving: "+s.dragTarget.getAttribute("id"))
					
					if ((s.dragParent.contains(s.dragBefore)) || (s.dragBefore==null)) {
						s.dragParent.insertBefore(s.dragTarget, s.dragBefore);
						
						var x = s.embeddedShapeOrigin.x + ( reset ? 0 : this.delta.x );
						var y = s.embeddedShapeOrigin.y + ( reset ? 0 : this.delta.y );
						
						s.dragTarget.style.setProperty('transform', 'translateX(' + x + 'px) translateY('
								+ y + 'px)');
						s.dragTarget.setAttributeNS(null, 'pointer-events', 'all');
						this.state.splice(i, 1);
					} 
				}
				console.log("going again")
			}
			
			this.state = null;
			this.svg.removeChild(this.shapeLayer);
			this.svg.style.cursor = null;
			this.shapeLayer = null;
			this.dragOrigin = null;
			this.dropTargets.forEach(t => t.classList.remove("dropping"));
		}
	}
	

	drop(evt) {
		// if we aren't currently dragging an element, don't do anything
		if (this.state) {
			const dragTargets = this.state.map(s => s.dragTarget);
			
			if (this.dropTargets.length > 0) {
				var result = this.dropCallbacks
					.map(dc => dc(dragTargets, evt, this.dropTargets))
					.reduce((a,b) => (a | b), false);
				this.endMove(false);
				this.dropTargets = [];
			} else {
				this.endMove(true);			
			}
		}
		
		this.mouseDown = false;
		this.svg.style.cursor = undefined;
	}

	
}




