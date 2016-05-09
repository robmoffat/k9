import React from 'react'
import ReactDOM from 'react-dom'
import jQuery from 'jquery'
import d3 from 'd3'
import polyfill from 'innersvg-polyfill'

export default class ADLSpace extends React.Component {
	
	layerId(layer) {
		return this.props.id+"-layer-"+layer;
	}

	render() {
		return (<svg id={this.props.id} width={this.props.width} height={this.props.height} xmlns="http://www.w3.org/2000/svg" />)
	}
	
	processLayer(topElement, layerName, layerGroup) {
		
		var ids = [];	// should contain IDs in layer.
		var newIds = this.processElement(topElement, layerName, 1, layerGroup);

		// do remove unnecessary ids.
		
		// re-order contents to be same as newIds
	}
	
	processElement(element, layerName, depth, layerGroup) {
		
		var out = [];
		var element = jQuery(element);
		var renderingInformation = element.children("renderingInformation");

		if (renderingInformation.length > 0) {
			var svg = renderingInformation.children("displayData").children('g[layer="'+layerName+'"]');
			
			if (svg.length > 0) {
				var id = element.attr('id')
				this.updateElementInLayer(svg, layerGroup, id)
			}
		}
		
		var react = this;
		jQuery.each(element.children("[id]"), function(k, e) {
			var elements = react.processElement(e, layerName, depth+1, layerGroup);
			out.push.apply(out, elements)
		})	

		return out;
	}
	
	updateElementInLayer(svg, layerGroup, id) {
		layerGroup.append(svg);
	}
	
	removeElementFromLayer(layer, id) {
		
		
	}
	
	
	componentDidMount() {
		var xml = jQuery.parseXML(this.props.content);
		var xmlSerializer = new XMLSerializer();
		var react = this;
		var groups = [];
		var dom = ReactDOM.findDOMNode(this);
		
		// create the defs
		var d3Defs = d3.select(dom).selectAll("defs");
		var svgDefs = d3.select(xml).selectAll("defs")[0];
		
		var defsData = d3Defs.data(svgDefs);
		defsData.enter().append("defs").html(function (data) {
			var str = xmlSerializer.serializeToString(data);
			return str;
		});
		
		defsData.exit().remove();
		
		// create the layers
		var d3Groups = d3.select(dom).selectAll("g")
		var layersData = d3Groups.data(this.props.layers, function(d) {
			return react.props.id+"-"+d;
		});
		
		layersData.enter().append("g")
			.attr('id', function(d) {
				return react.props.id+"-"+d;
			})
			.attr('layer', function (d) {
				return d;
			});
		layersData.exit().remove("g");
		layersData.order();
		
		// create the content
		var elements = d3.select(xml).selectAll("*[id]")[0];
		var elementsData = layersData.selectAll("g").data(elements, function(key) {
			return react.props.id+"-layer-"+d3.select(key).attr('id')
		})
		
		elementsData.enter().append("g").html(function(data) {
			var parent = this.parentElement;
			var layer = d3.select(parent).attr('layer')
			var toRender = d3.select(data).select('renderingInformation displayData g[layer="'+layer+'"] g')
			if (toRender[0][0] != null) {
				var str = xmlSerializer.serializeToString(toRender[0][0]);
				return str;
			} else {
				return null;
			}
		});
	}
}

