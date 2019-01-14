//var fromNodes = document.querySelectorAll("*[id]");
//			var fromIds = Array.from(fromNodes).map(o => o.getAttribute("id"));
//			
//			var toNodes = ob.querySelectorAll("*[id]");
//			var toIds = Array.from(toNodes).map(o => o.getAttribute("id"));
//			
//			var transformIds = fromIds.filter(id => -1 != toIds.indexOf(id));
//			var enteredIds = toIds.filter(id => -1 == fromIds.indexOf(id));
//			var exitedIds = fromIds.filter(id => -1 == fromIds.indexOf(id));


import { transition } from './transition.js' 

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
    var selectedElement;
    
    document.querySelectorAll(".navigable").forEach(function(v) {
		v.removeEventListener("click", onClick);
		v.addEventListener("click", onClick);
    })
})