import { getMainSvg } from '/public/bundles/screen.js';

export function initEditContextMenuCallback(transition, selector, action) {

	if (action == undefined) {
		action = function onClick(event) {
			var v = event.currentTarget;
			var url = v.getAttribute("href");
			transition.get(url);
		};
	}
	
	
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[id][k9-ui~=open]");
		}
	}
	
	/**
	 * Provides a link option for the context menu
	 */
	return function(event, contextMenu) {
		
		const e = hasLastSelected(selector(), true);
		
		if (e) {
		
			contextMenu.addControl(event, "/public/behaviours/navigable/open/open.svg", "Open For Editing",
					function(e2, selector) {
				contextMenu.destroy();
				palette.open(event, "insert");
			});
		}
	}

	window.addEventListener('load', function() {
		selector().forEach(function(v) {
			v.removeEventListener("click", onClick);
			v.addEventListener("click", onClick);
	    })
	})
}

var lastUrl;

export function navigableMetadataCallback(metadata) {
	
	if (lastUrl != metadata.self) {
		lastUrl = metadata.self;
		history.pushState({}, "", lastUrl)
	}
}
