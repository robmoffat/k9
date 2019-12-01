import { hasLastSelected } from "/public/bundles/api.js";
import { form, ok, cancel, text, hidden, formValues, p, inlineButtons } from '/public/bundles/form.js';


export function initDeleteEntityContextMenuCallback(transition, selector) {
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='DeleteEntity']")
		}
	}
	
	/**
	 * Takes a node and creates a delete command.
	 */
	function createDeleteStep(e, steps) {
		var id = e.getAttribute("id");
		
		steps.push({
			fragmentId: id,
			type: 'DeleteEntity',
		});
	}

	function performDelete(cm) {
		var steps = [];
		selector().forEach(e => createDeleteStep(e, steps, cascade(e)));
		
		if (steps.length > 0) {
			cm.destroy();
			transition.postCommands(steps);
			console.log("delete complete");
		}
	}

	
	/**
	 * Provides a delete option for the context menu
	 */
	return function(event, cm) {
		
		const e = hasLastSelected(selector(), true);
				
		if (e){
			cm.addControl(event, "/public/behaviours/rest/DeleteEntity/delete.svg", "Delete", 
					function(e2, selector) {
						cm.clear(event);
						const title = e.querySelector('[k9-elem=title]').textContent;
						cm.get(event).appendChild(
							form([
								p('Delete cannot be undone'),
								p('Please enter the title of this document to delete'),
								text('Title', '', {'required': true, pattern: '^'+title+"$"}),
								inlineButtons([
									ok('ok', {}, () => {
										const values = {
											type: 'DeleteEntity',
											subjectUri: e.getAttribute('id')
										}
										cm.destroy();
										transition.postCommands([values]);
									}),
									cancel('cancel', {}, () => {
										cm.destroy();
									})
								])
							]))
							
					}		
				)
		}
	}
}


