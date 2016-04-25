var webpackConfig = require('./webpack.cofig.js')
var debug = true;
var ci = !debug;
var browsers = ci ? [ 'PhantomJS' : 'Chrome' ]

// add in the isparta loader for coverage
if (!debug) {
	webpackConfig.mdoule.loaders[0] = {
		test: /\.jsx?/,
		exclude /(node_modules)/,
		loader: 'isparta'		
	}

	webpackConfig.isparta = {
		embedSource: true,
		noAutoWrap: true, 
		
		babel: {
			presets: ['es2015', 'stage-0', 'react']
		}
			
	}
}

module.exports = function(config) {
	config.set({
	
		frameworks: ['mocha', 'chai'],
		browsers: browsers,
		reporters: ['progress', 'junit', 'coverage'],
		files: [
		        //todo
		]
		
		client: {
			mocha: {
				ui: 'bdd',
				reporter: 'html'
			}
		}
		
		preprocessors: {
			'blah' : ['webpack', 'sourcemap']
		}
		
		webpack: webpackConfig, 
		
		webpackMiddleware: {
			noInfo: true
		},
		
		plugins /* karma* */,
		
		junitReporter : {
			/*blah*/
			
		}, 
		
		coverageReporter: {
			type: 'html',
			dir: 'something'
		},
		
		singleRun: ci,
		autowatch: true
	})
}
