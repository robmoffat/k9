import { hasLastSelected } from "/public/bundles/api.js";
import { form, ok, cancel, text, hidden, formValues } from '/public/bundles/form.js';

 export function initNewProjectContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='NewProject']")
		}
	}

	return function(event, cm) {
		
		const e = hasLastSelected(selector(), true);
		
		if (e){
			cm.addControl(event, "/public/behaviours/rest/NewProject/add.svg", "New Project", 
					function(e2, selector) {
						cm.clear(event);
						cm.get(event).appendChild(
							form([
								text('Title', undefined, {'required': true}),
								text('Description'),
								text('Stub', undefined, {'pattern' : "^[a-zA-Z_\\-]+$"}),
								hidden('type', 'NewProject'),
								ok('ok', {}, () => {
									const values = formValues();
									values['subjectUri'] = e.getAttribute('id');
									cm.destroy();
									transition.postCommands([values]);
								}),
								
							]))
							
					}		
				)
		}
	}
}


