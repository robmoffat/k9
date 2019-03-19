/**
 * This file contains basic functionality to allow SVG diagrams to have behaviours and
 * animate between different versions.
 */
function reconcileAttributes(fromElement, toElement) {
    var toAtts = Array.from(toElement.attributes).map(a => a.name);
    var fromAtts = Array.from(fromElement.attributes).map(a => a.name);
    var toRemove = fromAtts.filter(a => -1 == toAtts.indexOf(a));
    toRemove.forEach(a => fromElement.removeAttribute(a));
    toAtts.forEach(a => {
        fromElement.setAttribute(a, toElement.getAttribute(a));
    });
}

function reconcileText(fromElement, toElement) {
	if (fromElement.textContent != toElement.textContent) {
		fromElement.textContent = toElement.textContent;
	}
}

function reconcileElement(inFrom, inTo, toDelete) {
	console.log("Reconciling "+inFrom.tagName+' with '+inTo.tagName+" "+inFrom.getAttribute("id")+" "+inTo.getAttribute("id"))
    reconcileAttributes(inFrom, inTo);
    
    if (inTo.childElementCount == 0) {
        reconcileText(inFrom, inTo);
    } else {
        var fi = 0;
        var ti = 0;
    	
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
        			reconcileElement(fromElement, toElement, toDelete);
        			ti ++;
        			fi ++;
        		} else if (missingFrom != null) {
        			// from element has moved
        			console.log("from moving")
    				inFrom.insertBefore(missingFrom, fromElement);
    				reconcileElement(missingFrom, toElement, toDelete);
    				ti ++;
    				fi ++;
        		} else {
        			// to element is new	
        			console.log("creating new element "+toElement.tagName)
        			const newFromElement = document.createElementNS(toElement.namespaceURI, toElement.tagName);
                    inFrom.insertBefore(newFromElement, fromElement);
                    reconcileElement(newFromElement, toElement, toDelete);
                    ti ++;
        		} 
        	} else {
    			// here, we have non-id elements, so we really just need 
    			// to be sure there are the right number
        			
        		if ((fromElement == null) || (fromElement.tagName != toElement.tagName)) {
        			// treat as an insertion.
        			console.log("creating new element "+toElement.tagName)
        			const newFromElement = document.createElementNS(toElement.namespaceURI, toElement.tagName);
                    inFrom.insertBefore(newFromElement, fromElement);
                    reconcileElement(newFromElement, toElement, toDelete);
                    ti ++;
        		} else {
        			// assume it's the same element (tags match, after all)
        			reconcileElement(fromElement, toElement, toDelete);
        			ti ++;
        			fi ++;
        		}
        	}
        }
        	
    	while (fi < inFrom.childElementCount) {
    		const fromElement = inFrom.children.item(fi);
    		console.log("removing "+fromElement);
    		if (fromElement != toDelete) {
	    		toDelete.appendChild(fromElement);
    		} else {
    			toDelete.parentElement.removeChild(toDelete);
    		}
    	}
        
    }
}
function transition(documentElement) {
	
	// this will store everything we'll eventually remove
	var svg = document.querySelector("body svg");
	var toDelete = svg.ownerDocument.createElementNS(svg.namespaceURI, "g");
	svg.appendChild(toDelete);
	toDelete.setAttribute('id', '--deleteGroup');
    reconcileElement(svg, documentElement, toDelete);
    
    // force the load event to occur again
    var evt = document.createEvent('Event');
    evt.initEvent('load', false, false);
    window.dispatchEvent(evt);
}

function handleErrors(response) {
    if (!response.ok) {
        return response.json().then(j => { 
        	console.log(JSON.stringify(j));
        	throw new Error(j.message); 
        });
    }
    return response;
}

function postCommands(commands, uri) {
	
	fetch(uri, {
		credentials: 'include', 
		method: 'POST', 
		body: JSON.stringify(commands),
		headers: {
			"Content-Type": "application/json; charset=utf-8",
			"Accept": "image/svg+xml, application/json"
		}})
	.then(handleErrors)
	.then(response => response.text())
	.then(text => {
		var parser = new DOMParser();
		return parser.parseFromString(text, "image/svg+xml");
	})
	.then(doc => transition(doc.documentElement))
	.catch(e => alert(e));
	
}
	
	
export { transition, postCommands };
