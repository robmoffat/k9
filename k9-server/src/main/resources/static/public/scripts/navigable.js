
import '../libraries/jquery.min.js'
import {transition} from '../bundles/transition.js'

function navigate(url) {
	$.get({
		url: url,
		success: function(ob, status, jqXHR) {
			transition(ob.documentElement);
		}
	
	});
}

function onClick(event) {
	var v = event.currentTarget;
	var url = v.getAttribute("href");
	navigate(url);
}

window.addEventListener('load', function() {
    document.querySelectorAll("[href]").forEach(function(v) {
		v.removeEventListener("click", onClick);
		v.addEventListener("click", onClick);
    })
})