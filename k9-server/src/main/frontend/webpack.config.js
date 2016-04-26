var path = require('path')

module.exports = {
	devtool: 'inline-source-map',
	cache: true,
	debug: true,
    context: __dirname,
    entry: {
    	bundle:  './app.jsx'
    },
    output: {
        path: __dirname + "/../../../target/classes/static/dist",
        filename: "bundle.js"
    },

    module: {
    	loaders: [
    	   {  
			   test: /\.jsx?$/,
			   exclude: /(node_modules)/,
			   loader: 'babel',
			   query: {
			      presets: ['es2015', 'stage-0', 'react']
			   }
		   },       
    	          
    	   { test: /\.less$/, loader: "style!css!less" },
    	   { test: /\.css$/, loader: "style!css" },
    	   { test: /\.(png|woff|woff2|eot|ttf|svg)$/, loader: "url-loader?limit=1000000" } 
    	]    	
    }

}