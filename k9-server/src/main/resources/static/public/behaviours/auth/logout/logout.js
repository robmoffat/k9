import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection, createUniqueId } from '/public/bundles/api.js';
import { text, form, ok, cancel, inlineButtons, formValues, password, p } from '/public/bundles/form.js';


export function initLogoutContextMenuCallback(transition, metadata, templateUri, selector, action) {
	
	if (selector == undefined) {
		selector = function() {
			if (metadata.get('user-page')) {
				return getMainSvg().querySelectorAll("[k9-ui~='auth'].selected");
			} else {
				return [];
			}
		}
	}
	
	/**
	 * Provides a label option for the context menu
	 */
	return function(event, contextMenu) {
		
		const selectedElements = hasLastSelected(selector());
		
		if (selectedElements.length > 0) {
			contextMenu.addControl(event, "/public/behaviours/auth/logout/logout.svg", "Log Out", 
				function(e2, selector) {
					contextMenu.clear(event);
					var htmlElement = contextMenu.get(event);
					htmlElement.appendChild(form([
						p('Logout: are you sure?'),
						inlineButtons([
							ok('logout', {}, () => {
								transition.get('/logout');
							}),
							cancel('cancel', [], () => contextMenu.destroy())
						])
					], 'logout'));
				}		
			)
		}
	}
}
