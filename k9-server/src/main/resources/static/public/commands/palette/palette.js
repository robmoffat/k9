import { getContextMenu, registerContextMenuCallback, destroyContextMenu } from "../context-menu.js";
import { mouseover, mouseout } from "../../hoverable.js";
import { transition } from "/publlc/behaviours/transition/transition.js"


function createInsertStep(e, id) {
	return {
		"type": 'INSERT',
		"arg1": e.getAttribute('id'),
		"arg2": "http://localhost:8080/public/landing/one.xml#"+id
	}
}

function getUri() {
	const href = document.URL;
	return href.replace(".html", ".xml")
}

/**
 * Creates the context menu within the main svg element,
 * positioning it relative to the event that created it.
 */
function getPalette(event) {
	var palette = document.querySelector("#palette");
	if (palette) {
		return palette;
	} else {
		palette = document.createElement("div");
		palette.setAttribute("id", "palette");
		palette.setAttribute("class", "palette");
		document.querySelector("body").appendChild(palette);

		$(palette).load("http://localhost:8080/public/landing/one.svg", function (rt, textStatus, request) {
			document.querySelector("div.palette").querySelectorAll("[id]").forEach(function (v) {
				v.addEventListener("click", dropIn);
				v.addEventListener("mouseover", mouseover)
				v.addEventListener("mouseout", mouseout)
			});
		})

	}
	return palette;	
}

/**
 * Performs the function of dropping the palette element into the diagram.
 */
function dropIn(event) {
	const selectedElements = document.querySelectorAll("[id].selected.insertable");
	const droppingElement = document.querySelector("[id].mouseover")
	const steps = Array.from(selectedElements).map(e => createInsertStep(e, droppingElement.getAttribute("id")));
	const data = {
		input: {
			uri: getUri()
		},
		steps: steps
	}; 
			
	destroyContextMenu();
	$.post({
		url: '/api/v1/command',
		data: JSON.stringify(data),
		dataType: 'xml',
		headers: {
			"Accept": "image/svg+xml"
		},
		contentType:"application/json; charset=utf-8",
		success: function(ob, status, jqXHR) {
			transition(ob.documentElement);
		}
	
	});
}

/**
 * Provides a delete option for the context menu
 */
registerContextMenuCallback(function(event) {
	
	const selectedElements = document.querySelectorAll("[id].lastSelected.insertable");
	
	if (selectedElements.length > 0) {
	
		var htmlElement = getContextMenu(event);
		
		var img = document.createElement("img");
		htmlElement.appendChild(img);
		
		img.setAttribute("title", "Edit");
		img.setAttribute("src", "../scripts/commands/palette/palette.svg");
		img.addEventListener("click", function(event) {
			getPalette(event);
		});
	}
});
