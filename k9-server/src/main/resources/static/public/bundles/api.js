

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

export { getChangeUri, parseInfo, createUniqueId, parseTransform, transformToCss, number, handleTransformAsStyle, getContainingDiagram, suffixIds }