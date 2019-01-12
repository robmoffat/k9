window.addEventListener('load', function() {
    console.log('All assets are loaded')
    
    var selectedElement;
    
    document.querySelectorAll(".hoverable").forEach(function(v) {
		var classes = v.classList;
    	v.addEventListener("mouseover", function() {
    		if (!classes.contains("mouseover")) {
    			classes.add("mouseover");
    			
    			if (selectedElement != undefined) {
    				selectedElement.classList.remove("mouseover")
    				selectedElement = v;
    			}
    		 }
    	})
    	
    	v.addEventListener("mouseout", function() {
    		classes.remove("mouseover");
    		if (selectedElement == v) {
    			selectedElement = undefined;
    		}
    	})
    })
})