/**
 * This handles the process of loading new SVG and animating between the old one and the new one.
 */

import { anime } from '/public/external/anime-3.0.1.js'
import { parseTransform, transformToCss, number, handleTransformAsStyle } from '/public/bundles/api.js';
import { getMainSvg } from '/public/bundles/screen.js';

const numeric = ['width', 'height', 'x', 'y', 'rx', 'ry'];

function reconcileTransform(fromValue, toValue, start, end) {
	var fromT = parseTransform(fromValue);
	var toT = parseTransform(toValue);
	for (var i in fromT) start[i] = fromT[i];
	for (var i in toT) end[i] = toT[i];
}

function reconcileStyles(fromElement, toElement, tl, start, end) {
	var toStyles = Array.from(toElement.style);
	var fromStyles = Array.from(fromElement.style);
	var toRemove = fromStyles.filter(a => -1 == toStyles.indexOf(a));

	toRemove.forEach(a => fromElement.style[a] = undefined);

	toStyles.forEach(a => {

		var fromValue = fromElement.style[a];
		var toValue = toElement.style[a];

		if (fromValue !== toValue) {
			if (numeric.indexOf(a) != -1) {
				end[a] = number(toValue);
				start[a] = number(fromValue);
			} else if (a == 'transform') {
				reconcileTransform(fromValue, toValue, start, end);
			} else {
				// just change text
				fromElement.style[a] = toValue;
			}
		}
	})


}

/**
 * 
 * This file contains basic functionality to allow SVG diagrams to have behaviours and
 * animate between different versions.
 */
function reconcileAttributes(fromElement, toElement, tl) {
	var toAtts = Array.from(toElement.attributes).map(a => a.name);
	var fromAtts = Array.from(fromElement.attributes).map(a => a.name);
	var toRemove = fromAtts.filter(a => -1 == toAtts.indexOf(a));

	var start = {
		delay: 0,
		duration: 0
	};
	var end = {
		duration: 1000
	};

	toRemove.forEach(a => fromElement.removeAttribute(a));
	toAtts.forEach(a => {
		var fromValue = fromElement.getAttribute(a);
		var toValue = toElement.getAttribute(a);

		if ((a.startsWith("xlink:")) && (fromValue !== toValue)) {
			fromElement.setAttributeNS("http://www.w3.org/1999/xlink", a.substring(a.indexOf(':')+1), toValue);
		} else if (fromValue == null) {
			fromElement.setAttribute(a, toValue);
		} else if (fromValue !== toValue) {
			if (numeric.indexOf(a) != -1) {
				end[a] = number(toValue);
				start[a] = number(fromValue);
			} else if (a == 'style') {
				if (fromElement.tagName != 'svg') {
					reconcileStyles(fromElement, toElement, tl, start, end);
				}
			} else if (a == 'd') {
				end[a] = toValue;
				start[a] = fromValue;
			} else {
				fromElement.setAttribute(a, toValue);
			}
		}

	});

	if (fromElement.style){
		tl.add({
			targets: fromElement,
			keyframes: [start, end],
		}, 0);
	}

}

function reconcileText(fromElement, toElement) {
	if (fromElement.tagName == 'script') {
		// we don't reconcile scripts as this means doing a lot of 
		// parsing / reloading js
		return;
	}
	
	if (fromElement.textContent != toElement.textContent) {
		fromElement.textContent = toElement.textContent;
	}
}

function getLocalTranslate(e) {
	const t = parseTransform(e.style.transform);
	return {
		x: t.translateX,
		y: t.translateY
	}
}

function getTotalTranslate(e) {
	if ((e == null) || (e.style == null)) {
		return { x: 0, y: 0 };
	}

	const t = getLocalTranslate(e);
	const pt = getTotalTranslate(e.parentElement);
	const out = {
		x: t.x + pt.x,
		y: t.y + pt.y
	}
	return out;
}

function removeNonElementContent(el) {
	var child = el.firstChild;
	var nextChild;

	while (child) {
	    nextChild = child.nextSibling;
	    if (child.nodeType == 3) {
	        el.removeChild(child);
	    }
	    child = nextChild;
	}
}


