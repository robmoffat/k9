import React from 'react'
import ReactDOM from 'react-dom'
import jQuery from 'jquery'
import d3 from 'd3'
import polyfill from 'innersvg-polyfill'


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
		
	update(content) {	
		var xml = jQuery.parseXML(content);
		var xmlSerializer = new XMLSerializer();
		var react = this;
		var groups = [];
		var dom = ReactDOM.findDOMNode(this);
		
		// add SVG style
		var size = d3.select(xml).select("diagram > renderingInformation > size");
		d3.select(dom).attr("style", function() {
			return  d3.select(xml).selectAll("svg").attr("style");
		}).transition()
			.attr("width", function() { return size.attr("x") })
			.attr("height", function() { return size.attr("y") })
		
		
		// create the defs
		var d3Defs = d3.select(dom).selectAll("defs").remove();
		var svgDefs = d3.select(xml).selectAll("defs > *");
		d3.select(dom).append("defs").html(function (data) {
			var out = '';
			svgDefs.each(function (d, i) {
				out = out + xmlSerializer.serializeToString(this);
			})
			
			return out;
		});
				
		// create the layers
		var d3Groups = d3.select(dom).selectAll("g")
		var layersData = d3Groups.data(this.props.layers, function(d) { return react.props.id+"-"+d; });
		layersData.enter().append("g").attr('layer', function (d) { return d; });
		layersData.exit().remove("g");
		layersData.order();
		
		
		// populate the layers
		layersData.each(function (layer, i) {
			var elements = d3.select(xml).selectAll('*[id] > renderingInformation > displayData > g[layer="'+layer+'"]')[0];
			var elementsData = d3.select(this).selectAll("g").data(elements, function(key) {
				var adlElement = key.parentElement.parentElement.parentElement
				return d3.select(adlElement).attr('id')
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
			});
			
		});
	}
}

