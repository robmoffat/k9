import React from 'react'
import ReactDOM from 'react-dom'
import jQuery from 'jquery'
import d3 from 'd3'
import polyfill from 'innersvg-polyfill'

const xmlSerializer = new XMLSerializer();

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

			elementsData.enter().append("g").html(function(data) {
				var renderingInformation = data.parentElement.parentElement;
				var adlElement = renderingInformation.parentElement;
				var layerGroup = this.parentElement;
				var toRender = d3.select(data)
				var out = '';
				
				toRender.each(function (d, i) {
					out = out + xmlSerializer.serializeToString(this);
				})
				
				return out;
			}).attr("id", function(data) {
				return react.props.id+"-"+layer+"-"+data.attributes['element-id'].value.replace(":","-")
			}).attr("key", function(data) {
				return data.attributes['element-id'].value
			});
//			
//			elementsData.transition().each(function() {
//				console.log("transitioning "+this);
//			});
//						
			elementsData.exit().remove();
			
		});
	}
}

function mergeContents(domWithin, d3From, d3To) {
	if (domFrom.name == domTo.name) {
		if (domFrom.hash = hash(domTo)) {
			
		}
	} else {
		domFrom.remove();
		d3.select(domWithin).append(domTo.name).html().each(transitionAttributes)
	}
}

function hash(d3Element) {
	
	if (d3Element.hash != undefined) {
		return d3Element.hash;
	}
}

function transitionAttributes(dom, i) {
	
}

