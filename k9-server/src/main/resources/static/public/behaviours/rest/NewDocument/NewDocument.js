import { hasLastSelected } from "/public/bundles/api.js";
import { form, ok, cancel, text, hidden, formValues, select, change } from '/public/bundles/form.js';

const templates = {
	'basic': '/public/examples/basic/example.adl',
	'risk-first': '/public/examples/risk-first/example.adl'		
}


export function initNewDocumentContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='NewDocument']")
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
						
						const templateUri = text('Template Uri', undefined, {'disabled': true, 'required': true});
						
						cm.get(event).appendChild(
							form([
								text('Title', undefined, {'required': true}),
								text('Description'),
								//change(
								select('Template', undefined, {}, ['basic', 'risk-first', 'custom']),
								select('Format', 'svg', {},['svg', 'png', 'adl'] ),
//									function(event) {
//										if (event.target.value != 'custom') {
//											templateUri.querySelector('input').setAttribute('disabled', '');
//										} else {
//											templateUri.querySelector('input').removeAttribute('disabled');
//										}
//										return true;
//									}),
								templateUri,
								hidden('type', 'NewDocument'),
								ok('ok', {}, () => {
									const values = formValues();
									values['subjectUri'] = e.getAttribute('subject-uri');
									if (values['template'] != 'custom') {
										const path = templates[values['template']]
										values.templateUri = new URL(path, document.location).href;
									}
									cm.destroy();
									transition.postCommands([values]);
								}),
								
							]))
							
					}		
				)
		}
	}
}


