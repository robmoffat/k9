import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection, createUniqueId } from '/public/bundles/api.js';
import { text, form, ok, cancel, inlineButtons, formValues, password, email } from '/public/bundles/form.js';


export function initRegisterContextMenuCallback(transition, templateUri, selector, action) {
	
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
			contextMenu.addControl(event, "/public/behaviours/auth/register/register.svg", "Register", 
				function(e2, selector) {
					contextMenu.clear(event);
					var htmlElement = contextMenu.get(event);
					
					function onchange() {
						alert('hi');
					}
					
					var pwf1 = password('Password', undefined, { required: true, minlength: 8}, onchange);
					var pwf2 = password('Password (Again)', undefined, { required: true, minlength: 8}, onchange);
										
					var f = form([
						text('User Name', undefined, { required: true}),
						pwf1,
						pwf2,
						email('Email Address', undefined, { required: true}),
						inlineButtons([
							ok('register', {}, () => {
								
								if (f.reportValidity()) {
									const values = formValues('register');
									const pw1 = values.password;
									const pw2 = values.passwordAgain;
									
									const command = {
										type: 'RegisterUser',
										username: values.userName,
										password: pw1,
										email: values.emailAddress
									}
									transition.postCommands([ command ]);
									contextMenu.destroy();
								}
								
							}),
							cancel('cancel', [], () => contextMenu.destroy())
						])
					], 'register');
					htmlElement.appendChild(f);
				}		
			)
		}
	}
}
