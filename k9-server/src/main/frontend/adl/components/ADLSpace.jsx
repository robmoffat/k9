import React from 'react'
import ReactDOM from 'react-dom'
import jQuery from 'jquery'
import setup_rendering from '../lib/kite9_rendering'
import setup_primitives from '../lib/kite9_primitives'
import setup_style_chooser from '../lib/kite9_style_chooser'

import Raphael from 'raphael'

class ADLSpace extends React.Component {

	render() {
		return (<svg id="ADLSpace" width="1000" height="1000"></svg>)
	}
	
	componentDidMount() {
		var dom = ReactDOM.findDOMNode(this);

		var kite9 = {}; 
		kite9.isTouch = jQuery("html").hasClass("touch");
		jQuery("body").addClass(kite9.isTouch ? "touch" : "mouse");
		setup_primitives(kite9);
		setup_rendering(kite9);
		kite9.main_control = kite9.new_control(Raphael("ADLSpace", 1, 1), dom);
        setup_style_chooser(kite9, kite9.main_control);
		kite9.load(kite9.main_control, "http://localhost:8080/dist/test-card-rendered.xml", undefined);
	}
}


export default ADLSpace
