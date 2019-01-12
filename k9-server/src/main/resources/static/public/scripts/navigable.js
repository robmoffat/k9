import { replace } from "./transition.js";
import "../libraries/jquery.min.js";

function navigate(url) {
	// let's try just replacing the svg diagram first.
	$("body svg").each(function(container) {
		$.get({
			url: url,
			success: function(ob, status, jqXHR) {
				var fromNodes = document.querySelectorAll("*[id]");
				var ids = Array.from(fromNodes).map(o => o.getAttribute("id"));
				alert(ids)
				alert(ob.querySelectorAll("*[id]").length)
			}
		
		});
	});
	
}

window.addEventListener('load', function() {
    var selectedElement;
    
    document.querySelectorAll(".navigable").forEach(function(v) {
    	
    	var url = v.getAttribute("href");
    	
    	if (url) {
    		v.addEventListener("click", function() {
    			navigate(url);
        	})  		
    	}
    })
})