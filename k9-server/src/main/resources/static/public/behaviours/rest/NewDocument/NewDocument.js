import { hasLastSelected } from "/public/bundles/api.js";
import { form, ok, cancel, text, hidden } from '/public/bundles/form.js';

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
		
		const e = hasLastSelected(selector());
		
		if (e.length > 0){
			cm.addControl(event, "/public/behaviours/rest/new.svg", "New Document", 
					function(e2, selector) {
						contextMenu.clear(event);
						contextMenu.get(event).appendChild(
							form([
								text('Title'),
								text('Description'),
								text('TemplateUri'),
								hidden('type', 'NewDocument'),
								ok('ok', {}, () => {
									const values = formValues();
									const steps = Array.from(selectedElements).map(e => createEditStep(e, values['EnterText']));
									transition.postCommands(steps);
									cm.destroy();
								}),
								
							])
							
							
								
						selectedElements.forEach(e => action(e, templateUri, transition));
						transition.postCommandList();
					}		
				)
		}
	}
}


