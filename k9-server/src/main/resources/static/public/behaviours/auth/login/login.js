import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection, createUniqueId } from '/public/bundles/api.js';
import { text, form, ok, cancel, inlineButtons, formValues, password } from '/public/bundles/form.js';


export function initLoginContextMenuCallback(transition, templateUri, selector, action) {
	
	if (selector == undefined) {
		selector = function() {
			return getMainSvg().querySelectorAll("[k9-ui~='auth'].selected");
		}
	}
	
	/**
	 * Provides a label option for the context menu
	 */
	return function(event, contextMenu) {
		
		const selectedElements = hasLastSelected(selector());
		
		if (selectedElements.length > 0) {
			contextMenu.addControl(event, "/public/behaviours/auth/login/login.svg", "Log In", 
				function(e2, selector) {
					contextMenu.clear(event);
					var htmlElement = contextMenu.get(event);
					htmlElement.appendChild(form([
						text('User Name', undefined, { required: true}),
						password('Password', undefined, { required: true}),
						inlineButtons([
							ok('login', {}, () => {
								const values = formValues('login');
								const body = "username="
									+values.userName
									+"&password="
									+values.password
									+"&submit=Login";
								const uri = '/login';
								transition.postForm(uri, body);
								contextMenu.destroy();
							}),
							cancel('cancel', [], () => contextMenu.destroy())
						])
					], 'login'));
				}		
			)
		}
	}
}
