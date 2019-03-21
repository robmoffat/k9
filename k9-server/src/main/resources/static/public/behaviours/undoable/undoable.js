



window.addEventListener('load', function() {
	
	const box = document.querySelector("div.main").querySelectorAll("[id]").forEach(function(v) {
    	// set up listeners
    	v.removeEventListener("click", click);
    	v.addEventListener("click", click);
    })
})