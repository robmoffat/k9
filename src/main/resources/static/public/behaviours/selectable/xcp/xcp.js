import { hasLastSelected } from '/public/bundles/api.js';
import { getMainSvg } from '/public/bundles/screen.js';
import { getBeforeId } from '/public/bundles/ordering.js';

/**
 * Handles cut, copy, paste 
 */

var pasteUris = [];

export function initXCPContextMenuCallback(transition, metadata, cutSelector, copySelector, pasteSelector) {
	
	if (cutSelector == undefined) {
		cutSelector = function() {
			return getMainSvg().querySelectorAll("[id][k9-ui~='delete'].selected")
		}
	}
	
	if (copySelector == undefined) {
		copySelector = function() {
			return getMainSvg().querySelectorAll("[id][k9-ui~='copy'].selected")
		}
	}
	
	if (pasteSelector == undefined) {
		pasteSelector = function() {
			return getMainSvg().querySelectorAll("[k9-ui~=insert].selected");
		}
	}
	
	function getElementUri(e) {
		var id = e.getAttribute("id");
		return metadata.get("revision")+"#"+id;	
	}
	
	function performCopy(copy, cut) {
		var steps = [];
		pasteUris = [];
		copy.forEach(e => {
			if (cut.indexOf(e) > -1) {
				steps.push({
					fragmentId: e.getAttribute("id"),
					type: 'ADLDelete',
					cascade: true
				});
			}
			
			pasteUris.push(getElementUri(e));
		})
		
		if (steps.length > 0) {
			transition.postCommands(steps);
		}
	}
	
	function performPaste(event, destinations) {
		var steps = []
		pasteUris.forEach(uri => {
			destinations.forEach(d => {
				const beforeId = getBeforeId(d, event, []);
				steps.push({
					"type": 'Copy',
					"fragmentId": d.getAttribute('id'),
					"uriStr": uri,
					"beforeFragmentId" : beforeId,
					"deep" : true
				});
			});
		})
		
		if (steps.length > 0) {
			transition.postCommands(steps);
		}
	}
		
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const copyElements = hasLastSelected(copySelector());
		const cutElements = hasLastSelected(cutSelector());
		const pasteElements = hasLastSelected(pasteSelector());
		
		if (copyElements.length > 0) {
		
			if (cutElements.length > 0) {
				contextMenu.addControl(event, "/public/behaviours/selectable/xcp/cut.svg", "Cut", (e2, selector) => {
					contextMenu.destroy();
					performCopy(Array.from(copyElements), Array.from(cutElements));
				});
			}
			
			contextMenu.addControl(event, "/public/behaviours/selectable/xcp/copy.svg", "Copy", (e2, selector) => {
				contextMenu.destroy();
				performCopy(Array.from(copyElements), []);
			});
		}
		
		if ((pasteElements.length > 0) && (pasteUris.length > 0)) {
			contextMenu.addControl(event, "/public/behaviours/selectable/xcp/paste.svg", "Paste", (e2, selector) => {
				contextMenu.destroy();
				performPaste(event, Array.from(pasteElements));
			});
		}
	}
}

