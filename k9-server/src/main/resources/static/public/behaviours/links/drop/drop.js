import { isLink, isTerminator, getKite9Target, parseInfo, createUniqueId } from '/public/bundles/api.js';


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
			} else if (!isTerminator(dragTargets[0])) {
				return [ dropTarget ];
			}
		}

		return [ ];
	}	
}

export function initLinkDropCallback(transition) {
	
	
	return function(dragTargets, evt, dropTargets) {
		const connectionDropTargets = dropTargets.filter(t => isLink(t));

		
		if (connectionDropTargets.length != 1) {
			return;
		}
		
		if (dragTargets.length != 1) {
			return;
		}
		
		const drop = connectionDropTargets[0];
		const drag = dragTargets[0];
		const info = parseInfo(drop);
		const ends = info['link'].split(" ");
		
		const dragId = drag.getAttribute("id");
		const dropId = drop.getAttribute("id");
		
		if (ends.indexOf(dragId) > -1) {
			// already connected
			return;
		}
		
		// first, duplicate the link twice
		const newId = createUniqueId();
		transition.push({
			"type": 'CopyLink',
			"uriStr": "#"+dropId,
			"beforeFragmentId" : dropId,
			"newId": newId,
			"fromId": ends[0],
			"toId": dragId
		})
		
		transition.push({
			"type": 'CopyLink',
			"uriStr": "#"+dropId,
			"beforeFragmentId" : dropId,
			"newId": newId,
			"fromId": dragId,
			"toId": ends[1]
		})
		
		// delete old link
		transition.push({
			"type": "ADLDelete",
			"fragmentId": dropId,
			"cascade": true
		})
	}
	
}