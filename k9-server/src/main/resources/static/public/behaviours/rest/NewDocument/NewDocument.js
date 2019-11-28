import { hasLastSelected } from "/public/bundles/api.js";
import { form, ok, cancel, text, hidden, formValues } from '/public/bundles/form.js';

export function initNewDocumentContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='NewDocument'].selected")
		}
	}
	/**
	 * Provides a delete option for the context menu
	 */
	return function(event, cm) {
		
		const e = hasLastSelected(selector(), true);
		
		if (e){
			cm.addControl(event, "/public/behaviours/rest/NewDocument/add.svg", "New Document", 
					function(e2, selector) {
						cm.clear(event);
						cm.get(event).appendChild(
							form([
								text('Title', undefined, {'required': true}),
								text('Description'),
								text('Template Uri'),
								hidden('type', 'NewDocument'),
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


