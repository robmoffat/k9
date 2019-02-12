// rollup.config.js
import typescript from 'rollup-plugin-typescript';
import resolve from 'rollup-plugin-node-resolve';
import replace from 'rollup-plugin-replace';
const commonjs = require("rollup-plugin-commonjs");

export default {
  input: './index.ts',
  plugins: [
    typescript(),
    resolve(), 
    commonjs(),
    replace({
      'process.env.NODE_ENV': JSON.stringify( 'development' )
    })
  ],
  output: [
  {
    file: '../../resources/static/public/bundles/transition.js',
    format: 'esm'
  },
  {
	    file: '../../../../target/classes/static/public/bundles/transition.js',
	    format: 'esm'
  }]
}