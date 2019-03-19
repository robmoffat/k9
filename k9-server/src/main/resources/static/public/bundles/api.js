

function getChangeUri() {
	const href = document.URL;
	return href.replace("/content", "/change")
}

function parseDebug(t) {
	if ((t!= null) &&(t.hasAttribute("debug"))) {
		const parts = t.getAttribute("debug").split(';');
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


var lastId = 1;

function createUniqueId() {
	while (document.getElementById(""+lastId)) {
		lastId++;
	}
	
	return ""+lastId;
}

export { getChangeUri, parseDebug, createUniqueId }