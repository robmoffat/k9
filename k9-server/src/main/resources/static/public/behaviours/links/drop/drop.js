import { isLink, getKite9Target } from '/public/bundles/api.js';


/**
 * Allows you to drop a single element onto a link
 */
export function initLinkDropLocator() {
	
	return function(dragTargets, evt) {
		if (dragTargets.length != 1) {
			return [];
		}
		
		var dropTarget = getKite9Target(evt.target);
		
		if (isLink(dropTarget)) {
			var out = dropTarget.getAttribute("k9-ui");
			if (!out.includes("drop")) {
				return [];
			} else {
				return [ dropTarget ];
			}
		}

		return [ ];
	}	
}