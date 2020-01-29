import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection, createUniqueId } from '/public/bundles/api.js';
import { text, form, ok, cancel, inlineButtons, formValues, password } from '/public/bundles/form.js';


export function initLoginContextMenuCallback(transition, metadata, templateUri, selector, action) {
	
	if (selector == undefined) {
		selector = function() {
			if (!metadata.get('user-page')) {
				return getMainSvg().querySelectorAll("[k9-ui~='auth']");
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
			contextMenu.addControl(event, "/public/behaviours/auth/login/login.svg", "Log In", 
				function(e2, selector) {
					contextMenu.clear(event);
					var htmlElement = contextMenu.get(event);
					htmlElement.appendChild(form([
						text('User Name', undefined, { required: true}),
						password('Password', undefined, { required: true}),
						inlineButtons([
							ok('login', {}, (e) => {
								e.preventDefault();
								const values = formValues('login');
								contextMenu.destroy();
								fetch('/oauth/token', {
									method: 'POST',
									credentials: 'omit',
									headers: {
										"Content-Type": "application/x-www-form-urlencoded",
										"Accept": "application/json",
										"Authorization": "Basic "+btoa(values.userName + ":" + values.password)
									},
									body: "grant_type=client_credentials" 
								})
								.then(r => {
									if (!r.ok) {
										return transition.get('/login-failed');
									} else {
										return r.json();
									}
								}).then(r => {
									if (r) {
										transition.setCredentials(r['access_token']);
										transition.get('/api/users')
									}
								}).catch(e => {
									console.log(e);
									transition.get('/login-failed');
								})
							}),
							cancel('cancel', [], () => contextMenu.destroy())
						])
					], 'login'));
				}		
			)
		}
	}
}
