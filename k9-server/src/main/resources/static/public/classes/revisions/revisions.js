/**
 * Keeps track of the revisions of the document that are created.
 * This is done to speed up client-side undo/redo
 */
export class Revisions {
	
	constructor(callbacks) {
		this.details = [];
		this.callbacks = callbacks;
	}
	
	
	update(details) {
		this.details = details;
		
		if (this.current == undefined) {
			// see if a current sha is set
			this.details
				.filter(r => r.current)
				.forEach(r => this.current = r.sha1);
		}
		
		this.callbacks.forEach(cb => cb(this.details, this.current));
	}
	
	setCurrent(sha1) {
		this.current = sha1;
		this.callbacks.forEach(cb => cb(this.details, this.current));
	}
	
}