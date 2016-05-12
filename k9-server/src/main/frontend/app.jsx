import React from 'react';
import { render } from 'react-dom'
import ADLSpace from './adl/components/ADLSpace.jsx'
import jQuery from 'jquery'

class App extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {

		var dataElem = jQuery("#data")
		var initialText = dataElem.html()
		var layers = [ 'SHADOW', 'MAIN' ];
		var react = this;	
		
		return (
				
			<div>
				<ADLSpace id={"ADLSpace1"} content={initialText} width={1000} height={500} layers={layers} />
				<button class="btn-default" onClick={function() {
					doMainRender();
					
				}}>Press Me</button>
			</div>
		)
	}
}


function doMainRender() {
	
	render(
			<App />,
			document.getElementById('react')
	)	
	
}

doMainRender();



