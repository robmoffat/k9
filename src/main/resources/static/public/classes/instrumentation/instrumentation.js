import { ensureCss } from '/public/bundles/ensure.js';
/**
 * Provides functionality for populating the instrumentation menu.
 */
export class Instrumentation {

	constructor(cb) {
		this.callbacks = cb == undefined ? [] : cb;

		ensureCss('/public/classes/instrumentation/instrumentation.css');
		
		setTimeout(function() {
			this.nav = document.getElementById("--instrumentation");
			if (this.nav == undefined) {
				this.nav = document.createElement("div");
				this.nav.setAttribute("id", "--instrumentation");
				this.nav.setAttribute("class", "instrumentation");
				document.querySelector("body").appendChild(this.nav);
			}

			cb.forEach(c => c(this.nav));
		}, 0)
	}
}

