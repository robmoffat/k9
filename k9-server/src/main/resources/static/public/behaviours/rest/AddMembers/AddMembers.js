import { hasLastSelected } from "/public/bundles/api.js";
import { form, ok, cancel, email, hidden, formValues, requirements } from '/public/bundles/form.js';

 export function initAddMembersContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='AddMembers']")
		}
	}

	return function(event, cm) {
		
		const e = hasLastSelected(selector(), true);
		
		if (e){
			cm.addControl(event, "/public/behaviours/rest/AddMembers/add.svg", "Add Members", 
					function(e2, selector) {
						cm.clear(event);
						cm.get(event).appendChild(
							form([
								email('Email Addresses', undefined, {'multiple': true}),
								hidden('type', 'AddMembers'),
								ok('ok', {}, () => {
									const values = formValues();
									values['subjectUri'] = e.getAttribute('subject-uri');
									cm.destroy();
									transition.postCommands([values]);
								}),
								
							]))
							
					}		
				)
		}
	}
}


