function getAlignElementsAndDirections(id1, id2) {
	return getExistingConnections(id1, id2)
	 	.filter(e => e.classList.contains("align"))
	 	.map(e => {
	 		const parsed = parseInfo(e);
	 		const d = parsed['direction'];
	 		const link = parsed['link'];
	 		const ids = link.split(" ");
	 		const reversed = ids[0] == id2;	
	 		return { 
	 			element: e,
	 			direction: reversed ? reverseDirection(d) : d
	 		}
	 	});
}

function getExistingConnections(id1, id2) {
	return Array.from(document.querySelectorAll("div.main svg [id][k9-info*='link:']")).filter(e => {
		const parsed = parseInfo(e);
		const link = parsed['link'];
		
		if (link) {
			const ids = link.split(" ");
			
			if (id2) {
				return ((ids[0] == id1) && (ids[1] == id2)) || 
				 ((ids[1] == id1) && (ids[0] == id2));	
			} else {
				return (ids[0] == id1) || (ids[1] == id1);
			}
			
		}
		
		return false;
	});
}

function reverseDirection(d) {
    switch (d) {
    case "LEFT":
            return "RIGHT";
    case "UP":
            return "DOWN";
    case "DOWN":
            return "UP";
    case "RIGHT":
            return "LEFT";
    }

    return d;
};


function getChangeUri() {
	const href = document.URL;
	return href.replace("/content", "/change")
}

function parseInfo(t) {
	if ((t!= null) &&(t.hasAttribute("k9-info"))) {
		const parts = t.getAttribute("k9-info").split(';');
		var out = {}
		parts.forEach(p => {
			p=p.trim();
			const colon = p.indexOf(":");
			if (colon > -1) {
				const name = p.substring(0, colon).trim();
				const value = p.substring(colon+1).trim();
				out[name]=value;
			}
		});
		return out;
	} else {
		return {};
	}
}

function getContainingDiagram(elem) {
	if (elem == null) {
		return null;
	}
	const pcd = getContainingDiagram(elem.parentElement);
	if (pcd) {
		return pcd;
	} else if (elem.hasAttribute("k9-elem")) {
		return elem;
	}
}

function transformToCss(a) {
	var out = '';
	out = a.scaleX != 1 ? out + "scaleX("+a.scaleX+") " : out;
	out = a.scaleY != 1 ? out + "scaleY("+a.scaleY+") " : out;	
	out = a.translateX != 0 ? out + "translateX("+a.translateX+"px) " : out;
	out = a.translateY != 0 ? out + "translateY("+a.translateY+"px) " : out;	
	return out;
}


function number(value) {
	if (value == null) {
		return null;
	} else if (value.endsWith("px")) {
		return Number(value.substr(0, value.length -2));
	} else {
		return Number(value);
	}
}

function parseTransform(a) {
    var b={ 
    	translateX: 0,
    	translateY: 0,
    	scaleX: 1,
    	scaleY: 1
    };
    
    if (a == null) {
    	return b;
    }
    
    for (var i in a = a.match(/(\w+\((\-?\d+\.?\d*e?\-?\d*[(px),\ ]*)+\))+/g))
    {
        var c = a[i].match(/[\w\.\-]+/g);
        var name = c.shift();
        if (c.length > 1) {
        	b[name] = c.map(e => number(e)); 
        } else {
        	b[name] = number(c[0]);
        }
    }
    
    if (b.translate) {
    	b.translateX = b.translate[0];
    	b.translateY = b.translate[1];
    	delete b.translate;
    }
    
    if (b.scale) {
    	if (b.scale.length) {
	    	b.scaleX = b.scale[0];
	    	b.scaleY = b.scale[1];
    	} else {
    		b.scaleX = b.scale;
	    	b.scaleY = b.scale;
    	}
    	delete b.scale;
    }
    
    return b;
}

function suffixIds(e, suffix) {
	if (e.hasAttribute("id")) {
		var id = e.getAttribute("id");
		e.setAttribute("id", id+suffix);
	}
	
	for (var i = 0; i < e.children.length; i++) {
		const c = e.children[i];
		suffixIds(c, suffix);
	}
}

function handleTransformAsStyle(e) {
	if (e.hasAttribute('transform')) {
		const t = parseTransform(e.getAttribute('transform'));
		const css = transformToCss(t);
		e.style.setProperty('transform', css, '');
		e.removeAttribute('transform')
	}
}

var lastId = 1;

function createUniqueId() {
	while (document.getElementById(""+lastId)) {
		lastId++;
	}
	
	return ""+lastId;
}


export { reverseDirection, getChangeUri, parseInfo, createUniqueId, parseTransform, transformToCss, number, getExistingConnections, handleTransformAsStyle, getContainingDiagram, suffixIds, getAlignElementsAndDirections }