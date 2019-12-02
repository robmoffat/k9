import { getMainSvg } from '/public/bundles/screen.js';
import { ensureCss } from '/public/bundles/css.js';
import { icon } from '/public/bundles/form.js';


export function toggleInstrumentationCallback(nav) {
	
	var toggle = nav.querySelector("#--indication-toggle");
	const main = getMainSvg().parentElement;

	if (toggle == undefined) {

	    toggle = nav.appendChild(icon('--indication-toggle', "Toggle Indicators", '/public/behaviours/indication/toggle/toggle.svg', function() {
	    	if (main.classList.contains('indicators-on')) {
	    		main.classList.remove('indicators-on');
	    		toggle.classList.remove('on');
	    	} else {
	    		main.classList.add('indicators-on');
	    		toggle.classList.add('on');
	    	}
	    }));
	    
		ensureCss('/public/behaviours/indication/indicators.css');
		main.classList.add("indicators-on");
		toggle.classList.add('on');
		

	}

}