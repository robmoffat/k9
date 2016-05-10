/**
	 * Builds a new, empty control object.
	 */
	kite9.new_control = function(paper, element) {
		var animationMaster = null;
		var animating = "go";
		var control = {};
		var moveCount = 0;
		var pathCount = 0;
		
		control.paper = paper;
		control.element = element;
				
		control.set_size = function(maxx, maxy) {
			control.paper.setSize(maxx, maxy);
		};

		const applyStyle = function(contents, styleAttr) {
			// remove fill as this breaks on firefox.
//			if (BrowserDetect.browser=='Firefox') {
//				if ((styleAttr.fill) && (styleAttr.fill.indexOf("-") !== -1)) {
//					var fillParts = styleAttr.fill.split("-");
//					styleAttr.fill = fillParts[1];
//				}
//			}
			
			contents.attr(styleAttr);
		}
		
		
		/**
		 * Suspends animation.
		 */
		control.animateSuspend = function() {
			while (kite9.animating === "go") {
				setTimeout(function() {
					kite9.animateSuspend();
				}, 50);
				return;
			}
		
			animating = "suspend";
			pathCount = 0;
			moveCount = 0;
		};
			
		/**
		 * Starts the suspended animation
		 */
		control.animateStart = function() {
			/*$('#move_count').html(moveCount);
			$('#path_count').html(pathCount);
			$('#errors').html('');*/
			
			if (animating === "suspend") {
				animating = "go";
				if (animationMaster !== null) {
					animationMaster.resume();
				}
			}
		};

		/**
		 * Adds elements to the animation
		 */
		control.animateWith = function(item, params, instant) {
			if (item === undefined) {
				return;
			}
			
			if (instant) {
				item.attr(params);
				return;
			}
			
			if (animationMaster === null) {
				animationMaster = item;
			}
			
			
			if (item !== animationMaster) {
				item.animateWith(animationMaster, "gordon", params, 1000, ">");
			} else {
				item.animate(params, 1000, ">", function () {
					animationMaster = null;
				});
				
				if (animating === "suspend") {
					item.pause();
				}
			}
		};
		
		
		
		/**
		 * A useful hash function.  
		 */
		kite9.hash = function(str) {
			var hash = 0;
			if ((str == undefined) || (str==null)) {
				return hash;
			}
			
			if (str.length == 0) 
				return hash;
			    
			for (var i = 0; i < str.length; i++) {
			        var char = str.charCodeAt(i);
			        hash = ((hash<<5)-hash)+char;
			}

			return hash;
		};