/**
 * Provides functionality for populating/ showing/ hiding the palette.  
 */
export class Palette {

	constructor(cb, uri) {
		this.callbacks = cb == undefined ? [] : cb;
		this.paletteUri = uri;

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
		
		var darken = document.querySelector("#--darken");
		if (!darken) {
			darken = document.createElement("div");
			darken.setAttribute("id", "--darken");
			darken.setAttribute("class", "darken");
			document.querySelector("body").appendChild(darken);
			darken.style.display = 'none';
		}
		
		var palette = document.querySelector("#--palette");
		if (!palette) {
			
			// create palette
			palette = document.createElement("div");
			palette.setAttribute("id", "--palette");
			palette.setAttribute("class", "palette");
			document.querySelector("body").appendChild(palette);
			
			// add cancel button
			var cancel = document.createElement("img");
			cancel.setAttribute("src", "/public/classes/palette/cancel.svg");
			cancel.setAttribute("class", "cancel");
			cancel.addEventListener("click", () => this.destroy());
			palette.appendChild(cancel);
			
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
				var parser = new DOMParser();
				return parser.parseFromString(text, "image/svg+xml");
			})
			.then(doc => {
				
				// create scrollable area
				var scrollable = document.createElement("div");
				scrollable.setAttribute("class", "scrollable");
				palette.appendChild(scrollable);
				scrollable.appendChild(doc.documentElement);
				this.callbacks.forEach(cb => cb(this, event));
				
				// ensure the palette appears in the centre of the screen
				var {width, height} = scrollable.getBoundingClientRect();
				this.paletteWidth = width;
				this.paletteHeight = height;
				
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
	
	get(event) {
		return document.querySelector("#--palette");
	}
		
	open(event) {
		var darken = document.querySelector("#--darken");
		var palette = document.querySelector("#--palette");
		
		var width = Math.min(this.paletteWidth, window.innerWidth-100);
		var height = Math.min(this.paletteHeight, window.innerHeight-100);
		
		palette.style.marginTop= (-height/2)+"px";
		palette.style.marginLeft= (-width/2)+"px";
		palette.style.width = (width)+"px";
		palette.style.height = (height)+"px";
		palette.style.visibility = 'visible';
		darken.style.display = 'block';
		
		return palette;	
	}
	
	destroy() {
		var palette = document.querySelector("#--palette");
		var darken = document.querySelector("#--darken");
		palette.style.visibility = 'hidden';
		darken.style.display = 'none';
	}
}