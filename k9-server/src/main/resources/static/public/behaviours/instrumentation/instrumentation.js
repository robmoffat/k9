/**
 * Creates the instrumentation menu, which can be populated by other behaviours.
 */
export function getInstrumentation() {
	var nav = document.getElementById("--instrumentation");
	if (nav) {
		return nav;
	} else {
		var cssId = 'instrumentation-css';  
		if (!document.getElementById(cssId)) {
		    var head  = document.getElementsByTagName('head')[0];
		    var link  = document.createElement('link');
		    link.id   = cssId;
		    link.rel  = 'stylesheet';
		    link.type = 'text/css';
		    link.href = '/public/behaviours/instrumentation/instrumentation.css';
		    link.media = 'all';
		    head.appendChild(link);
		}
		
		nav = document.createElement("div");
		nav.setAttribute("id", "--instrumentation");
		nav.setAttribute("class", "instrumentation");
		document.querySelector("body").appendChild(nav);
		return nav;
	}
	
}