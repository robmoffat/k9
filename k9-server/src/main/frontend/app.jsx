import React from 'react';
import { render } from 'react-dom'
import ADLSpace from './adl/components/ADLSpace.jsx'
import jQuery from 'jquery'

class App extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {

		var layers = [ 'SHADOW', 'MAIN' ];
		var react = this;	
		
		return (
				
			<div>
				<ADLSpace id={"ADLSpace1"} content={this.props.xml} width={1000} height={500} layers={layers} />
				<button class="btn-default" onClick={function() {
					doMainRender();
					
				}}>Press Me</button>
			</div>
		)
	}
}


function doMainRender() {
	
	d3.xml('http://localhost:8080/api/renderer/random', 'text/adl-svg+xml', function(x) {	
		render(
				<App xml={x} />,
				document.getElementById('react')
		)	
	}) 
	
}

doMainRender()