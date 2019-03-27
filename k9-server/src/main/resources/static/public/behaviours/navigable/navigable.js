export function initNavigable(transition, selector) {

	function onClick(event) {
		var v = event.currentTarget;
		var url = v.getAttribute("href");
		transition.get(url);
	}
	
	if (selector == undefined) {
		selector = function() {
			return document.querySelectorAll("div.main [href]");
		}
	}

	window.addEventListener('load', function() {
		selector().forEach(function(v) {
			v.removeEventListener("click", onClick);
			v.addEventListener("click", onClick);
	    })
	})
}