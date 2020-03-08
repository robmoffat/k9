import { suffixIds } from '/public/bundles/api.js';

/**
 * Provides functionality for populating/ showing/ hiding a palette.  
 */
export class Palette {

	constructor(id, cb, uriList) {
		this.callbacks = cb == undefined ? [] : cb;
		this.paletteMap = [];
		this.expanded = {};
		
		for(var i = 0; i< uriList.length; i+=2) {
			this.paletteMap.push({
				number: i / 2,
				uri: uriList[i],
				selector: uriList[i+1]
			})
		}
		
		this.id = (id == undefined) ? "--palette" : id;

		var cssId = 'palette';  
		if (!document.getElementById(cssId)) {
		    var head  = document.getElementsByTagName('head')[0];
		    var link  = document.createElement('link');
		    link.id   = cssId;
		    link.rel  = 'stylesheet';
		    link.type = 'text/css';
		    link.href = '/public/classes/palette/palette.css';
		    link.media = 'all';
		    head.appendChild(link);
		}
		
		var darken = document.getElementById("--darken");
		if (!darken) {
			darken = document.createElement("div");
			darken.setAttribute("id", "--darken");
			darken.setAttribute("class", "darken");
			document.querySelector("body").appendChild(darken);
			darken.style.display = 'none';
		}
		
		var palette = document.getElementById(this.id);
		if (!palette) {
			// create palette
			palette = document.createElement("div");
			palette.setAttribute("id", this.id);
			palette.setAttribute("class", "palette");
			document.querySelector("body").appendChild(palette);
			
			// create area for control buttons
			var control = document.createElement("div");
			control.setAttribute("class", "control");
			palette.appendChild(control);
			
			// create concertina area
			var concertina = document.createElement("div");
			concertina.setAttribute("class", "concertina");
			palette.appendChild(concertina);
			
			this.paletteMap.forEach(p => {
				var id = "--palette-"+p.number;
				var item = document.createElement("div");
				item.setAttribute("k9-uses", p.selector);
				item.setAttribute("class", "palette-item");
				item.setAttribute("k9-palette-uri", p.uri);
				item.setAttribute("id", id);
				concertina.appendChild(item);
				
				// create loading indicator
				var loading = document.createElement("img");
				loading.setAttribute("src", "/public/classes/palette/loading.svg");
				item.appendChild(loading);
				
				// populate it
				fetch(p.uri+".svg", {
					credentials: 'include',
					method: 'GET',
					headers: {
						"Content-Type": "application/json; charset=utf-8",
						"Accept": "image/svg+xml, application/json"
					}
				})
				.then(response => {
					if (!response.ok) {
						return response.json().then(j => {
							loading.setAttribute("src", "/public/classes/palette/missing.svg");
							console.log(JSON.stringify(j));
							throw new Error(j.message);
						});
					} 
					
					return response;
				})
				.then(response => response.text())
				.then(text => {
					console.log("Loaded "+this.paletteUri);
					var parser = new DOMParser();
					return parser.parseFromString(text, "image/svg+xml");
				})
				.then(doc => {
					// set new ids
					suffixIds(doc.documentElement, id);
					item.appendChild(doc.documentElement);
					item.removeChild(loading);
					
					this.callbacks.forEach(cb => {
						cb(this, item);
					})
					
					var evt = document.createEvent('Event');
					evt.initEvent('load', false, false);
					window.dispatchEvent(evt);
				})
				.catch(e => {
					alert("Problem loading palette: "+ e);
				})
			})
		}
	}
	
	getId() {
		return this.id;
	}
	
	get(event) {
		return document.getElementById(this.id);
	}
	
	getOpenEvent() {
		return this.openEvent;
	}
	
	getCurrentSelector() {
		return this.currentSelector;
	}
		
	open(event, selector) {
		this.openEvent = event;
		this.currentSelector = selector;
		
		var darken = document.getElementById("--darken");
		var palette = document.getElementById(this.id);
		var concertina = palette.querySelector("div.concertina");
		var control = palette.querySelector("div.control");
		
		// hide palettes without the selector
		palette.querySelectorAll("div.palette-item:not([k9-uses~="+selector+"])")
			.forEach(e => e.style.display = 'none');
		
		// keep track of which palette we are showing
		var expandInfo = this.expanded;
		var toShow = Array.from(palette.querySelectorAll("div.palette-item[k9-uses~="+selector+"]"));
		if (expandInfo[selector] ==undefined) {
			expandInfo[selector] =toShow[0];
		};
			
		// remove old control buttons
		while (control.firstChild) {
		    control.removeChild(control.firstChild);
		}
		
		// add cancel button
		var cancel = document.createElement("img");
		cancel.setAttribute("src", "/public/classes/palette/cancel.svg");
		cancel.addEventListener("click", () => this.destroy());
		control.appendChild(cancel);
		
		var paletteWidth = 100, paletteHeight = 100, width, height;
		var selectedDot;
		
		function expandPanel(e, dot) {
			const expanded = expandInfo[selector];
			expanded.style.maxHeight = "0px";
			e.style.maxHeight = height+"px";
			expandInfo[selector] = e;
			control.querySelectorAll("img").forEach(e => e.classList.remove("selected"));
			if (dot != null) {
				dot.classList.add("selected");
			}
		}

		
		// display new control buttons and size the overall thing
		toShow.forEach((e) => {
			e.style.maxHeight = "0px";
			e.style.visibility = 'show';
			e.style.display = 'block';
			var svg = e.querySelector(":first-child");
			if (!(svg.tagName.toLowerCase() == 'img')) {
				paletteWidth = Math.max(svg.width.baseVal.valueInSpecifiedUnits, paletteWidth);
				paletteHeight = Math.max(svg.height.baseVal.valueInSpecifiedUnits,paletteHeight);
			}
			
			if (toShow.length > 1) {
				var dot = document.createElement("img");
				dot.setAttribute("src", "/public/classes/palette/dot.svg");
				dot.addEventListener("click", () => expandPanel(e, dot));
				if (e == expandInfo[selector]) {
					selectedDot = dot;
				}
				control.appendChild(dot);
			}
		});
		
		// ensure the palette appears in the centre of the screen
		width = Math.min(paletteWidth+30, window.innerWidth-100);
		height = Math.min(paletteHeight+30, window.innerHeight-100);
		
		palette.style.marginTop= (-height/2)+"px";
		palette.style.marginLeft= (-width/2)+"px";
		concertina.style.width = (width)+"px";
		concertina.style.height = (height)+"px";
		palette.style.visibility = 'visible';
		darken.style.display = 'block';

		expandPanel(expandInfo[selector], selectedDot);
		
		return palette;			
	}
	
	destroy() {
		var palette = document.getElementById(this.id);
		var darken = document.getElementById("--darken");
		palette.style.visibility = 'hidden';
		darken.style.display = 'none';
	}
}

export function initPaletteHoverableAllowed(palette) {
	
	return function(v) {
		const currentSelector = palette.getCurrentSelector();
		return v.hasAttribute("k9-palette") ? v.getAttribute("k9-palette").includes(currentSelector) : false;
	}
	
}