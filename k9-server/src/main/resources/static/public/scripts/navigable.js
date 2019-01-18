
import '../libraries/jquery.min.js'
import {transition} from '../bundles/transition.js'
import {a, b} from '../bundles/test.js'

function navigate(url) {
	$.get({
		url: url,
		success: function(ob, status, jqXHR) {
			a(5);
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
    document.querySelectorAll(".navigable").forEach(function(v) {
		v.removeEventListener("click", onClick);
		v.addEventListener("click", onClick);
    })
})