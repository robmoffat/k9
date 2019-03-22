export function initNavigable(transition) {

	function onClick(event) {
		var v = event.currentTarget;
		var url = v.getAttribute("href");
		transition.get(url);
	}

	window.addEventListener('load', function() {
		document.querySelector("div.main").querySelectorAll("[href]").forEach(function(v) {
			v.removeEventListener("click", onClick);
			v.addEventListener("click", onClick);
	    })
	})
}