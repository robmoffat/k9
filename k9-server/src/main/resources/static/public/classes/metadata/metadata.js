/**
 * Handles monitoring of document metadata, and callbacks for when it changes.
 */
export class Metadata {

	constructor(cb) {
		this.callbacks = cb == undefined ? [] : cb;
		this.metadata = {};

		Array.from(document.querySelectorAll("html head meta[property]")).forEach(e => {
			if (e.getAttribute("property").startsWith("kite9:")) {
				var name = e.getAttribute("property").substring(6);
				var value = e.getAttribute("content");
				this.metadata[name] = value;
			}
		});
		
		this.callbacks.forEach(cb => {
			cb(this.metadata);
		});
	}

	/**
	 * Attach this to the transition object to allow Metadata to 
	 * receive messages about transition changes.
	 */
	transitionCallback(response) {

		for (var pair of response.headers.entries()) {
			if (pair[0].startsWith("kite9-")) {
				const name = pair[0].substring(6);
				this.metadata[name] = pair[1];
			}
		}
		
		this.callbacks.forEach(cb => {
			cb(this.metadata);
		});
	};
}



