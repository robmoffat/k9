import React from 'react'
import ReactDOM from 'react-dom'
import jQuery from 'jquery'
import d3 from 'd3'
import polyfill from 'innersvg-polyfill'

const xmlSerializer = new XMLSerializer();

var round = 1;

export default class ADLSpace extends React.Component {
	
	render() {
		return (<svg id={this.props.id} width={this.props.width} height={this.props.height} xmlns="http://www.w3.org/2000/svg" />)
	}
	
	componentDidMount() {
		this.update(this.props.content);
	}		
	
	componentDidUpdate() {
		this.update(this.props.content);
	}
		
	update(xml) {	
		round = round + 1;
		var react = this;
		var groups = [];
		var dom = ReactDOM.findDOMNode(this);
		var d3dom = d3.select(dom)
		var d3xml = d3.select(xml)
		
		// add SVG style
		var size = d3xml.select("diagram > renderingInformation > size");
		d3.select(dom).attr("style", function() {
			return  d3xml.selectAll("svg").attr("style");
		}).transition()
			.attr("width", function() { return size.attr("x") })
			.attr("height", function() { return size.attr("y") })
		
		
		// create the defs
		var d3Defs = d3dom.selectAll("defs").remove();
		var svgDefs = d3xml.selectAll("defs > *");
		d3dom.append("defs").html(function (data) {
			var out = '';
			svgDefs.each(function (d, i) {
				out = out + xmlSerializer.serializeToString(this);
			})
			
			return out;
		});
				
		// create the layers
		this.props.layers.forEach(function(layer, i) {
			var groupLayer = d3dom.select("g[group-layer='"+layer+"']")
			if (groupLayer.size() == 0) {
				d3dom.append("g").attr("group-layer", layer);
				groupLayer = d3dom.select("g[group-layer='"+layer+"']")
			}
			
			var elements = d3xml.selectAll('*[id] > renderingInformation > displayData > g[layer="'+layer+'"]')[0];
			var d3groups = groupLayer.selectAll("g[key]")
			var elementsData = d3groups.data(elements, function(data) {
				var key = data.attributes['element-id'].value
				return key
			})

			elementsData.enter().append("g")
				.attr("id", function(data) {
					return react.props.id+"-"+layer+"-"+data.attributes['element-id'].value.replace(":","-")
				}).attr("key", function(data) {
					return data.attributes['element-id'].value
				});
			
			elementsData.each(function(data) {
				mergeElements(this.parentElement, data, this)
			});
						
			elementsData.exit().remove();
		});
	}
}

function mergeElements(domWithin, domFrom, domTo) {
	if (hashElement(domFrom) != hashElement(domTo)) {
		if (domFrom.tagName == 'text') {
			// transition text content (if any)
			d3.select(domTo.children).remove();
			d3.select(domTo).text(domFrom.textContent)
			mergeAttributes(domFrom, domTo)
		} else {
			// transition the elements
			mergeAttributes(domFrom, domTo)
			
			if ((domFrom.children.length != domTo.children.length) && (domTo.children.length > 0)) {
				var id = domWithin.attributes.item("id")
				console.log("Different Dom size "+domFrom.textContent+" "+ ((id != null) ? id.value : ""))
			}

			var processed = [];
			
			for (var i = 0; i < domFrom.children.length; i++) {
				var e = domFrom.children.item(i)
				var matchTo = findMatchingNode(processed, domTo.children, e)
				
				if (matchTo == undefined) {
					matchTo = d3.select(domTo).append(e.tagName)[0][0]
				}
				
				processed.push(matchTo)
				mergeElements(domTo, e, matchTo)
			}
			
			
			// remove any surplus elements left in domTo
			for (var i = 0; i < domTo.children.length; i++) {
				var e = domTo.children.item(i)
				
				var r = e.attributes.getNamedItem("round")
				
				if (processed.indexOf(e) == -1) {
					d3.select(e).remove();
					i = i - 1;  //because we removed one
					console.log("removed " +e.textContent)
				} 
				
//				else if (r != round) {
//					d3.select(e).remove();
//					console.log("removed the other way "+e.tagName)
//				}
			}			
		}
	} 
}

function mergeAttributes(domFrom, domTo) {
	var trans = d3.select(domTo).transition();
	
	// transition attributes in domFrom
	var attrs = domFrom.attributes
	for (var i = 0; i < attrs.length; i++) {
		var attr = attrs.item(i)
		trans.attr(attr.name, attr.value)
	}
	
	// remove any surplus elements left in domTo
	attrs = domTo.attributes
	for (var i = 0; i < attrs.length; i++) {
		var attr = attrs.item(i)
		if (attr.name != 'key') {
			if (domFrom.attributes.getNamedItem(attr.name) == undefined) {
				domTo.attributes.removeNamedItem(attr.name)
			}
		}
	}
}


function findMatchingNode(doneList, available, e) {	
	for (var i = 0; i < available.length; i++) {
		var opt = available.item(i)
		if (doneList.indexOf(opt) == -1) {
			if ((opt.tagName == e.tagName) && (opt.children.length == e.children.length)) {
				return opt;
			}
		}
	}
	
	return undefined;
}

function hashElement(domElement) {	
	if (domElement.hash != undefined) {
		return domElement.hash;
	}
	
	var h = 0;
	var attrs = domElement.attributes
	for (var length = attrs.length, i = 0; i < length; i++) {
		var a = attrs.item(i)
		var key = a.name
		var value = a.value
		h += hashString(key) + 5*hashString(value);
	}
	
	var h2 = 0;
	var cn = domElement.childNodes
	for(i=0; i < cn.length; i++) {
		var a = cn.item(i)
		if (a.nodeType == 3) {
			// text element
			h2 += hashString(a.textContent)
		} else if (a.nodeType == 1) {
			// element
			h2 += hashElement(a);
		}
	}
	
	domElement.hash = h+h2;
	return h+h2;
}

function hashString(str) {
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


