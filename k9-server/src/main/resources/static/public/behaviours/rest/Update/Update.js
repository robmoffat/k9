import { hasLastSelected } from "/public/bundles/api.js";
import { form, ok, cancel, text, hidden, formValues } from '/public/bundles/form.js';


export function initUpdateContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='Update']")
		}
	}
	
	/**
	 * Provides a delete option for the context menu
	 */
	return function(event, cm) {
		
		const e = hasLastSelected(selector(), true);
		
		if (e){
			cm.addControl(event, "/public/behaviours/rest/Update/update.svg", "Update Details", 
					function(e2, selector) {
						cm.clear(event);
						cm.get(event).appendChild(
							form([
								text('Title', e.querySelector('[k9-elem=title]').textContent, {'required': true}),
								text('Description', e.querySelector('[k9-elem=description]').textContent, {'required': true}),
								hidden('type', 'Update'),
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

