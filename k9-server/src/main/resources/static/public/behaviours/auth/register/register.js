import { getMainSvg } from '/public/bundles/screen.js';
import { hasLastSelected, parseInfo, getContainingDiagram, reverseDirection, createUniqueId } from '/public/bundles/api.js';
import { text, form, ok, cancel, inlineButtons, formValues, password, email } from '/public/bundles/form.js';


export function initRegisterContextMenuCallback(transition, metadata, selector) {
	
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
			contextMenu.addControl(event, "/public/behaviours/auth/register/register.svg", "Register", 
				function(e2, selector) {
					contextMenu.clear(event);
					var htmlElement = contextMenu.get(event);
					
					function passwordsSame() {
						var p1 = document.querySelector("#password");
						var p2 = document.querySelector("#passwordAgain")
						if (p1.value != p2.value) {
							p2.setCustomValidity("passwords should be the same");
						} else {
							p2.setCustomValidity('');
						}
					}
										
					var f = form([
						text('User Name', undefined, { required: true}),
						password('Password', undefined, { required: true, minlength: 8}),
						password('Password (Again)', undefined, { required: true, minlength: 8}),
						email('Email Address', undefined, { required: true}),
						inlineButtons([
							ok('register', {}, (a) => {
								
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
								} else {
									a.preventDefault();
								}
								
							}),
							cancel('cancel', [], () => contextMenu.destroy())
						])
					], 'register');
					
					htmlElement.appendChild(f);
					
					document.querySelector("#password").addEventListener('change', passwordsSame);
					document.querySelector("#passwordAgain").addEventListener('change', passwordsSame);
					
				}		
			)
		}
	}
}
