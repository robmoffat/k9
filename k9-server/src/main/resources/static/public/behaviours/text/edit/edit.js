import { hasLastSelected } from '/public/bundles/api.js';
import { textarea, form, ok, cancel, inlineButtons, formValues } from '/public/bundles/form.js';

export function initEditContextMenuCallback(transition, selector, defaultSelector) {
	
	function createEditStep(e, text) {
		return {
			"type": 'SetText',
			"fragmentId": e.getAttribute('id'),
			"newText": text
		}
	}
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("[id][k9-ui~='edit'].selected");
		}
	}
	
	if (defaultSelector == undefined) {
		defaultSelector = function(e) {
			const text = e.querySelector("[k9-ui~='text']");
			
			return (text != null) ? text : e;
		}
	}

	/**
	 * Provides a text-edit option for the context menu
	 */
	return function(event, cm) {
		
		const selectedElements = hasLastSelected(selector());
		
		if (selectedElements.length > 0) {
			
			cm.addControl(event, "/public/behaviours/text/edit/edit.svg", 'Edit Text', () => {
				const defaultText = defaultSelector(hasLastSelected(selectedElements, true)).textContent.trim();
				cm.clear();
				var htmlElement = cm.get(event);
				htmlElement.appendChild(form([
					textarea('Enter Text', defaultText, { rows: 10 }),
					inlineButtons([
						ok('ok', {}, () => {
							const values = formValues('editText');
							const steps = Array.from(selectedElements).map(e => createEditStep(e, values['EnterText']));
							transition.postCommands(steps);
							cm.destroy();
						}),
						cancel('cancel', [], () => cm.destroy())
					])
				], 'editText'));
				
			});
		}
	}
}
