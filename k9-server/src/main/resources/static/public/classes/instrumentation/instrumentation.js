/**
 * Provides functionality for populating the instrumentation menu.
 */
export class Instrumentation {

	constructor(cb) {
		this.callbacks = cb == undefined ? [] : cb;

		var cssId = 'instrumentation-css';  
		if (!document.getElementById(cssId)) {
			var head  = document.getElementsByTagName('head')[0];
			var link  = document.createElement('link');
			link.id   = cssId;
			link.rel  = 'stylesheet';
			link.type = 'text/css';
			link.href = '/public/classes/instrumentation/instrumentation.css';
			link.media = 'all';
			head.appendChild(link);
		}
		
		this.nav = document.getElementById("--instrumentation");
		if (this.nav == undefined) {
			this.nav = document.createElement("div");
			this.nav.setAttribute("id", "--instrumentation");
			this.nav.setAttribute("class", "instrumentation");
			document.querySelector("body").appendChild(this.nav);
		}

		this.callbacks.forEach(cb => cb(this.nav));
	}

	/**
	 * Callback for updating the instrumentation menu when metadata changes.
	 */
	metadataCallback() {

		
	}

}




