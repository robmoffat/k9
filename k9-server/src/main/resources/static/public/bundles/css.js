
/**
 * Adds an extra css import 
 */
export function ensureCss(css) {
	if (!document.getElementById(css)) {
	    var head  = document.getElementsByTagName('head')[0];
	    var link  = document.createElement('link');
	    link.id   = css;
	    link.rel  = 'stylesheet';
	    link.type = 'text/css';
	    link.href = css;
	    link.media = 'all';
	    head.appendChild(link);
	}
} 