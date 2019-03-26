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
		var palette = document.querySelector("#--palette");
		if (!palette) {
			palette = document.createElement("div");
			palette.setAttribute("id", "--palette");
			palette.setAttribute("class", "palette");
			document.querySelector("body").appendChild(palette);

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
				palette.appendChild(doc.documentElement);
				this.callbacks.forEach(cb => cb(this, event));
				
				// force the load event to occur again
				var evt = document.createEvent('Event');
				evt.initEvent('load', false, false);
				window.dispatchEvent(evt);
			});
		}
			
		palette.style.display = 'block';
		return palette;	
	}
	
	destroy() {
		var palette = document.querySelector("#--palette");
		palette.style.display = 'none';
	}
}