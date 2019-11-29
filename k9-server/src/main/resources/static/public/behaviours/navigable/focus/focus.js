import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected } from '/public/bundles/api.js';


export function initFocusContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[id][k9-ui~=focus]");
		}
	}
	
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const e = hasLastSelected(selector(), true);
		
		if (e) {
			contextMenu.addControl(event, "/public/behaviours/navigable/focus/focus.svg", "Focus On",
				function(e2, selector) {
					contextMenu.destroy();
					var url = e.getAttribute("id");
					const projection = url.indexOf('{');
					if (projection != -1) {
						url = url.substring(0, projection);
					}
					transition.get(url);
			});
		}
	}
}
