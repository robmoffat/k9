import { suffixIds } from '/public/bundles/api.js';

/**
 * Provides functionality for populating/ showing/ hiding a palette.  
 */
export class Palette {

	constructor(id, cb, uri) {
		this.callbacks = cb == undefined ? [] : cb;
		this.paletteUri = uri;
		
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
			
			// add cancel button
			var cancel = document.createElement("img");
			cancel.setAttribute("src", "/public/classes/palette/cancel.svg");
			cancel.setAttribute("class", "cancel");
			cancel.addEventListener("click", () => this.destroy());
			palette.appendChild(cancel);
			
			// create scrollable area
			var scrollable = document.createElement("div");
			scrollable.setAttribute("class", "scrollable");
			palette.appendChild(scrollable);
			
			// populate it
			fetch(this.paletteUri+".svg", {
				credentials: 'include',
				method: 'GET',
				headers: {
					"Content-Type": "application/json; charset=utf-8",
					"Accept": "image/svg+xml, application/json"
				}
			})
			.then(this.handleErrors)
			.then(response => response.text())
			.then(text => {
				console.log("Loaded "+this.paletteUri);
				var parser = new DOMParser();
				return parser.parseFromString(text, "image/svg+xml");
			})
			.then(doc => {
				// set new ids
				suffixIds(doc.documentElement, this.id);
				scrollable.appendChild(doc.documentElement);
				this.callbacks.forEach(cb => cb(this, event));
				
				// force the load event to occur again
				var evt = document.createEvent('Event');
				evt.initEvent('load', false, false);
				window.dispatchEvent(evt);
			});
		}
	}
	

	
	handleErrors(response) {
		if (!response.ok) {
			return response.json().then(j => {
				console.log(JSON.stringify(j));
				throw new Error(j.message);
			});
		}
		return response;
	}
	
	getUri() {
		return this.paletteUri;
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
		
	open(event) {
		var darken = document.getElementById("--darken");
		var palette = document.getElementById(this.id);
		var scrollable = palette.querySelector(".scrollable");
		var svg = scrollable.querySelector("svg");
		
		// ensure the palette appears in the centre of the screen
		var paletteWidth = svg.width.baseVal.valueInSpecifiedUnits;
		var paletteHeight = svg.height.baseVal.valueInSpecifiedUnits;
		var width = Math.min(paletteWidth+30, window.innerWidth-100);
		var height = Math.min(paletteHeight+30, window.innerHeight-100);
		
		palette.style.marginTop= (-height/2)+"px";
		palette.style.marginLeft= (-width/2)+"px";
		scrollable.style.width = (width)+"px";
		scrollable.style.height = (height)+"px";
		palette.style.visibility = 'visible';
		darken.style.display = 'block';
		
		this.openEvent = event;
		
		return palette;	
	}
	
	destroy() {
		var palette = document.getElementById(this.id);
		var darken = document.getElementById("--darken");
		palette.style.visibility = 'hidden';
		darken.style.display = 'none';
	}
}