function reconcileElement(inFrom, inTo, toDelete, tl) {
	//console.log("Reconciling " + inFrom.tagName + ' with ' + inTo.tagName + " " + inFrom.getAttribute("id") + " " + inTo.getAttribute("id"))
	
	handleTransformAsStyle(inFrom);
	handleTransformAsStyle(inTo);
	reconcileAttributes(inFrom, inTo, tl);

	if (inTo.childElementCount == 0) {
		reconcileText(inFrom, inTo);
	} else {
		var ti = 0;
		var fi = 0;
		
		removeNonElementContent(inFrom);
		removeNonElementContent(inTo);
		
		while (ti < inTo.childElementCount) {
			const toElement = inTo.children.item(ti);
			const fromElement = (fi < inFrom.childElementCount) ? inFrom.children.item(fi) : null;

			if (toElement.hasAttribute("id")) {
				// ideally, we need to merge
				const toId = toElement.getAttribute("id");
				const fromId = fromElement == null ? null : fromElement.getAttribute("id");
				const missingFrom = inFrom.ownerDocument.getElementById(toId);

				if (toId == fromId) {
					// to/from correspond
					reconcileElement(fromElement, toElement, toDelete, tl);
					fi++;
					ti++;
				} else if (missingFrom != null) {
					// from element has moved
					const parentFromTranslate = getTotalTranslate(missingFrom.parentElement);
					const parentToTranslate = getTotalTranslate(inTo);
					const localFromTranslate = getLocalTranslate(missingFrom);
					const newTranslate = {
						x: localFromTranslate.x + parentFromTranslate.x - parentToTranslate.x,
						y: localFromTranslate.y + parentFromTranslate.y - parentToTranslate.y
					};
					//console.log("from moving" + newTranslate);
					inFrom.insertBefore(missingFrom, fromElement);
					missingFrom.setAttribute("transform", "translate(" + newTranslate.x + "," + newTranslate.y + ")");
					reconcileElement(missingFrom, toElement, toDelete, tl);
					ti++;
					fi++;
				} else {
					// to element is new	
					//console.log("creating new element " + toElement.tagName)
					const newFromElement = document.createElementNS(toElement.namespaceURI, toElement.tagName);
					inFrom.insertBefore(newFromElement, fromElement);
					reconcileElement(newFromElement, toElement, toDelete, tl);
					fi++;
					ti++;
				}
			} else {
				// here, we have non-id elements, so we really just need 
				// to be sure there are the right number

				if ((fromElement == null) || (fromElement.tagName != toElement.tagName) || (fromElement.hasAttribute("id"))) {
					// treat as an insertion.
					//console.log("creating new element " + toElement.tagName)
					const newFromElement = document.createElementNS(toElement.namespaceURI, toElement.tagName);
					inFrom.insertBefore(newFromElement, fromElement);
					reconcileElement(newFromElement, toElement, toDelete, tl);
					fi++;
					ti++;
				} else {
					// assume it's the same element (tags match, after all)
					reconcileElement(fromElement, toElement, toDelete, tl);
					fi++;
					ti++;
				}
			}

		}

		while (fi < inFrom.childElementCount) {
			const fromElement = inFrom.children.item(fi);
			const totalTranslate = getTotalTranslate(fromElement);
			//console.log("removing " + fromElement);
			if (fromElement != toDelete) {
				toDelete.appendChild(fromElement);
				fromElement.setAttribute("transform", "translate(" + totalTranslate.x + "," + totalTranslate.y + "); ");
			} else {
				toDelete.parentElement.removeChild(toDelete);
			}
		}

	}
}

export class Transition {
	
	constructor(uri, loadCallbacks, animationCallbacks) {
		this.loadCallbacks = loadCallbacks == undefined ? [] : loadCallbacks;
		this.animationCallbacks = animationCallbacks == undefined ? [] : animationCallbacks;
		this.commandList = [];
		this.uri = uri;
	}
	
	transition(documentElement) {

		// this will store everything we'll eventually remove
		var svg = document.querySelector("body svg");
		var toDelete = svg.ownerDocument.createElementNS(svg.namespaceURI, "g");
		svg.appendChild(toDelete);
		toDelete.setAttribute('id', '--deleteGroup');

		// create the animation timeline
		var tl = anime.timeline({
			easing: 'easeOutExpo',
			duration: 1000,
			autoplay: false,
			complete: (anim) => this.animationCallbacks.forEach(cb => cb(anim))

		});

		reconcileElement(svg, documentElement, toDelete, tl);
		
		tl.play();

		// force the load event to occur again
		var evt = document.createEvent('Event');
		evt.initEvent('load', false, false);
		window.dispatchEvent(evt);
	}

	handleErrors(response) {
		if (!response.ok) {
			return response.json().then(j => {
				//console.log(JSON.stringify(j));
				throw new Error(j.message);
			});
		}
		return response;
	}
	
	handleRedirect(response) {
		return response;
	}

	mainHandler(p) {
		return p
			.then(this.handleErrors)
			.then(this.handleRedirect)
			.then(response => {
				this.loadCallbacks.forEach(cb => cb(response));
				return response;
			})
			.then(response => response.text())
			.then(text => {
				var parser = new DOMParser();
				return parser.parseFromString(text, "image/svg+xml");
			})
			.then(doc => this.transition(doc.documentElement))
	}

	get(uri) {
		return this.mainHandler(fetch(uri, {
			method: 'GET',
			headers: this.getHeaders()
		}));
	}
	
	getHeaders() {
		var out = {
			"Content-Type": "application/json",
			"Accept": "image/svg+xml, application/json"
		};
		
		return out;
	}
	
	setCredentials(jwt) {
		this.jwt = jwt;
	}

	postCommands(commands) {
		// before we post commands, add adl payload if there is one.
		const firstCommand = commands[0];
		const base64Text = getMainSvg().getElementById("adl:markup");
		firstCommand.base64adl = base64Text.textContent;
		
		return this.mainHandler(fetch(this.uri(), {
			method: 'POST',
			body: JSON.stringify(commands),
			headers: this.getHeaders()
		})).catch(e => {
			this.get(this.uri());
			alert(e);
		});
	}
		
	/**
	 * This is for where we want to queue up a bunch of commands and post them all together
	 */
	push(command) {
		this.commandList.push(command);
	}
	
	postCommandList() {
		if (this.commandList.length > 0) {
			this.postCommands(this.commandList);
			this.commandList = [];
		}
	}
	
}