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
    fromElement.textContent = toElement.textContent;
}

function reconcileElement(fromElement, toElement) {
    reconcileAttributes(fromElement, toElement);
    var fi = 0;
    var ti = 0;
    while (fromElement.childElementCount > toElement.childElementCount) {
        fromElement.children[fromElement.childElementCount - 1].remove();
    }
    if (toElement.childElementCount == 0) {
        reconcileText(fromElement, toElement);
    }
    while (ti < toElement.childElementCount) {
        if (fi < fromElement.childElementCount) {
            // element on both sides.
            var newFromElement = fromElement.children[fi];
            var newToElement = toElement.children[ti];
            if (newFromElement.tagName == newToElement.tagName) {
                reconcileElement(newFromElement, newToElement);
                fi++;
                ti++;
            }
            else {
                console.log("Replacing: " + newFromElement.tagName + " " + newToElement);
                fromElement.insertBefore(newToElement, newFromElement);
                newFromElement.remove();
                fi++;
            }
        }
        else {
            const newToElement = toElement.children[ti];
            const newFromElement = document.createElementNS(newToElement.namespaceURI, newToElement.tagName);
            fromElement.appendChild(newFromElement);
            reconcileElement(newFromElement, newToElement);
        }
    }
}
function transition(documentElement) {
    reconcileElement(document.querySelector("body svg"), documentElement);
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
