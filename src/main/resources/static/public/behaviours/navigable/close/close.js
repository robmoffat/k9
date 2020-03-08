import { icon } from '/public/bundles/form.js';


var closeUrl;

export function closeMetadataCallback(metadata) {
	closeUrl = metadata['self'];
}

export function closeInstrumentationCallback(nav) {
	
	var close = nav.querySelector("#--close");
	
	if ((close == undefined) && (closeUrl != undefined)) {
	    nav.appendChild(icon('--close', "Close", '/public/behaviours/navigable/close/close.svg', function() {
	    	window.location.href = closeUrl;
	    }));
	}
	
	if ((closeUrl == undefined) && (close != undefined)) {
		nav.removeChild(close);
	}
}