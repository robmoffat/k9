var _slicedToArray = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/**
 * A tweenable color.
 *
 * @typedef {Object} Color
 *
 * @property {string} middleware - The name of this middleware.
 * @property {number} r - The hexadecimal red value.
 * @property {number} g - The hexadecimal green value.
 * @property {number} b - The hexadecimal blue value.
 * @property {number} a - The alpha value.
 */

var name = 'color';

var htmlColors = {
  'aliceblue': '#F0F8FF',
  'antiquewhite': '#FAEBD7',
  'aqua': '#00FFFF',
  'aquamarine': '#7FFFD4',
  'azure': '#F0FFFF',
  'beige': '#F5F5DC',
  'bisque': '#FFE4C4',
  'black': '#000000',
  'blanchedalmond': '#FFEBCD',
  'blue': '#0000FF',
  'blueviolet': '#8A2BE2',
  'brown': '#A52A2A',
  'burlywood': '#DEB887',
  'cadetblue': '#5F9EA0',
  'chartreuse': '#7FFF00',
  'chocolate': '#D2691E',
  'coral': '#FF7F50',
  'cornflowerblue': '#6495ED',
  'cornsilk': '#FFF8DC',
  'crimson': '#DC143C',
  'cyan': '#00FFFF',
  'darkblue': '#00008B',
  'darkcyan': '#008B8B',
  'darkgoldenrod': '#B8860B',
  'darkgray': '#A9A9A9',
  'darkgreen': '#006400',
  'darkgrey': '#A9A9A9',
  'darkkhaki': '#BDB76B',
  'darkmagenta': '#8B008B',
  'darkolivegreen': '#556B2F',
  'darkorange': '#FF8C00',
  'darkorchid': '#9932CC',
  'darkred': '#8B0000',
  'darksalmon': '#E9967A',
  'darkseagreen': '#8FBC8F',
  'darkslateblue': '#483D8B',
  'darkslategray': '#2F4F4F',
  'darkslategrey': '#2F4F4F',
  'darkturquoise': '#00CED1',
  'darkviolet': '#9400D3',
  'deeppink': '#FF1493',
  'deepskyblue': '#00BFFF',
  'dimgray': '#696969',
  'dimgrey': '#696969',
  'dodgerblue': '#1E90FF',
  'firebrick': '#B22222',
  'floralwhite': '#FFFAF0',
  'forestgreen': '#228B22',
  'fuchsia': '#FF00FF',
  'gainsboro': '#DCDCDC',
  'ghostwhite': '#F8F8FF',
  'gold': '#FFD700',
  'goldenrod': '#DAA520',
  'gray': '#808080',
  'green': '#008000',
  'greenyellow': '#ADFF2F',
  'grey': '#808080',
  'honeydew': '#F0FFF0',
  'hotpink': '#FF69B4',
  'indianred': '#CD5C5C',
  'indigo': '#4B0082',
  'ivory': '#FFFFF0',
  'khaki': '#F0E68C',
  'lavender': '#E6E6FA',
  'lavenderblush': '#FFF0F5',
  'lawngreen': '#7CFC00',
  'lemonchiffon': '#FFFACD',
  'lightblue': '#ADD8E6',
  'lightcoral': '#F08080',
  'lightcyan': '#E0FFFF',
  'lightgoldenrodyellow': '#FAFAD2',
  'lightgray': '#D3D3D3',
  'lightgreen': '#90EE90',
  'lightgrey': '#D3D3D3',
  'lightpink': '#FFB6C1',
  'lightsalmon': '#FFA07A',
  'lightseagreen': '#20B2AA',
  'lightskyblue': '#87CEFA',
  'lightslategray': '#778899',
  'lightslategrey': '#778899',
  'lightsteelblue': '#B0C4DE',
  'lightyellow': '#FFFFE0',
  'lime': '#00FF00',
  'limegreen': '#32CD32',
  'linen': '#FAF0E6',
  'magenta': '#FF00FF',
  'maroon': '#800000',
  'mediumaquamarine': '#66CDAA',
  'mediumblue': '#0000CD',
  'mediumorchid': '#BA55D3',
  'mediumpurple': '#9370DB',
  'mediumseagreen': '#3CB371',
  'mediumslateblue': '#7B68EE',
  'mediumspringgreen': '#00FA9A',
  'mediumturquoise': '#48D1CC',
  'mediumvioletred': '#C71585',
  'midnightblue': '#191970',
  'mintcream': '#F5FFFA',
  'mistyrose': '#FFE4E1',
  'moccasin': '#FFE4B5',
  'navajowhite': '#FFDEAD',
  'navy': '#000080',
  'oldlace': '#FDF5E6',
  'olive': '#808000',
  'olivedrab': '#6B8E23',
  'orange': '#FFA500',
  'orangered': '#FF4500',
  'orchid': '#DA70D6',
  'palegoldenrod': '#EEE8AA',
  'palegreen': '#98FB98',
  'paleturquoise': '#AFEEEE',
  'palevioletred': '#DB7093',
  'papayawhip': '#FFEFD5',
  'peachpuff': '#FFDAB9',
  'peru': '#CD853F',
  'pink': '#FFC0CB',
  'plum': '#DDA0DD',
  'powderblue': '#B0E0E6',
  'purple': '#800080',
  'rebeccapurple': '#663399',
  'red': '#FF0000',
  'rosybrown': '#BC8F8F',
  'royalblue': '#4169E1',
  'saddlebrown': '#8B4513',
  'salmon': '#FA8072',
  'sandybrown': '#F4A460',
  'seagreen': '#2E8B57',
  'seashell': '#FFF5EE',
  'sienna': '#A0522D',
  'silver': '#C0C0C0',
  'skyblue': '#87CEEB',
  'slateblue': '#6A5ACD',
  'slategray': '#708090',
  'slategrey': '#708090',
  'snow': '#FFFAFA',
  'springgreen': '#00FF7F',
  'steelblue': '#4682B4',
  'tan': '#D2B48C',
  'teal': '#008080',
  'thistle': '#D8BFD8',
  'tomato': '#FF6347',
  'turquoise': '#40E0D0',
  'violet': '#EE82EE',
  'wheat': '#F5DEB3',
  'white': '#FFFFFF',
  'whitesmoke': '#F5F5F5',
  'yellow': '#FFFF00',
  'yellowgreen': '#9ACD32'
};

var htmlColorKeys = Object.keys(htmlColors);

/**
 * Converts a color string to a Color.
 *
 * @param {*} x - A potential color string.
 *
 * @returns {*}
 *
 * @example
 * input('#FFFFFF')
 */
var input = function input(x) {
  if (typeof x === 'string') {
    if (hex(x)) {
      return hexToColor(x);
    } else if (rgb(x)) {
      return rgbToColor(x);
    } else if (rgba(x)) {
      return rgbaToColor(x);
    } else if (html(x)) {
      return htmlToColor(x);
    }
  }

  return x;
};

/**
 * Converts a Color to a rgba color string.
 *
 * @param {*} x - A potential Color.
 *
 * @returns {*}
 *
 * @example
 * output(color)
 */
var output = function output(x) {
  if ((typeof x === 'undefined' ? 'undefined' : _typeof(x)) === 'object' && x.middleware === name) {
    return colorToRgba(x);
  }

  return x;
};

/**
 * Is string a hex color?
 *
 * @param {string} str - A potential hex color.
 *
 * @returns {boolean}
 *
 * @example
 * hex('#FFFFFF')
 */
var hex = function hex(str) {
  return str.match(/^#(?:[0-9a-f]{3}){1,2}$/i) !== null;
};

/**
 * Is string a rgba color?
 *
 * @param {string} str - A potential rgba color.
 *
 * @returns {boolean}
 *
 * @example
 * rgba('rgba(255,255,255,1)')
 */
var rgba = function rgba(str) {
  return str.startsWith('rgba(');
};

/**
 * Is string a rgb color?
 *
 * @param {string} str - A potential rgb color.
 *
 * @returns {boolean}
 *
 * @example
 * rgb('rgb(255,255,255)')
 */
var rgb = function rgb(str) {
  return str.startsWith('rgb(');
};

/**
 * Is string a html color?
 *
 * @param {string} str - A potential html color.
 *
 * @returns {boolean}
 *
 * @example
 * html('limegreen')
 */
var html = function html(str) {
  return htmlColorKeys.indexOf(str) !== -1;
};

/**
 * Converts a hex string to a Color.
 *
 * @param {string} hex - A hex color.
 *
 * @returns {Color}
 *
 * @example
 * hexToColor('#FFFFFF')
 */
var hexToColor = function hexToColor(hex) {
  var x = hex.replace('#', '');

  if (x.length === 3) {
    var y = '';

    for (var i = 0; i < 3; i++) {
      var v = x.charAt(i);
      y += '' + v + v;
    }

    x = y;
  }

  return {
    middleware: name,
    r: parseInt(x.slice(0, 2), 16),
    g: parseInt(x.slice(2, 4), 16),
    b: parseInt(x.slice(4, 6), 16),
    a: 1
  };
};

/**
 * Converts a rgb string to a Color.
 *
 * @param {string} rgb - A rgb color.
 *
 * @returns {Color}
 *
 * @example
 * rgbToColor('rgb(255,255,255)')
 */
var rgbToColor = function rgbToColor(rgb) {
  var x = rgb.replace(/\s/g, '');

  var _x$substring$split = x.substring(4, x.length - 1).split(','),
      _x$substring$split2 = _slicedToArray(_x$substring$split, 3),
      r = _x$substring$split2[0],
      g = _x$substring$split2[1],
      b = _x$substring$split2[2];

  return {
    middleware: name,
    r: parseFloat(r),
    g: parseFloat(g),
    b: parseFloat(b),
    a: 1
  };
};

/**
 * Converts a rgba string to a Color.
 *
 * @param {string} rgba - A rgba color.
 *
 * @returns {Color}
 *
 * @example
 * rgbaToColor('rgba(255,255,255,1)')
 */
var rgbaToColor = function rgbaToColor(rgba) {
  var x = rgba.replace(/\s/g, '');

  var _x$substring$split3 = x.substring(5, x.length - 1).split(','),
      _x$substring$split4 = _slicedToArray(_x$substring$split3, 4),
      r = _x$substring$split4[0],
      g = _x$substring$split4[1],
      b = _x$substring$split4[2],
      a = _x$substring$split4[3];

  return {
    middleware: 'color',
    r: parseFloat(r),
    g: parseFloat(g),
    b: parseFloat(b),
    a: parseFloat(a)
  };
};

/**
 * Converts a html string to a Color.
 *
 * @param {string} html - An html color.
 *
 * @returns {Color}
 *
 * @example
 * htmlToColor('limegreen')
 */
var htmlToColor = function htmlToColor(html) {
  return hexToColor(htmlColors[html]);
};

/**
 * Converts a Color to a rgba color string.
 *
 * @param {Color} color
 *
 * @returns {string}
 *
 * @example
 * colorToRgba(color)
 */
var colorToRgba = function colorToRgba(_ref) {
  var r = _ref.r,
      g = _ref.g,
      b = _ref.b,
      a = _ref.a;
  return 'rgba(' + parseInt(limit(r, 0, 255), 10) + ',' + parseInt(limit(g, 0, 255), 10) + ',' + parseInt(limit(b, 0, 255), 10) + ',' + limit(a, 0, 1) + ')';
};

/**
 * Find the closest number within limits.
 *
 * @param {number} num - The desired number.
 * @param {number} min - The minimum returned number.
 * @param {number} max - the maximum returned number.
 *
 * @returns {number}
 *
 * @example
 * limit(-1, 2, 5)
 */
var limit = function limit(num, min, max) {
  return Math.max(min, Math.min(max, num));
};

var colorMiddleware = { name: name, input: input, output: output };

var _typeof$1 = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/**
 * A naive, but small, clone function.
 *
 * @param {*} value
 *
 * @returns {*}
 *
 * @example
 * clone('hello world')
 */
var clone = function clone(value) {
  if ((typeof value === 'undefined' ? 'undefined' : _typeof$1(value)) !== 'object') {
    return value;
  } else if (Array.isArray(value)) {
    var arr = [];

    for (var i = 0, l = value.length; i < l; i++) {
      arr.push(clone(value[i]));
    }

    return arr;
  } else if (value !== null) {
    var obj = {};

    for (var key in value) {
      obj[key] = clone(value[key]);
    }

    return obj;
  }

  return value;
};

var _typeof$2 = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/**
 * A tweenable unit.
 *
 * @typedef {Object} Unit
 *
 * @property {string} middleware - The name of this middleware.
 * @property {string} values - The type of color string to output.
 */

var name$1 = 'unit';

var units = ['ch', 'cm', 'em', 'ex', 'in', 'mm', 'pc', 'pt', 'px', 'rem', 'vh', 'vmax', 'vmin', 'vw', '%'];

/**
 * Converts a unit string to a Unit.
 *
 * @param {*} x - A potential unit string.
 *
 * @returns {*}
 *
 * @example
 * input('20px')
 */
var input$1 = function input(x) {
  if (typeof x === 'string') {
    var parts = x.split(' ');
    var values = [];

    for (var i = 0, l = parts.length; i < l; i++) {
      var part = parts[i];
      var number = parseFloat(part);
      var unit = part.replace(number, '');

      if (!isNaN(number) && (unit === '' || units.indexOf(unit) !== -1)) {
        values.push([number, unit]);
      } else {
        values.push(part);
      }
    }

    if (values.toString() !== parts.toString()) {
      return { middleware: name$1, values: values };
    }
  }

  return x;
};

/**
 * Converts a Unit to a unit string.
 *
 * @param {*} x - A potential Unit.
 *
 * @returns {*}
 *
 * @example
 * output(unit)
 */
var output$1 = function output(x) {
  if ((typeof x === 'undefined' ? 'undefined' : _typeof$2(x)) === 'object' && x.middleware === name$1) {
    var values = x.values;
    var result = [];

    for (var i = 0, l = values.length; i < l; i++) {
      result.push(values[i].join(''));
    }

    return result.join(' ');
  }

  return x;
};

var unitMiddleware = { name: name$1, input: input$1, output: output$1 };

var config = {
  defaults: {
    keyframe: {
      duration: 250,
      easing: 'easeInOutQuad'
    },
    motionPath: {
      easing: 'easeInOutQuad'
    },
    timeline: {
      alternate: false,
      initialIterations: 0,
      iterations: 1,
      middleware: [colorMiddleware, unitMiddleware],
      queue: 0,
      reverse: false
    }
  }
};

var _typeof$3 = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/**
 * A group of functions to transform/untransform a value.
 *
 * @typedef {Object} Middleware
 *
 * @property {string} name - The name of the middleware.
 * @property {function} input - Transform.
 * @property {function} output - Untransform.
 */

/**
 * Run every part of a value through a function.
 *
 * @param {*} value
 * @param {function} func
 *
 * @returns {*}
 *
 * @example
 * apply(2, n => n * 2)
 */
var apply = function apply(value, func) {
  var v = func(value);

  if ((typeof v === 'undefined' ? 'undefined' : _typeof$3(v)) !== 'object') {
    return v;
  } else if (Array.isArray(v)) {
    var arr = [];

    for (var i = 0, l = v.length; i < l; i++) {
      arr.push(apply(v[i], func));
    }

    return arr;
  } else if (v !== null) {
    var obj = {};

    for (var k in v) {
      obj[k] = apply(v[k], func);
    }

    return obj;
  }

  return v;
};

/**
 * Runs each Middleware input function in turn on a value.
 *
 * @param {*} value
 * @param {Middleware[]} middleware
 *
 * @returns {*}
 *
 * @example
 * input({ foo: 1, bar: [ 2, 3 ] }, middleware)
 */
var input$2 = function input(value, middleware) {
  var v = value;

  for (var i = 0, l = middleware.length; i < l; i++) {
    v = apply(v, middleware[i].input);
  }

  return v;
};

/**
 * Runs each Middleware output function in reverse on a value.
 *
 * @param {*} value
 * @param {Middleware[]} middleware
 *
 * @returns {*}
 *
 * @example
 * output({ foo: 1, bar: [ 2, 3 ] }, middleware)
 */
var output$2 = function output(value, middleware) {
  var v = value;

  for (var i = middleware.length - 1; i >= 0; i--) {
    v = apply(v, middleware[i].output);
  }

  return v;
};

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _typeof$4 = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var _slicedToArray$1 = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

/**
 * The position of an object on a Timeline
 * where 0 is Timeline start and 1 is Timeline finish.
 *
 * @typedef {Object} TimelinePosition
 *
 * @property {Position} start
 * @property {Position} finish
 */

/**
 * A Shape positioned on a Timeline.
 *
 * @typedef {Object} TimelineShape
 *
 * @property {Shape} shape
 * @property {TimelinePosition} timelinePosition
 */

/**
 * The position of an object on a Timeline in milliseconds.
 *
 * @typedef {Object} MsTimelinePosition
 *
 * @property {number} start.
 * @property {number} finish.
 */

/**
 * A Shape positioned on a Timeline (position set in milliseconds).
 *
 * @typedef {Object} MsTimelineShape
 *
 * @property {Shape} shape
 * @property {MsTimelinePosition} timelinePosition
 */

/**
 * A TimelineShape array and their total duration.
 *
 * @typedef {Object} TimelineShapesAndDuration
 *
 * @property {TimelineShape[]} timelineShapes
 * @property {number} duration
 */

/**
 * The options required to calculate the current playback Position.
 *
 * @typedef {Object} PlaybackOptions
 *
 * @property {boolean} alternate - Should the next iteration reverse current direction?
 * @property {number} duration - Milliseconds that each iteration lasts.
 * @property {number} initialIterations - The starting number of iterations.
 * @property {number} iterations - The number of playback interations (additional to initialIterations).
 * @property {boolean} reverse - Should the first iteration start in a reverse direction?
 * @property {number} [started] - The UNIX timestamp of playback start.
 */

/**
 * PlaybackOptions and tween middleware.
 *
 * @typedef {Object} TimelineOptions
 *
 * @extends PlaybackOptions
 * @property {Middleware[]} middleware
 */

/**
 * A Shape and timeline related options.
 *
 * @typedef {Object} ShapeWithOptions
 *
 * @property {(string|number)} [after] - The name of the Shape to queue after (in sequence).
 * @property {(string|number)} [at] - The name of the Shape to queue at (in parallel).
 * @property {(string|number)} name - A unique reference.
 * @property {number} offset - The offset in milliseconds to adjust the queuing of this shape.
 * @property {Shape} shape
 */

/**
 * An object containing Middlware, PlaybackOptions and ShapesWithOptions.
 *
 * @typedef {Object} SortedTimelineProps
 *
 * @property {Middleware[]} middleware
 * @property {PlaybackOptions} playbackOptions
 * @property {ShapeWithOptions[]} shapesWithOptions
 */

/**
 * A sequence of Shapes.
 *
 * @typedef {Object} Timeline
 *
 * @property {Middleware[]} middleware
 * @property {PlaybackOptions} playbackOptions
 * @property {Object} state - Holds the last known state of the timeline.
 * @property {TimelineShape[]} timelineShapes
 */

/**
 * Runs each Middleware input function on every Keyframe's FrameShape.
 *
 * @param {Shape} shape
 * @param {Middleware[]} middleware
 *
 * @example
 * apply(shape, middleware)
 */
var apply$1 = function apply(_ref, middleware) {
  var keyframes = _ref.keyframes;

  for (var i = 0, l = keyframes.length; i < l; i++) {
    var keyframe = keyframes[i];
    keyframe.frameShape = input$2(keyframe.frameShape, middleware);
  }
};

/**
 * Is playback currently in reverse?
 *
 * @param {PlaybackOptions} playbackOptions
 * @param {number} complete - The number of iterations complete.
 *
 * @example
 * currentReverse(playbackOptions, complete)
 */
var currentReverse = function currentReverse(playbackOptions, complete) {
  var reverse = playbackOptions.reverse;

  if (complete === 0) {
    return reverse;
  }

  var alternate = playbackOptions.alternate;
  var initialIterations = playbackOptions.initialIterations;

  var initialReverse = sameDirection(alternate, initialIterations) ? reverse : !reverse;

  return sameDirection(alternate, initialIterations + complete) ? initialReverse : !initialReverse;
};

/**
 * The number of iterations a Timeline has completed.
 *
 * @param {PlaybackOptions} playbackOptions
 * @param {number} opts.at
 *
 * @returns {number}
 *
 * @example
 * iterationsComplete(playbackOptions, 1000)
 */
var iterationsComplete = function iterationsComplete(playbackOptions, at) {
  var duration = playbackOptions.duration;
  var iterations = playbackOptions.iterations;
  var started = playbackOptions.started;

  if (typeof started === 'undefined' || at <= started) {
    return 0;
  }

  var ms = at - started;
  var maxDuration = duration * iterations;

  if (ms >= maxDuration) {
    return iterations;
  }

  return ms / duration;
};

/**
 * Calculate the Timeline Position.
 *
 * @param {number} totalIterations - initialIterations + iterationsComplete.
 * @param {boolean} reverse - Is the Timeline currently in reverse?
 *
 * @returns {Position}
 *
 * @example
 * position(5.43, true)
 */
var position = function position(totalIterations, reverse) {
  var i = totalIterations >= 1 && totalIterations % 1 === 0 ? 1 : totalIterations % 1;

  return reverse ? 1 - i : i;
};

/**
 * Is the direction same as initial direction?
 *
 * @param {boolean} alternate - Is iteration direction alternating?
 * @param {number} iterations - The number of iterations complete.
 *
 * @return {boolean}
 *
 * @example
 * sameDirection(true, 3.25)
 */
var sameDirection = function sameDirection(alternate, iterations) {
  var x = iterations % 2;
  return !alternate || iterations === 0 || x <= 1 && x % 2 > 0;
};

/**
 * Calculate the start position of a Shape on the Timeline.
 *
 * @param {Object} props
 * @param {(string|number)} [props.after]
 * @param {(string|number)} [props.at]
 * @param {MsTimelineShape[]} props.msTimelineShapes
 * @param {number} props.offset
 * @param {number} props.timelineFinish - The current finish of the timeline.
 *
 * @returns {number}
 *
 * @example
 * shapeStart({ 'foo', msTimelineShapes, 200, 2000 })
 */
var shapeStart = function shapeStart(_ref2) {
  var after = _ref2.after,
      at = _ref2.at,
      msTimelineShapes = _ref2.msTimelineShapes,
      offset = _ref2.offset,
      timelineFinish = _ref2.timelineFinish;

  if (typeof after !== 'undefined' || typeof at !== 'undefined') {
    var reference = typeof after !== 'undefined' ? after : at;

    for (var i = 0; i < msTimelineShapes.length; i++) {
      var s = msTimelineShapes[i];

      if (reference === s.shape.name) {
        return (typeof at !== 'undefined' ? s.timelinePosition.start : s.timelinePosition.finish) + offset;
      }
    }

    for (var _i = 0; _i < msTimelineShapes.length; _i++) {
      var _s = msTimelineShapes[_i];

      for (var j = 0; j < _s.shape.keyframes.length; j++) {
        var keyframe = _s.shape.keyframes[j];

        if (reference === keyframe.name) {
          return _s.timelinePosition.start + _s.shape.duration * keyframe.position + offset;
        }
      }
    }

    {
      throw new Error('No Shape or Keyframe matching name \'' + reference + '\'');
    }
  }

  return timelineFinish + offset;
};

/**
 * Create a ShapeWithOptions from an array.
 *
 * @param {Object[]} arr
 * @param {Shape} arr.0
 * @param {Object} arr.1
 *
 * @returns {ShapeWithOptions}
 *
 * @example
 * shapeWithOptionsFromArray(arr, i)
 */
var shapeWithOptionsFromArray = function shapeWithOptionsFromArray(_ref3, i) {
  var _ref4 = _slicedToArray$1(_ref3, 2),
      shape = _ref4[0],
      options = _ref4[1];

  if ((typeof shape === 'undefined' ? 'undefined' : _typeof$4(shape)) !== 'object' || !shape.keyframes) {
    throw new TypeError('When an array is passed to the timeline function the first item must be a Shape');
  }

  if ((typeof options === 'undefined' ? 'undefined' : _typeof$4(options)) !== 'object') {
    throw new TypeError('When an array is passed to the timeline function the second item must be an object');
  }

  var _options$name = options.name,
      name = _options$name === undefined ? i : _options$name,
      _options$queue = options.queue,
      queue = _options$queue === undefined ? config.defaults.timeline.queue : _options$queue;


  if (typeof name !== 'string' && typeof name !== 'number') {
    throw new TypeError('The name prop must be of type string or number');
  }

  if ((typeof queue === 'undefined' ? 'undefined' : _typeof$4(queue)) === 'object' && !Array.isArray(queue) && queue !== null) {
    var after = queue.after,
        at = queue.at,
        _queue$offset = queue.offset,
        offset = _queue$offset === undefined ? 0 : _queue$offset;


    if (typeof offset !== 'undefined' && typeof offset !== 'number') {
      throw new TypeError('The queue.offset prop must be of type number');
    }

    if (typeof at !== 'undefined' && typeof after !== 'undefined') {
      throw new TypeError('You cannot pass both queue.at and queue.after props');
    }

    if (typeof at !== 'undefined' && typeof at !== 'string' && typeof at !== 'number') {
      throw new TypeError('The queue.at prop must be of type string or number');
    }

    if (typeof after !== 'undefined' && typeof after !== 'string' && typeof after !== 'number') {
      throw new TypeError('The queue.after prop must be of type string or number');
    }

    if (typeof at !== 'undefined') {
      return { at: at, name: name, offset: offset, shape: shape };
    }

    if (typeof after !== 'undefined') {
      return { after: after, name: name, offset: offset, shape: shape };
    }

    return { name: name, offset: offset, shape: shape };
  } else if (typeof queue === 'number') {
    return { name: name, offset: queue, shape: shape };
  } else if (typeof queue === 'string') {
    return { after: queue, name: name, offset: 0, shape: shape };
  }

  {
    throw new TypeError('The queue prop must be of type number, string or object');
  }

  return { name: name, offset: 0, shape: shape };
};

/**
 * Sorts an array of Shapes, ShapesWithOptions and TimelineOptions.
 *
 * @param {(Shape|Object[]|TimelineOptions)[]} props
 *
 * @returns {SortedTimelineProps}
 *
 * @example
 * sort(props)
 */
var sort = function sort(props) {
  if (props.length === 0) {
    throw new TypeError('The timeline function must be passed at least one Shape');
  }

  var options = {};

  var shapesWithOptions = [];

  for (var i = 0, l = props.length; i < l; i++) {
    var prop = props[i];

    if (Array.isArray(prop)) {
      shapesWithOptions.push(shapeWithOptionsFromArray(prop, i));
    } else {
      if ((typeof prop === 'undefined' ? 'undefined' : _typeof$4(prop)) !== 'object') {
        throw new TypeError('The timeline function must only be passed objects and arrays');
      }

      if (prop.keyframes) {
        shapesWithOptions.push({
          name: i,
          offset: config.defaults.timeline.queue,
          shape: prop
        });
      } else {
        {
          if (i === 0) {
            throw new TypeError('The timeline function must receive a Shape as the first argument');
          } else if (i !== props.length - 1) {
            throw new TypeError('The timeline function must receive options as the final argument');
          }
        }

        options = clone(prop);
      }
    }
  }

  return {
    middleware: validMiddleware(options),
    playbackOptions: validPlaybackOptions(options),
    shapesWithOptions: shapesWithOptions
  };
};

/**
 * Creates a Timeline from one or more Shape.
 * Optionally can take an options object as the last argument,
 * as well as options for each Shape if passed in as an array.
 *
 * @param {...(Shape|Object[]|TimelineOptions)} props
 *
 * @returns {Timeline}
 *
 * @example
 * timeline(circle, [ square, { queue: -200 } ], { duration: 5000 })
 */
var timeline = function timeline() {
  for (var _len = arguments.length, props = Array(_len), _key = 0; _key < _len; _key++) {
    props[_key] = arguments[_key];
  }

  var _sort = sort(props),
      middleware = _sort.middleware,
      playbackOptions = _sort.playbackOptions,
      shapesWithOptions = _sort.shapesWithOptions;

  var _timelineShapesAndDur = timelineShapesAndDuration(shapesWithOptions, middleware),
      duration = _timelineShapesAndDur.duration,
      timelineShapes = _timelineShapesAndDur.timelineShapes;

  if (typeof playbackOptions.duration === 'undefined') {
    playbackOptions.duration = duration;
  }

  var t = { middleware: middleware, playbackOptions: playbackOptions, state: {}, timelineShapes: timelineShapes };

  for (var i = 0, l = timelineShapes.length; i < l; i++) {
    var shape = timelineShapes[i].shape;

    shape.timeline = t;
    shape.timelineIndex = i;
  }

  updateState(t);

  t.event = event(t);

  return t;
};

/**
 * Converts a set of MsTimelineShapes to a set of TimelineShapes
 * given the Timeline start and total duration values.
 *
 * @param {Object} props
 * @param {number} props.duration
 * @param {msTimelineShape[]} props.msTimelineShapes
 * @param {number} props.start
 *
 * @returns {TimelineShape[]}
 *
 * @example
 * timelineShapes()
 */
var timelineShapes = function timelineShapes(_ref5) {
  var duration = _ref5.duration,
      msTimelineShapes = _ref5.msTimelineShapes,
      start = _ref5.start;

  var s = [];

  for (var i = 0, l = msTimelineShapes.length; i < l; i++) {
    var msTimelineShape = msTimelineShapes[i];
    var timelinePosition = msTimelineShape.timelinePosition;

    s.push({
      shape: msTimelineShape.shape,
      timelinePosition: {
        start: (timelinePosition.start - start) / duration,
        finish: (timelinePosition.finish - start) / duration
      }
    });
  }

  return s;
};

/**
 * Converts an array of ShapesWithOptions into TimelineShapes
 * and their total duration.
 *
 * @param {ShapeWithOptions[]} shapesWithOptions
 * @param {Middleware[]} middleware
 *
 * @returns {TimelineShapesAndDuration}
 *
 * @example
 * timelineShapes(shapesWithOptions)
 */
var timelineShapesAndDuration = function timelineShapesAndDuration(shapesWithOptions, middleware) {
  var timelineStart = 0;
  var timelineFinish = 0;

  var msTimelineShapes = [];

  for (var i = 0, l = shapesWithOptions.length; i < l; i++) {
    var _shapesWithOptions$i = shapesWithOptions[i],
        after = _shapesWithOptions$i.after,
        at = _shapesWithOptions$i.at,
        name = _shapesWithOptions$i.name,
        offset = _shapesWithOptions$i.offset,
        shape = _shapesWithOptions$i.shape;


    if (typeof shape.timeline !== 'undefined') {
      throw new Error('A Shape can only be added to one timeline');
    }

    shape.name = name;

    apply$1(shape, middleware);

    var start = shapeStart({
      after: after,
      at: at,
      msTimelineShapes: msTimelineShapes,
      offset: offset,
      timelineFinish: timelineFinish
    });

    var finish = start + shape.duration;

    timelineStart = Math.min(timelineStart, start);
    timelineFinish = Math.max(timelineFinish, finish);

    msTimelineShapes.push({ shape: shape, timelinePosition: { start: start, finish: finish } });
  }

  var timelineDuration = Math.abs(timelineStart - timelineFinish);

  return {
    duration: timelineDuration,
    timelineShapes: timelineShapes({
      duration: timelineDuration,
      msTimelineShapes: msTimelineShapes,
      start: timelineStart
    })
  };
};

/**
 * Updates the Timeline state.
 *
 * @param {Timeline} timeline
 * @param {number} at
 *
 * @example
 * updateState(timeline, Date.now())
 */
var updateState = function updateState(t, at) {
  var playbackOptions = t.playbackOptions;
  var state = t.state;

  state.started = typeof playbackOptions.started !== 'undefined';
  state.iterationsComplete = iterationsComplete(playbackOptions, at);
  state.totalIterations = playbackOptions.initialIterations + state.iterationsComplete;
  state.reverse = currentReverse(playbackOptions, state.iterationsComplete);
  state.finished = playbackOptions.iterations - state.iterationsComplete === 0;
  state.position = position(state.totalIterations, state.reverse);
};

/**
 * Extracts and validates Middlware from an object.
 *
 * @param {Object} opts
 *
 * @returns {Middleware[]}
 *
 * @example
 * validMiddleware(opts)
 */
var validMiddleware = function validMiddleware(_ref7) {
  var _ref7$middleware = _ref7.middleware,
      middleware = _ref7$middleware === undefined ? config.defaults.timeline.middleware : _ref7$middleware;

  if (!Array.isArray(middleware)) {
    throw new TypeError('The timeline function middleware option must be of type array');
  }

  for (var i = 0, l = middleware.length; i < l; i++) {
    var _middleware$i = middleware[i],
        name = _middleware$i.name,
        _input = _middleware$i.input,
        output = _middleware$i.output;


    if (typeof name !== 'string') {
      throw new TypeError('A middleware must have a name prop');
    }

    if (typeof _input !== 'function') {
      throw new TypeError('The ' + name + ' middleware must have an input method');
    }

    if (typeof output !== 'function') {
      throw new TypeError('The ' + name + ' middleware must have an output method');
    }
  }

  return middleware;
};

/**
 * Extracts and validates PlaybackOptions from an object.
 *
 * @param {Object} opts
 *
 * @returns {PlaybackOptions}
 *
 * @example
 * validPlaybackOptions(opts)
 */
var validPlaybackOptions = function validPlaybackOptions(_ref8) {
  var _ref8$alternate = _ref8.alternate,
      alternate = _ref8$alternate === undefined ? config.defaults.timeline.alternate : _ref8$alternate,
      duration = _ref8.duration,
      _ref8$initialIteratio = _ref8.initialIterations,
      initialIterations = _ref8$initialIteratio === undefined ? config.defaults.timeline.initialIterations : _ref8$initialIteratio,
      _ref8$iterations = _ref8.iterations,
      iterations = _ref8$iterations === undefined ? config.defaults.timeline.iterations : _ref8$iterations,
      _ref8$reverse = _ref8.reverse,
      reverse = _ref8$reverse === undefined ? config.defaults.timeline.reverse : _ref8$reverse,
      started = _ref8.started;

  var playbackOptions = {};

  if (typeof duration !== 'undefined') {
    if (typeof duration !== 'number' || duration < 0) {
      throw new TypeError('The timeline function duration option must be a positive number or zero');
    }

    playbackOptions.duration = duration;
  }

  {
    if (typeof alternate !== 'boolean') {
      throw new TypeError('The timeline function alternate option must be true or false');
    }

    if (typeof initialIterations !== 'number' || initialIterations < 0) {
      throw new TypeError('The timeline function initialIterations option must be a positive number or zero');
    }

    if (typeof iterations !== 'number' || iterations < 0) {
      throw new TypeError('The timeline function iterations option must be a positive number or zero');
    }

    if (typeof reverse !== 'boolean') {
      throw new TypeError('The timeline function reverse option must be true or false');
    }
  }

  if (typeof started !== 'undefined') {
    if (typeof started !== 'number' || started < 0) {
      throw new TypeError('The timeline function started option must be a positive number or zero');
    }

    playbackOptions.started = started;
  }

  return _extends({}, playbackOptions, {
    alternate: alternate,
    initialIterations: initialIterations,
    iterations: iterations,
    reverse: reverse
  });
};

var _extends$1 = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

/**
 * An event.
 *
 * @typedef {Object} Event
 *
 * @property {number} at - The time the event occured.
 * @property {string} name - The event name.
 * @property {Object} options - Any additional event data.
 */

/**
 * A Timeline event subscription.
 *
 * @typedef {Object} EventSubscription
 *
 * @property {function} callback
 * @property {string} name
 * @property {number} token
 */

/**
 * An object to hold Timeline EventSubscriptions, and subscribe/unsubscribe functions.
 *
 * @typedef {Object} EventObject
 *
 * @property {Object} previousPlaybackOptions
 * @property {Object} previousState
 * @property {function} subscribe - A function to subscribe to Timeline events.
 * @property {EventSubscription[]} subscriptions
 * @property {function} unsubscribe - A function to unsubscribe to Timeline events.
 */

/**
 * Token incrementor.
 */
var t = 0;

/**
 * Accepted event names.
 */
var acceptedEventNames = ['timeline.start', 'timeline.finish', 'shape.start', 'shape.finish', 'keyframe', 'frame'];

/**
 * An EventObject creator.
 *
 * @param {Timeline} timeline
 *
 * @returns {EventObject}
 *
 * @example
 * event(timeline)
 */
var event = function event(timeline$$1) {
  return {
    previousPlaybackOptions: {},
    previousState: {},
    subscribe: subscribe(timeline$$1),
    subscriptions: [],
    unsubscribe: unsubscribe(timeline$$1)
  };
};

/**
 * Is a Timeline active?
 *
 * @param {Timeline} timeline
 *
 * @returns {boolean}
 *
 * @example
 * active(timeline)
 */
var active = function active(_ref) {
  var event = _ref.event,
      state = _ref.state;
  return state.started && (!state.finished || typeof event.previousState === 'undefined' || !event.previousState.finished);
};

/**
 * A unique list of Timeline EventSubscription names.
 *
 * @param {Timeline} timeline
 *
 * @returns {string[]}
 *
 * @example
 * activeEventNames(timeline)
 */
var activeEventNames = function activeEventNames(_ref2) {
  var subscriptions = _ref2.event.subscriptions;

  var s = [];

  for (var i = 0, l = subscriptions.length; i < l; i++) {
    var name = subscriptions[i].name;

    if (s.indexOf(name) === -1) {
      s.push(name);
    }
  }

  return s;
};

/**
 * Run EventSubscription callbacks for every event that has occured since last check.
 *
 * @param {Timeline} timeline
 *
 * @example
 * events(timeline)
 */
var events = function events(timeline$$1) {
  if (playbackOptionsChanged(timeline$$1)) {
    timeline$$1.event.previousPlaybackOptions = {};
    timeline$$1.event.previousState = {};
  }

  var subscriptions = timeline$$1.event.subscriptions;

  if (subscriptions.length && active(timeline$$1)) {
    var eventNames = activeEventNames(timeline$$1);
    var queue = eventQueue(timeline$$1, eventNames);

    for (var i = 0, l = queue.length; i < l; i++) {
      var _event = queue[i];
      var eventName = _event.name;
      var options = _event.options || {};

      for (var _i = 0, _l = subscriptions.length; _i < _l; _i++) {
        var subscription = subscriptions[_i];

        if (eventName === subscription.name) {
          subscription.callback(options);
        }
      }
    }
  }

  timeline$$1.event.previousPlaybackOptions = _extends$1({}, timeline$$1.playbackOptions);
  timeline$$1.event.previousState = _extends$1({}, timeline$$1.state);
};

/**
 * An array of Events that have occured since last checked.
 *
 * @param {Timeline} timeline
 * @param {string[]} eventNames
 *
 * @returns {Event[]}
 *
 * @example
 * eventQueue(timeline, eventNames)
 */
var eventQueue = function eventQueue(_ref3, eventNames) {
  var previousState = _ref3.event.previousState,
      playbackOptions = _ref3.playbackOptions,
      state = _ref3.state,
      timelineShapes = _ref3.timelineShapes;

  var queue = [];
  var alternate = playbackOptions.alternate,
      duration = playbackOptions.duration,
      initialIterations = playbackOptions.initialIterations,
      iterations = playbackOptions.iterations,
      reverse = playbackOptions.reverse,
      started = playbackOptions.started;

  var max = started + duration * state.iterationsComplete;
  var min = typeof previousState.iterationsComplete !== 'undefined' ? started + duration * previousState.iterationsComplete + 1 : 0;

  var getTimestamps = function getTimestamps(pos) {
    return positionTimestamps({
      alternate: alternate,
      duration: duration,
      initialIterations: initialIterations,
      iterations: iterations,
      max: max,
      min: min,
      position: pos,
      reverse: reverse,
      started: started
    });
  };

  if (eventNames.indexOf('timeline.start') !== -1) {
    var timestamps = getTimestamps(0);

    for (var i = 0, l = timestamps.length; i < l; i++) {
      queue.push({ name: 'timeline.start', at: timestamps[i] });
    }
  }

  if (eventNames.indexOf('timeline.finish') !== -1) {
    var _timestamps = getTimestamps(1);

    for (var _i2 = 0, _l2 = _timestamps.length; _i2 < _l2; _i2++) {
      queue.push({ name: 'timeline.finish', at: _timestamps[_i2] });
    }
  }

  if (eventNames.indexOf('shape.start') !== -1) {
    for (var _i3 = 0, _l3 = timelineShapes.length; _i3 < _l3; _i3++) {
      var _timelineShapes$_i = timelineShapes[_i3],
          shapeName = _timelineShapes$_i.shape.name,
          start = _timelineShapes$_i.timelinePosition.start;

      var _timestamps2 = getTimestamps(start);

      for (var _i = 0, _l = _timestamps2.length; _i < _l; _i++) {
        queue.push({ name: 'shape.start', at: _timestamps2[_i], options: { shapeName: shapeName } });
      }
    }
  }

  if (eventNames.indexOf('shape.finish') !== -1) {
    for (var _i4 = 0, _l4 = timelineShapes.length; _i4 < _l4; _i4++) {
      var _timelineShapes$_i2 = timelineShapes[_i4],
          shapeName = _timelineShapes$_i2.shape.name,
          finish = _timelineShapes$_i2.timelinePosition.finish;

      var _timestamps3 = getTimestamps(finish);

      for (var _i5 = 0, _l5 = _timestamps3.length; _i5 < _l5; _i5++) {
        queue.push({ name: 'shape.finish', at: _timestamps3[_i5], options: { shapeName: shapeName } });
      }
    }
  }

  if (eventNames.indexOf('keyframe') !== -1) {
    for (var _i6 = 0, _l6 = timelineShapes.length; _i6 < _l6; _i6++) {
      var _timelineShapes$_i3 = timelineShapes[_i6],
          _timelineShapes$_i3$s = _timelineShapes$_i3.shape,
          shapeName = _timelineShapes$_i3$s.name,
          keyframes = _timelineShapes$_i3$s.keyframes,
          _timelineShapes$_i3$t = _timelineShapes$_i3.timelinePosition,
          start = _timelineShapes$_i3$t.start,
          finish = _timelineShapes$_i3$t.finish;


      for (var _i7 = 0, _l7 = keyframes.length; _i7 < _l7; _i7++) {
        var _keyframes$_i = keyframes[_i7],
            keyframeName = _keyframes$_i.name,
            position$$1 = _keyframes$_i.position;


        var keyframePosition = start + (finish - start) * position$$1;
        var _timestamps4 = getTimestamps(keyframePosition);

        for (var __i = 0, __l = _timestamps4.length; __i < __l; __i++) {
          queue.push({ name: 'keyframe', at: _timestamps4[__i], options: { keyframeName: keyframeName, shapeName: shapeName } });
        }
      }
    }
  }

  if (eventNames.indexOf('frame') !== -1) {
    queue.push({ name: 'frame', at: max });
  }

  return queue.sort(oldest);
};

/**
 * A sort function for Events.
 *
 * @param {Event} a
 * @param {Event} b
 *
 * @returns {number}
 *
 * @example
 * oldest(event1, event2)
 */
var oldest = function oldest(a, b) {
  return a.at === b.at ? 0 : a.at < b.at ? -1 : 1;
};

/**
 * Have playbackOptions changed since last check?
 *
 * @param {Timeline} timeline
 *
 * @return {boolean}
 *
 * @example
 * playbackOptionsChanged(timeline)
 */
var playbackOptionsChanged = function playbackOptionsChanged(timeline$$1) {
  return JSON.stringify(timeline$$1.playbackOptions) !== JSON.stringify(timeline$$1.event.previousPlaybackOptions);
};

/**
 * Timestamps at which a Timeline was at a Position.
 *
 * @param {Object} opts
 * @param {boolean} opts.alternate
 * @param {number} opts.duration
 * @param {number} initialIterations
 * @param {number} iterations
 * @param {number} opts.max - The maximum bound within which to look for timestamps.
 * @param {number} opts.min - The minimum bound within which to look for timestamps.
 * @param {Position} opts.position - The Position in question.
 * @param {boolean} opts.reverse
 * @param {number} opts.started
 *
 * @returns {number[]}
 *
 * @example
 * positionTimestamps(opts)
 */
var positionTimestamps = function positionTimestamps(_ref4) {
  var alternate = _ref4.alternate,
      duration = _ref4.duration,
      initialIterations = _ref4.initialIterations,
      iterations = _ref4.iterations,
      max = _ref4.max,
      min = _ref4.min,
      position$$1 = _ref4.position,
      reverse = _ref4.reverse,
      started = _ref4.started;

  var startedPosition = position(initialIterations, reverse);
  var finishedTimestamp = started + duration * iterations;

  var timestamps = function timestamps(timestamp) {
    if (timestamp <= max) {
      var timestampReverse = currentReverse({
        alternate: alternate,
        initialIterations: initialIterations,
        iterations: iterations,
        reverse: reverse
      }, iterationsComplete({ duration: duration, iterations: iterations, started: started }, timestamp));

      var positionAtEnd = position$$1 === 0 || position$$1 === 1;
      var timelineFinished = timestamp === finishedTimestamp;
      var finishedAtPosition = position$$1 === 0 && timestampReverse || position$$1 === 1 && !timestampReverse;

      if (timestamp <= finishedTimestamp && (!positionAtEnd || !timelineFinished || finishedAtPosition)) {
        var _t = timestamp >= min ? [timestamp] : [];

        return _t.concat(timestamps(timestamp + timeToSamePosition({
          alternate: alternate,
          duration: duration,
          position: position$$1,
          reverse: timestampReverse
        })));
      }
    }

    return [];
  };

  return timestamps(started + timeToPosition({
    alternate: alternate,
    duration: duration,
    from: startedPosition,
    reverse: reverse,
    to: position$$1
  }));
};

/**
 * The number of milliseconds between two Positions during Timeline playback.
 *
 * @param {Object} opts
 * @param {boolean} opts.alternate
 * @param {number} opts.duration
 * @param {Position} opts.from - The from Position.
 * @param {boolean} opts.reverse - Is Timeline in reverse at the from Position?
 * @param {Position} opts.to - The to Position.
 *
 * @returns {number}
 *
 * @example
 * timeToPosition(opts)
 */
var timeToPosition = function timeToPosition(_ref5) {
  var alternate = _ref5.alternate,
      duration = _ref5.duration,
      from = _ref5.from,
      reverse = _ref5.reverse,
      to = _ref5.to;
  return duration * (alternate ? reverse ? from < to ? to + from : from - to : from > to ? 2 - (to + from) : to - from : reverse ? from === 1 && to === 0 ? 1 : (1 - to + from) % 1 : from === 0 && to === 1 ? 1 : (1 - from + to) % 1);
};

/**
 * The number of milliseconds between the same Position during Timeline playback.
 *
 * @param {Object} opts
 * @param {boolean} opts.alternate
 * @param {number} opts.duration
 * @param {Position} opts.position
 * @param {boolean} opts.reverse - Is Timeline in reverse at the Position?
 *
 * @returns {number}
 *
 * @example
 * timeToSamePosition(opts)
 */
var timeToSamePosition = function timeToSamePosition(_ref6) {
  var alternate = _ref6.alternate,
      duration = _ref6.duration,
      position$$1 = _ref6.position,
      reverse = _ref6.reverse;
  return duration * (alternate ? reverse ? (position$$1 === 0 ? 1 : position$$1) * 2 : 2 - (position$$1 === 1 ? 0 : position$$1) * 2 : 1);
};

/**
 * Creates a subscribe function.
 * The created function adds an EventSubscription to the subscriptions
 * property of an EventObject.
 *
 * @param {Timeline} timeline
 *
 * @returns {function}
 *
 * @example
 * subscribe(timeline)('timeline.start', () => console.log('timeline.start'))
 */
var subscribe = function subscribe(timeline$$1) {
  return function (name, callback) {
    if (validEventName(name)) {
      if (typeof callback !== 'function') {
        throw new TypeError('The subscribe functions second argument must be of type function');
      }

      var token = ++t;

      timeline$$1.event.subscriptions.push({ name: name, callback: callback, token: token });

      return token;
    }
  };
};

/**
 * Is an event name valid?
 *
 * @param {string} name
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * validEventName('timeline.start')
 */
var validEventName = function validEventName(name) {
  {
    if (typeof name !== 'string') {
      throw new TypeError('The subscribe functions first argument must be of type string');
    }

    if (acceptedEventNames.indexOf(name) === -1) {
      throw new TypeError('The subscribe functions first argument was not a valid event name');
    }
  }

  return true;
};

/**
 * Creates an unsubscribe function.
 * Created function removes an EventSubscription from the subscriptions
 * property of an EventObject, given the Event token.
 *
 * @param {Timeline} timeline
 *
 * @returns {function}
 *
 * @example
 * unsubscribe(timeline)(token)
 */
var unsubscribe = function unsubscribe(timeline$$1) {
  return function (token) {
    var subscriptions = timeline$$1.event.subscriptions;

    var matchIndex = void 0;

    for (var i = 0, l = subscriptions.length; i < l; i++) {
      if (subscriptions[i].token === token) {
        matchIndex = i;
      }
    }

    if (typeof matchIndex !== 'undefined') {
      timeline$$1.event.subscriptions.splice(matchIndex, 1);
      return true;
    }

    return false;
  };
};

var _slicedToArray$2 = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

// I extracted this from the a2c function from
// SVG path – https://github.com/fontello/svgpath
//
// All credit goes to:
//
// Sergey Batishchev – https://github.com/snb2013
// Vitaly Puzrin – https://github.com/puzrin
// Alex Kocharin – https://github.com/rlidwka

var TAU = Math.PI * 2;

var mapToEllipse = function mapToEllipse(_ref, rx, ry, cosphi, sinphi, centerx, centery) {
  var x = _ref.x,
      y = _ref.y;

  x *= rx;
  y *= ry;

  var xp = cosphi * x - sinphi * y;
  var yp = sinphi * x + cosphi * y;

  return {
    x: xp + centerx,
    y: yp + centery
  };
};

var approxUnitArc = function approxUnitArc(ang1, ang2) {
  var a = 4 / 3 * Math.tan(ang2 / 4);

  var x1 = Math.cos(ang1);
  var y1 = Math.sin(ang1);
  var x2 = Math.cos(ang1 + ang2);
  var y2 = Math.sin(ang1 + ang2);

  return [{
    x: x1 - y1 * a,
    y: y1 + x1 * a
  }, {
    x: x2 + y2 * a,
    y: y2 - x2 * a
  }, {
    x: x2,
    y: y2
  }];
};

var vectorAngle = function vectorAngle(ux, uy, vx, vy) {
  var sign = ux * vy - uy * vx < 0 ? -1 : 1;
  var umag = Math.sqrt(ux * ux + uy * uy);
  var vmag = Math.sqrt(ux * ux + uy * uy);
  var dot = ux * vx + uy * vy;

  var div = dot / (umag * vmag);

  if (div > 1) {
    div = 1;
  }

  if (div < -1) {
    div = -1;
  }

  return sign * Math.acos(div);
};

var getArcCenter = function getArcCenter(px, py, cx, cy, rx, ry, largeArcFlag, sweepFlag, sinphi, cosphi, pxp, pyp) {
  var rxsq = Math.pow(rx, 2);
  var rysq = Math.pow(ry, 2);
  var pxpsq = Math.pow(pxp, 2);
  var pypsq = Math.pow(pyp, 2);

  var radicant = rxsq * rysq - rxsq * pypsq - rysq * pxpsq;

  if (radicant < 0) {
    radicant = 0;
  }

  radicant /= rxsq * pypsq + rysq * pxpsq;
  radicant = Math.sqrt(radicant) * (largeArcFlag === sweepFlag ? -1 : 1);

  var centerxp = radicant * rx / ry * pyp;
  var centeryp = radicant * -ry / rx * pxp;

  var centerx = cosphi * centerxp - sinphi * centeryp + (px + cx) / 2;
  var centery = sinphi * centerxp + cosphi * centeryp + (py + cy) / 2;

  var vx1 = (pxp - centerxp) / rx;
  var vy1 = (pyp - centeryp) / ry;
  var vx2 = (-pxp - centerxp) / rx;
  var vy2 = (-pyp - centeryp) / ry;

  var ang1 = vectorAngle(1, 0, vx1, vy1);
  var ang2 = vectorAngle(vx1, vy1, vx2, vy2);

  if (sweepFlag === 0 && ang2 > 0) {
    ang2 -= TAU;
  }

  if (sweepFlag === 1 && ang2 < 0) {
    ang2 += TAU;
  }

  return [centerx, centery, ang1, ang2];
};

var arcToBezier = function arcToBezier(_ref2) {
  var px = _ref2.px,
      py = _ref2.py,
      cx = _ref2.cx,
      cy = _ref2.cy,
      rx = _ref2.rx,
      ry = _ref2.ry,
      _ref2$xAxisRotation = _ref2.xAxisRotation,
      xAxisRotation = _ref2$xAxisRotation === undefined ? 0 : _ref2$xAxisRotation,
      _ref2$largeArcFlag = _ref2.largeArcFlag,
      largeArcFlag = _ref2$largeArcFlag === undefined ? 0 : _ref2$largeArcFlag,
      _ref2$sweepFlag = _ref2.sweepFlag,
      sweepFlag = _ref2$sweepFlag === undefined ? 0 : _ref2$sweepFlag;

  var curves = [];

  if (rx === 0 || ry === 0) {
    return [];
  }

  var sinphi = Math.sin(xAxisRotation * TAU / 360);
  var cosphi = Math.cos(xAxisRotation * TAU / 360);

  var pxp = cosphi * (px - cx) / 2 + sinphi * (py - cy) / 2;
  var pyp = -sinphi * (px - cx) / 2 + cosphi * (py - cy) / 2;

  if (pxp === 0 && pyp === 0) {
    return [];
  }

  rx = Math.abs(rx);
  ry = Math.abs(ry);

  var lambda = Math.pow(pxp, 2) / Math.pow(rx, 2) + Math.pow(pyp, 2) / Math.pow(ry, 2);

  if (lambda > 1) {
    rx *= Math.sqrt(lambda);
    ry *= Math.sqrt(lambda);
  }

  var _getArcCenter = getArcCenter(px, py, cx, cy, rx, ry, largeArcFlag, sweepFlag, sinphi, cosphi, pxp, pyp),
      _getArcCenter2 = _slicedToArray$2(_getArcCenter, 4),
      centerx = _getArcCenter2[0],
      centery = _getArcCenter2[1],
      ang1 = _getArcCenter2[2],
      ang2 = _getArcCenter2[3];

  var segments = Math.max(Math.ceil(Math.abs(ang2) / (TAU / 4)), 1);

  ang2 /= segments;

  for (var i = 0; i < segments; i++) {
    curves.push(approxUnitArc(ang1, ang2));
    ang1 += ang2;
  }

  return curves.map(function (curve) {
    var _mapToEllipse = mapToEllipse(curve[0], rx, ry, cosphi, sinphi, centerx, centery),
        x1 = _mapToEllipse.x,
        y1 = _mapToEllipse.y;

    var _mapToEllipse2 = mapToEllipse(curve[1], rx, ry, cosphi, sinphi, centerx, centery),
        x2 = _mapToEllipse2.x,
        y2 = _mapToEllipse2.y;

    var _mapToEllipse3 = mapToEllipse(curve[2], rx, ry, cosphi, sinphi, centerx, centery),
        x = _mapToEllipse3.x,
        y = _mapToEllipse3.y;

    return { x1: x1, y1: y1, x2: x2, y2: y2, x: x, y: y };
  });
};

var angleFromSides = function angleFromSides(a, b, c) {
  var r = Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b));

  return r * (180 / Math.PI);
};

var applyFuncToShapes = function applyFuncToShapes(f, s) {
  for (var _len = arguments.length, args = Array(_len > 2 ? _len - 2 : 0), _key = 2; _key < _len; _key++) {
    args[_key - 2] = arguments[_key];
  }

  if (isShapeArray(s)) {
    return s.map(function (shape) {
      return f.apply(undefined, [shape].concat(args));
    });
  }

  return f.apply(undefined, [s].concat(args));
};

var getShapeArray = function getShapeArray(s) {
  return isShapeArray(s) ? s : [s];
};

var isShapeArray = function isShapeArray(s) {
  return Array.isArray(s[0]);
};

var numberAtInterval = function numberAtInterval(a, b, interval) {
  var c = a === b ? 0 : Math.abs(b - a);
  return c === 0 ? a : a < b ? a + c * interval : a - c * interval;
};

var cubifyShape = function cubifyShape(shape) {
  var s = [];

  for (var i = 0, l = shape.length; i < l; i++) {
    var point = shape[i];

    if (point.curve && point.curve.type !== 'cubic') {
      var _shape = shape[i - 1],
          px = _shape.x,
          py = _shape.y;
      var cx = point.x,
          cy = point.y;


      if (point.curve.type === 'arc') {
        var curves = arcToBezier({
          px: px,
          py: py,
          cx: cx,
          cy: cy,
          rx: point.curve.rx,
          ry: point.curve.ry,
          xAxisRotation: point.curve.xAxisRotation,
          largeArcFlag: point.curve.largeArcFlag,
          sweepFlag: point.curve.sweepFlag
        });

        curves.forEach(function (_ref) {
          var x1 = _ref.x1,
              y1 = _ref.y1,
              x2 = _ref.x2,
              y2 = _ref.y2,
              x = _ref.x,
              y = _ref.y;

          s.push({ x: x, y: y, curve: { type: 'cubic', x1: x1, y1: y1, x2: x2, y2: y2 } });
        });
      } else if (point.curve.type === 'quadratic') {
        var x1 = px + 2 / 3 * (point.curve.x1 - px);
        var y1 = py + 2 / 3 * (point.curve.y1 - py);
        var x2 = cx + 2 / 3 * (point.curve.x1 - cx);
        var y2 = cy + 2 / 3 * (point.curve.y1 - cy);

        s.push({ x: cx, y: cy, curve: { type: 'cubic', x1: x1, y1: y1, x2: x2, y2: y2 } });
      }
    } else {
      s.push(point);
    }
  }

  return s;
};

var cubify = function cubify(s) {
  return applyFuncToShapes(cubifyShape, s);
};

var _slicedToArray$3 = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

function _toConsumableArray(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

var linearPoints = function linearPoints(from, to) {
  return [{
    x: numberAtInterval(from.x, to.x, 0.5),
    y: numberAtInterval(from.y, to.y, 0.5)
  }, to];
};

var curvedPoints = function curvedPoints(from, to) {
  var _to$curve = to.curve,
      x1 = _to$curve.x1,
      y1 = _to$curve.y1,
      x2 = _to$curve.x2,
      y2 = _to$curve.y2;


  var A = { x: from.x, y: from.y };
  var B = { x: x1, y: y1 };
  var C = { x: x2, y: y2 };
  var D = { x: to.x, y: to.y };
  var E = { x: numberAtInterval(A.x, B.x, 0.5), y: numberAtInterval(A.y, B.y, 0.5) };
  var F = { x: numberAtInterval(B.x, C.x, 0.5), y: numberAtInterval(B.y, C.y, 0.5) };
  var G = { x: numberAtInterval(C.x, D.x, 0.5), y: numberAtInterval(C.y, D.y, 0.5) };
  var H = { x: numberAtInterval(E.x, F.x, 0.5), y: numberAtInterval(E.y, F.y, 0.5) };
  var J = { x: numberAtInterval(F.x, G.x, 0.5), y: numberAtInterval(F.y, G.y, 0.5) };
  var K = { x: numberAtInterval(H.x, J.x, 0.5), y: numberAtInterval(H.y, J.y, 0.5) };

  return [{ x: K.x, y: K.y, curve: { type: 'cubic', x1: E.x, y1: E.y, x2: H.x, y2: H.y } }, { x: D.x, y: D.y, curve: { type: 'cubic', x1: J.x, y1: J.y, x2: G.x, y2: G.y } }];
};

var points = function points(from, to) {
  return to.curve ? curvedPoints(from, to) : linearPoints(from, to);
};

var addPoints = function addPoints(shape, pointsRequired) {
  if (isNaN(pointsRequired)) {
    throw Error('`add` function must be passed a number as the second argument');
  }

  var nextShape = [].concat(_toConsumableArray(shape));

  for (var i = 1; i < nextShape.length;) {
    if (nextShape.length >= pointsRequired) {
      return nextShape;
    }

    var to = nextShape[i];

    if (to.moveTo) {
      i++;
    } else {
      var from = nextShape[i - 1];

      var _points = points(from, to),
          _points2 = _slicedToArray$3(_points, 2),
          midPoint = _points2[0],
          replacementPoint = _points2[1];

      nextShape.splice(i, 1, midPoint, replacementPoint);

      i += 2;
    }
  }

  return addPoints(nextShape, pointsRequired);
};

var add = function add(shape, pointsRequired) {
  return addPoints(cubify(shape), pointsRequired);
};

var length = function length(shape, accuracy) {
  var s = decurve(shape, accuracy);

  return s.reduce(function (currentLength, _ref, i) {
    var x2 = _ref.x,
        y2 = _ref.y,
        moveTo = _ref.moveTo;

    if (!moveTo) {
      var _s = s[i - 1],
          x1 = _s.x,
          y1 = _s.y;

      currentLength += linearLength(x1, y1, x2, y2);
    }

    return currentLength;
  }, 0);
};

var linearLength = function linearLength(x1, y1, x2, y2) {
  return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
};

var _slicedToArray$4 = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

function _toConsumableArray$1(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

var angle = function angle(triangle) {
  var _triangle$ = _slicedToArray$4(triangle[0], 2),
      ax = _triangle$[0],
      ay = _triangle$[1];

  var _triangle$2 = _slicedToArray$4(triangle[1], 2),
      bx = _triangle$2[0],
      by = _triangle$2[1];

  var _triangle$3 = _slicedToArray$4(triangle[2], 2),
      cx = _triangle$3[0],
      cy = _triangle$3[1];

  var a = linearLength(ax, ay, bx, by);
  var b = linearLength(bx, by, cx, cy);
  var c = linearLength(cx, cy, ax, ay);

  return angleFromSides(a, b, c);
};

var curved = function curved(shape) {
  return shape.reduce(function (c, _ref) {
    var curve = _ref.curve;
    return curve ? true : c;
  }, false);
};

var decurve = function decurve(shape) {
  var accuracy = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 1;

  if (!curved(shape)) {
    return shape;
  }

  var s = cubify(shape);
  var d = [];

  s.map(function (point, i) {
    if (point.curve) {
      var prevPoint = s[i - 1];
      straighten(prevPoint, point, accuracy).map(function (p) {
        return d.push(p);
      });
    } else {
      d.push(point);
    }
  });

  return d;
};

var straight = function straight(x1, y1, cx1, cy1, x2, y2, cx2, cy2, accuracy) {
  var t1 = [[cx1, cy1], [x2, y2], [x1, y1]];
  var t2 = [[cx2, cy2], [x1, y1], [x2, y2]];
  return angle(t1) < accuracy && angle(t2) < accuracy;
};

var straighten = function straighten(prevPoint, point, accuracy) {
  var x1 = prevPoint.x,
      y1 = prevPoint.y;
  var x2 = point.x,
      y2 = point.y,
      curve = point.curve;
  var cx1 = curve.x1,
      cy1 = curve.y1,
      cx2 = curve.x2,
      cy2 = curve.y2;


  if (straight(x1, y1, cx1, cy1, x2, y2, cx2, cy2, accuracy)) {
    return [point];
  }

  var _curvedPoints = curvedPoints(prevPoint, point),
      _curvedPoints2 = _slicedToArray$4(_curvedPoints, 2),
      midPoint = _curvedPoints2[0],
      lastPoint = _curvedPoints2[1];

  return [].concat(_toConsumableArray$1(straighten(prevPoint, midPoint, accuracy)), _toConsumableArray$1(straighten(midPoint, lastPoint, accuracy)));
};

var boundingBox = function boundingBox(s) {
  var bottom = void 0;
  var left = void 0;
  var right = void 0;
  var top = void 0;

  var shapes = getShapeArray(s);

  shapes.map(function (shape) {
    return decurve(shape).map(function (_ref) {
      var x = _ref.x,
          y = _ref.y;

      if (typeof bottom !== 'number' || y > bottom) {
        bottom = y;
      }

      if (typeof left !== 'number' || x < left) {
        left = x;
      }

      if (typeof right !== 'number' || x > right) {
        right = x;
      }

      if (typeof top !== 'number' || y < top) {
        top = y;
      }
    });
  });

  return {
    bottom: bottom,
    center: {
      x: left + (right - left) / 2,
      y: top + (bottom - top) / 2
    },
    left: left,
    right: right,
    top: top
  };
};

function _toConsumableArray$2(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

var countLinePoints = function countLinePoints(lines) {
  return lines.reduce(function (count, points) {
    return count + countPoints(points);
  }, 0);
};

var countPoints = function countPoints(points) {
  return points.length - (isJoined(points) ? 1 : 0);
};

var isJoined = function isJoined(points) {
  var firstPoint = points[0];
  var lastPoint = points[points.length - 1];
  return firstPoint.x === lastPoint.x && firstPoint.y === lastPoint.y;
};

var joinLines = function joinLines(lines) {
  return lines.reduce(function (shape, line) {
    return [].concat(_toConsumableArray$2(shape), _toConsumableArray$2(line));
  }, []);
};

var moveIndex = function moveIndex(s, offset) {
  return applyFuncToShapes(movePointsIndex, s, offset);
};

var movePointsIndex = function movePointsIndex(shape, offset) {
  var lines = splitLines(shape);
  var count = countLinePoints(lines);
  var normalisedOffset = (offset % count + count) % count;

  if (!normalisedOffset) {
    return shape;
  }

  var _nextIndex = nextIndex(lines, normalisedOffset),
      lineIndex = _nextIndex.lineIndex,
      pointIndex = _nextIndex.pointIndex;

  var reorderedLines = reorderLines(lines, lineIndex);
  var firstLine = reorderPoints(reorderedLines[0], pointIndex);
  var restOfLines = [].concat(_toConsumableArray$2(reorderedLines)).splice(1);

  return joinLines([firstLine].concat(_toConsumableArray$2(restOfLines)));
};

var nextIndex = function nextIndex(lines, offset) {
  for (var i = 0, l = lines.length; i < l; i++) {
    var count = countPoints(lines[i]);

    if (offset <= count - 1) {
      return {
        lineIndex: i,
        pointIndex: offset
      };
    }

    offset -= count;
  }
};

var reorderLines = function reorderLines(lines, offset) {
  return [].concat(_toConsumableArray$2(lines)).splice(offset).concat([].concat(_toConsumableArray$2(lines)).splice(0, offset));
};

var reorderPoints = function reorderPoints(points, offset) {
  if (!offset) {
    return points;
  }

  var nextPoints = [{ x: points[offset].x, y: points[offset].y, moveTo: true }].concat(_toConsumableArray$2([].concat(_toConsumableArray$2(points)).splice(offset + 1)));

  if (isJoined(points)) {
    return [].concat(_toConsumableArray$2(nextPoints), _toConsumableArray$2([].concat(_toConsumableArray$2(points)).splice(1, offset)));
  }

  return [].concat(_toConsumableArray$2(nextPoints), _toConsumableArray$2([].concat(_toConsumableArray$2(points)).splice(0, offset + 1)));
};

var splitLines = function splitLines(shape) {
  return shape.reduce(function (lines, point) {
    if (point.moveTo) {
      lines.push([]);
    }

    lines[lines.length - 1].push(point);

    return lines;
  }, []);
};

var _extends$2 = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var offsetPoints = function offsetPoints(shape, x, y) {
  return shape.map(function (point) {
    var p = _extends$2({}, point);

    p.x += x;
    p.y += y;

    if (p.curve) {
      p.curve = _extends$2({}, p.curve);

      if (p.curve.type === 'quadratic' || p.curve.type === 'cubic') {
        p.curve.x1 += x;
        p.curve.y1 += y;
      }

      if (p.curve.type === 'cubic') {
        p.curve.x2 += x;
        p.curve.y2 += y;
      }
    }

    return p;
  });
};

var offset = function offset(s) {
  var x = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;
  var y = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 0;
  return applyFuncToShapes(offsetPoints, s, x, y);
};

var angle$1 = function angle(x1, y1, x2, y2, a) {
  if (x1 === x2) {
    return y1 >= y2 ? 0 : 180;
  }

  var b = 100;
  var c = linearLength(x2, y2, x1, y1 - b);
  var ang = angleFromSides(a, b, c);

  return x1 < x2 ? ang : 360 - ang;
};

var over = function over(shape, length$$1, totalLength, desiredLength) {
  var _shape = shape[length$$1 - 2],
      x1 = _shape.x,
      y1 = _shape.y;
  var _shape2 = shape[length$$1 - 1],
      x2 = _shape2.x,
      y2 = _shape2.y;

  var segmentLength = linearLength(x1, y1, x2, y2);
  var segmentInterval = (desiredLength - totalLength) / segmentLength + 1;
  return { x1: x1, y1: y1, x2: x2, y2: y2, segmentInterval: segmentInterval, segmentLength: segmentLength };
};

var position$1 = function position(shape, interval, accuracy) {
  var s = decurve(shape, accuracy);
  var l = s.length;
  var t = length(s);
  var d = t * interval;

  var _ref = interval > 1 ? over(s, l, t, d) : interval < 0 ? under(s, d) : within(s, l, d),
      x1 = _ref.x1,
      y1 = _ref.y1,
      x2 = _ref.x2,
      y2 = _ref.y2,
      segmentInterval = _ref.segmentInterval,
      segmentLength = _ref.segmentLength;

  return {
    angle: angle$1(x1, y1, x2, y2, segmentLength),
    x: numberAtInterval(x1, x2, segmentInterval),
    y: numberAtInterval(y1, y2, segmentInterval)
  };
};

var under = function under(shape, desiredLength) {
  var _shape$ = shape[0],
      x1 = _shape$.x,
      y1 = _shape$.y;
  var _shape$2 = shape[1],
      x2 = _shape$2.x,
      y2 = _shape$2.y;

  var segmentLength = linearLength(x1, y1, x2, y2);
  var segmentInterval = desiredLength / segmentLength;
  return { x1: x1, y1: y1, x2: x2, y2: y2, segmentInterval: segmentInterval, segmentLength: segmentLength };
};

var within = function within(shape, length$$1, desiredLength) {
  var currentLength = 0;

  for (var i = 0; i < length$$1; i++) {
    var moveTo = shape[i].moveTo;


    if (!moveTo) {
      var _shape3 = shape[i - 1],
          x1 = _shape3.x,
          y1 = _shape3.y;
      var _shape$i = shape[i],
          x2 = _shape$i.x,
          y2 = _shape$i.y;


      var segmentLength = linearLength(x1, y1, x2, y2);

      if (currentLength + segmentLength >= desiredLength) {
        var segmentInterval = (desiredLength - currentLength) / segmentLength;
        return { x1: x1, y1: y1, x2: x2, y2: y2, segmentInterval: segmentInterval, segmentLength: segmentLength };
      }

      currentLength += segmentLength;
    }
  }
};

var isBetween = function isBetween(a, b, c) {
  if (b.curve || c.curve) {
    return false;
  }

  var crossProduct = (c.y - a.y) * (b.x - a.x) - (c.x - a.x) * (b.y - a.y);

  if (Math.abs(crossProduct) > Number.EPSILON) {
    return false;
  }

  var dotProduct = (c.x - a.x) * (b.x - a.x) + (c.y - a.y) * (b.y - a.y);

  if (dotProduct < 0) {
    return false;
  }

  var squaredLengthBA = (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);

  if (dotProduct > squaredLengthBA) {
    return false;
  }

  return true;
};

var removePoints = function removePoints(shape) {
  var s = [];

  for (var i = 0, l = shape.length; i < l; i++) {
    var a = s[s.length - 1];
    var b = shape[i + 1];
    var c = shape[i];

    if (!(a && b && c) || !isBetween(a, b, c)) {
      s.push(c);
    }
  }

  return s;
};

var remove = function remove(s) {
  return applyFuncToShapes(removePoints, s);
};

var reversePoints = function reversePoints(shape) {
  var m = void 0;
  var c = void 0;

  return shape.reverse().map(function (_ref, i) {
    var x = _ref.x,
        y = _ref.y,
        moveTo = _ref.moveTo,
        curve = _ref.curve;

    var point = { x: x, y: y };

    if (c) {
      var _c = c,
          x2 = _c.x1,
          y2 = _c.y1,
          x1 = _c.x2,
          y1 = _c.y2;

      point.curve = { type: 'cubic', x1: x1, y1: y1, x2: x2, y2: y2 };
    }

    if (i === 0 || m) {
      point.moveTo = true;
    }

    m = moveTo;
    c = curve || null;

    return point;
  });
};

var reverse = function reverse(s) {
  return applyFuncToShapes(reversePoints, cubify(s));
};

var _extends$3 = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _slicedToArray$5 = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

var rotatePoint = function rotatePoint(x, y, c, s, about) {
  var offsetX = about.x,
      offsetY = about.y;

  var relativeX = x - offsetX;
  var relativeY = y - offsetY;

  return [relativeX * c - relativeY * s + offsetX, relativeX * s + relativeY * c + offsetY];
};

var rotatePoints = function rotatePoints(shape, angle, about) {
  return shape.map(function (point) {
    var r = angle * Math.PI / 180;
    var c = Math.cos(r);
    var s = Math.sin(r);

    var _rotatePoint = rotatePoint(point.x, point.y, c, s, about),
        _rotatePoint2 = _slicedToArray$5(_rotatePoint, 2),
        x = _rotatePoint2[0],
        y = _rotatePoint2[1];

    var p = _extends$3({}, point, { x: x, y: y });

    if (p.curve) {
      if (p.curve.type === 'quadratic' || p.curve.type === 'cubic') {
        var _rotatePoint3 = rotatePoint(p.curve.x1, p.curve.y1, c, s, about),
            _rotatePoint4 = _slicedToArray$5(_rotatePoint3, 2),
            x1 = _rotatePoint4[0],
            y1 = _rotatePoint4[1];

        p.curve = _extends$3({}, p.curve, { x1: x1, y1: y1 });
      }

      if (p.curve.type === 'cubic') {
        var _rotatePoint5 = rotatePoint(p.curve.x2, p.curve.y2, c, s, about),
            _rotatePoint6 = _slicedToArray$5(_rotatePoint5, 2),
            x2 = _rotatePoint6[0],
            y2 = _rotatePoint6[1];

        p.curve = _extends$3({}, p.curve, { x2: x2, y2: y2 });
      }
    }

    return p;
  });
};

var rotate = function rotate(s, angle) {
  var _boundingBox = boundingBox(s),
      about = _boundingBox.center;

  return applyFuncToShapes(rotatePoints, s, angle, about);
};

var _extends$4 = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var scalePoint = function scalePoint(point, scaleFactor, anchorX, anchorY) {
  var p = _extends$4({}, point);

  p.x = anchorX - (anchorX - p.x) * scaleFactor;
  p.y = anchorY - (anchorY - p.y) * scaleFactor;

  if (point.curve) {
    p.curve = _extends$4({}, p.curve);

    if (p.curve.type === 'arc') {
      if (p.curve.rx) {
        p.curve.rx = p.curve.rx * scaleFactor;
      }

      if (p.curve.ry) {
        p.curve.ry = p.curve.ry * scaleFactor;
      }
    } else {
      p.curve.x1 = anchorX - (anchorX - p.curve.x1) * scaleFactor;
      p.curve.y1 = anchorY - (anchorY - p.curve.y1) * scaleFactor;

      if (p.curve.type === 'cubic') {
        p.curve.x2 = anchorX - (anchorX - p.curve.x2) * scaleFactor;
        p.curve.y2 = anchorY - (anchorY - p.curve.y2) * scaleFactor;
      }
    }
  }

  return p;
};

var scale = function scale(s, scaleFactor) {
  var anchor = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 'center';

  var _boundingBox = boundingBox(s),
      bottom = _boundingBox.bottom,
      center = _boundingBox.center,
      left = _boundingBox.left,
      right = _boundingBox.right,
      top = _boundingBox.top;

  var anchorX = center.x;
  var anchorY = center.y;

  switch (anchor) {
    case 'topLeft':
      anchorX = left;
      anchorY = top;
      break;
    case 'topRight':
      anchorX = right;
      anchorY = top;
      break;
    case 'bottomRight':
      anchorX = right;
      anchorY = bottom;
      break;
    case 'bottomLeft':
      anchorX = left;
      anchorY = bottom;
      break;
  }

  return applyFuncToShapes(function (shape) {
    return shape.map(function (point) {
      return scalePoint(point, scaleFactor, anchorX, anchorY);
    });
  }, s);
};



var transformFunctions = /*#__PURE__*/Object.freeze({
  add: add,
  boundingBox: boundingBox,
  cubify: cubify,
  length: length,
  moveIndex: moveIndex,
  offset: offset,
  position: position$1,
  remove: remove,
  reverse: reverse,
  rotate: rotate,
  scale: scale
});

var _extends$5 = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

var toPoints = function toPoints(_ref) {
  var type = _ref.type,
      props = _objectWithoutProperties(_ref, ['type']);

  switch (type) {
    case 'circle':
      return getPointsFromCircle(props);
    case 'ellipse':
      return getPointsFromEllipse(props);
    case 'line':
      return getPointsFromLine(props);
    case 'path':
      return getPointsFromPath(props);
    case 'polygon':
      return getPointsFromPolygon(props);
    case 'polyline':
      return getPointsFromPolyline(props);
    case 'rect':
      return getPointsFromRect(props);
    case 'g':
      return getPointsFromG(props);
    default:
      throw new Error('Not a valid shape type');
  }
};

var getPointsFromCircle = function getPointsFromCircle(_ref2) {
  var cx = _ref2.cx,
      cy = _ref2.cy,
      r = _ref2.r;

  return [{ x: cx, y: cy - r, moveTo: true }, { x: cx, y: cy + r, curve: { type: 'arc', rx: r, ry: r, sweepFlag: 1 } }, { x: cx, y: cy - r, curve: { type: 'arc', rx: r, ry: r, sweepFlag: 1 } }];
};

var getPointsFromEllipse = function getPointsFromEllipse(_ref3) {
  var cx = _ref3.cx,
      cy = _ref3.cy,
      rx = _ref3.rx,
      ry = _ref3.ry;

  return [{ x: cx, y: cy - ry, moveTo: true }, { x: cx, y: cy + ry, curve: { type: 'arc', rx: rx, ry: ry, sweepFlag: 1 } }, { x: cx, y: cy - ry, curve: { type: 'arc', rx: rx, ry: ry, sweepFlag: 1 } }];
};

var getPointsFromLine = function getPointsFromLine(_ref4) {
  var x1 = _ref4.x1,
      x2 = _ref4.x2,
      y1 = _ref4.y1,
      y2 = _ref4.y2;

  return [{ x: x1, y: y1, moveTo: true }, { x: x2, y: y2 }];
};

var validCommands = /[MmLlHhVvCcSsQqTtAaZz]/g;

var commandLengths = {
  A: 7,
  C: 6,
  H: 1,
  L: 2,
  M: 2,
  Q: 4,
  S: 4,
  T: 2,
  V: 1,
  Z: 0
};

var relativeCommands = ['a', 'c', 'h', 'l', 'm', 'q', 's', 't', 'v'];

var isRelative = function isRelative(command) {
  return relativeCommands.indexOf(command) !== -1;
};

var optionalArcKeys = ['xAxisRotation', 'largeArcFlag', 'sweepFlag'];

var getCommands = function getCommands(d) {
  return d.match(validCommands);
};

var getParams = function getParams(d) {
  return d.split(validCommands).map(function (v) {
    return v.replace(/[0-9]+-/g, function (m) {
      return m.slice(0, -1) + ' -';
    });
  }).map(function (v) {
    return v.replace(/\.[0-9]+/g, function (m) {
      return m + ' ';
    });
  }).map(function (v) {
    return v.trim();
  }).filter(function (v) {
    return v.length > 0;
  }).map(function (v) {
    return v.split(/[ ,]+/).map(parseFloat).filter(function (n) {
      return !isNaN(n);
    });
  });
};

var getPointsFromPath = function getPointsFromPath(_ref5) {
  var d = _ref5.d;

  var commands = getCommands(d);
  var params = getParams(d);

  var points = [];

  var moveTo = void 0;

  for (var i = 0, l = commands.length; i < l; i++) {
    var command = commands[i];
    var upperCaseCommand = command.toUpperCase();
    var commandLength = commandLengths[upperCaseCommand];
    var relative = isRelative(command);

    if (commandLength > 0) {
      var commandParams = params.shift();
      var iterations = commandParams.length / commandLength;

      for (var j = 0; j < iterations; j++) {
        var prevPoint = points[points.length - 1] || { x: 0, y: 0 };

        switch (upperCaseCommand) {
          case 'M':
            var x = (relative ? prevPoint.x : 0) + commandParams.shift();
            var y = (relative ? prevPoint.y : 0) + commandParams.shift();

            if (j === 0) {
              moveTo = { x: x, y: y };
              points.push({ x: x, y: y, moveTo: true });
            } else {
              points.push({ x: x, y: y });
            }

            break;

          case 'L':
            points.push({
              x: (relative ? prevPoint.x : 0) + commandParams.shift(),
              y: (relative ? prevPoint.y : 0) + commandParams.shift()
            });

            break;

          case 'H':
            points.push({
              x: (relative ? prevPoint.x : 0) + commandParams.shift(),
              y: prevPoint.y
            });

            break;

          case 'V':
            points.push({
              x: prevPoint.x,
              y: (relative ? prevPoint.y : 0) + commandParams.shift()
            });

            break;

          case 'A':
            points.push({
              curve: {
                type: 'arc',
                rx: commandParams.shift(),
                ry: commandParams.shift(),
                xAxisRotation: commandParams.shift(),
                largeArcFlag: commandParams.shift(),
                sweepFlag: commandParams.shift()
              },
              x: (relative ? prevPoint.x : 0) + commandParams.shift(),
              y: (relative ? prevPoint.y : 0) + commandParams.shift()
            });

            var _iteratorNormalCompletion = true;
            var _didIteratorError = false;
            var _iteratorError = undefined;

            try {
              for (var _iterator = optionalArcKeys[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
                var k = _step.value;

                if (points[points.length - 1]['curve'][k] === 0) {
                  delete points[points.length - 1]['curve'][k];
                }
              }
            } catch (err) {
              _didIteratorError = true;
              _iteratorError = err;
            } finally {
              try {
                if (!_iteratorNormalCompletion && _iterator.return) {
                  _iterator.return();
                }
              } finally {
                if (_didIteratorError) {
                  throw _iteratorError;
                }
              }
            }

            break;

          case 'C':
            points.push({
              curve: {
                type: 'cubic',
                x1: (relative ? prevPoint.x : 0) + commandParams.shift(),
                y1: (relative ? prevPoint.y : 0) + commandParams.shift(),
                x2: (relative ? prevPoint.x : 0) + commandParams.shift(),
                y2: (relative ? prevPoint.y : 0) + commandParams.shift()
              },
              x: (relative ? prevPoint.x : 0) + commandParams.shift(),
              y: (relative ? prevPoint.y : 0) + commandParams.shift()
            });

            break;

          case 'S':
            var sx2 = (relative ? prevPoint.x : 0) + commandParams.shift();
            var sy2 = (relative ? prevPoint.y : 0) + commandParams.shift();
            var sx = (relative ? prevPoint.x : 0) + commandParams.shift();
            var sy = (relative ? prevPoint.y : 0) + commandParams.shift();

            var diff = {};

            var sx1 = void 0;
            var sy1 = void 0;

            if (prevPoint.curve && prevPoint.curve.type === 'cubic') {
              diff.x = Math.abs(prevPoint.x - prevPoint.curve.x2);
              diff.y = Math.abs(prevPoint.y - prevPoint.curve.y2);
              sx1 = prevPoint.x < prevPoint.curve.x2 ? prevPoint.x - diff.x : prevPoint.x + diff.x;
              sy1 = prevPoint.y < prevPoint.curve.y2 ? prevPoint.y - diff.y : prevPoint.y + diff.y;
            } else {
              diff.x = Math.abs(sx - sx2);
              diff.y = Math.abs(sy - sy2);
              sx1 = prevPoint.x;
              sy1 = prevPoint.y;
            }

            points.push({ curve: { type: 'cubic', x1: sx1, y1: sy1, x2: sx2, y2: sy2 }, x: sx, y: sy });

            break;

          case 'Q':
            points.push({
              curve: {
                type: 'quadratic',
                x1: (relative ? prevPoint.x : 0) + commandParams.shift(),
                y1: (relative ? prevPoint.y : 0) + commandParams.shift()
              },
              x: (relative ? prevPoint.x : 0) + commandParams.shift(),
              y: (relative ? prevPoint.y : 0) + commandParams.shift()
            });

            break;

          case 'T':
            var tx = (relative ? prevPoint.x : 0) + commandParams.shift();
            var ty = (relative ? prevPoint.y : 0) + commandParams.shift();

            var tx1 = void 0;
            var ty1 = void 0;

            if (prevPoint.curve && prevPoint.curve.type === 'quadratic') {
              var _diff = {
                x: Math.abs(prevPoint.x - prevPoint.curve.x1),
                y: Math.abs(prevPoint.y - prevPoint.curve.y1)
              };

              tx1 = prevPoint.x < prevPoint.curve.x1 ? prevPoint.x - _diff.x : prevPoint.x + _diff.x;
              ty1 = prevPoint.y < prevPoint.curve.y1 ? prevPoint.y - _diff.y : prevPoint.y + _diff.y;
            } else {
              tx1 = prevPoint.x;
              ty1 = prevPoint.y;
            }

            points.push({ curve: { type: 'quadratic', x1: tx1, y1: ty1 }, x: tx, y: ty });

            break;
        }
      }
    } else {
      var _prevPoint = points[points.length - 1] || { x: 0, y: 0 };

      if (_prevPoint.x !== moveTo.x || _prevPoint.y !== moveTo.y) {
        points.push({ x: moveTo.x, y: moveTo.y });
      }
    }
  }

  return points;
};

var getPointsFromPolygon = function getPointsFromPolygon(_ref6) {
  var points = _ref6.points;

  return getPointsFromPoints({ closed: true, points: points });
};

var getPointsFromPolyline = function getPointsFromPolyline(_ref7) {
  var points = _ref7.points;

  return getPointsFromPoints({ closed: false, points: points });
};

var getPointsFromPoints = function getPointsFromPoints(_ref8) {
  var closed = _ref8.closed,
      points = _ref8.points;

  var numbers = points.split(/[\s,]+/).map(function (n) {
    return parseFloat(n);
  });

  var p = numbers.reduce(function (arr, point, i) {
    if (i % 2 === 0) {
      arr.push({ x: point });
    } else {
      arr[(i - 1) / 2].y = point;
    }

    return arr;
  }, []);

  if (closed) {
    p.push(_extends$5({}, p[0]));
  }

  p[0].moveTo = true;

  return p;
};

var getPointsFromRect = function getPointsFromRect(_ref9) {
  var height = _ref9.height,
      rx = _ref9.rx,
      ry = _ref9.ry,
      width = _ref9.width,
      x = _ref9.x,
      y = _ref9.y;

  if (rx || ry) {
    return getPointsFromRectWithCornerRadius({
      height: height,
      rx: rx || ry,
      ry: ry || rx,
      width: width,
      x: x,
      y: y
    });
  }

  return getPointsFromBasicRect({ height: height, width: width, x: x, y: y });
};

var getPointsFromBasicRect = function getPointsFromBasicRect(_ref10) {
  var height = _ref10.height,
      width = _ref10.width,
      x = _ref10.x,
      y = _ref10.y;

  return [{ x: x, y: y, moveTo: true }, { x: x + width, y: y }, { x: x + width, y: y + height }, { x: x, y: y + height }, { x: x, y: y }];
};

var getPointsFromRectWithCornerRadius = function getPointsFromRectWithCornerRadius(_ref11) {
  var height = _ref11.height,
      rx = _ref11.rx,
      ry = _ref11.ry,
      width = _ref11.width,
      x = _ref11.x,
      y = _ref11.y;

  var curve = { type: 'arc', rx: rx, ry: ry, sweepFlag: 1 };

  return [{ x: x + rx, y: y, moveTo: true }, { x: x + width - rx, y: y }, { x: x + width, y: y + ry, curve: curve }, { x: x + width, y: y + height - ry }, { x: x + width - rx, y: y + height, curve: curve }, { x: x + rx, y: y + height }, { x: x, y: y + height - ry, curve: curve }, { x: x, y: y + ry }, { x: x + rx, y: y, curve: curve }];
};

var getPointsFromG = function getPointsFromG(_ref12) {
  var shapes = _ref12.shapes;
  return shapes.map(function (s) {
    return toPoints(s);
  });
};

var pointsToD = function pointsToD(p) {
  var d = '';
  var i = 0;
  var firstPoint = void 0;

  var _iteratorNormalCompletion = true;
  var _didIteratorError = false;
  var _iteratorError = undefined;

  try {
    for (var _iterator = p[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
      var point = _step.value;
      var _point$curve = point.curve,
          curve = _point$curve === undefined ? false : _point$curve,
          moveTo = point.moveTo,
          x = point.x,
          y = point.y;

      var isFirstPoint = i === 0 || moveTo;
      var isLastPoint = i === p.length - 1 || p[i + 1].moveTo;
      var prevPoint = i === 0 ? null : p[i - 1];

      if (isFirstPoint) {
        firstPoint = point;

        if (!isLastPoint) {
          d += 'M' + x + ',' + y;
        }
      } else if (curve) {
        switch (curve.type) {
          case 'arc':
            var _point$curve2 = point.curve,
                _point$curve2$largeAr = _point$curve2.largeArcFlag,
                largeArcFlag = _point$curve2$largeAr === undefined ? 0 : _point$curve2$largeAr,
                rx = _point$curve2.rx,
                ry = _point$curve2.ry,
                _point$curve2$sweepFl = _point$curve2.sweepFlag,
                sweepFlag = _point$curve2$sweepFl === undefined ? 0 : _point$curve2$sweepFl,
                _point$curve2$xAxisRo = _point$curve2.xAxisRotation,
                xAxisRotation = _point$curve2$xAxisRo === undefined ? 0 : _point$curve2$xAxisRo;

            d += 'A' + rx + ',' + ry + ',' + xAxisRotation + ',' + largeArcFlag + ',' + sweepFlag + ',' + x + ',' + y;
            break;
          case 'cubic':
            var _point$curve3 = point.curve,
                cx1 = _point$curve3.x1,
                cy1 = _point$curve3.y1,
                cx2 = _point$curve3.x2,
                cy2 = _point$curve3.y2;

            d += 'C' + cx1 + ',' + cy1 + ',' + cx2 + ',' + cy2 + ',' + x + ',' + y;
            break;
          case 'quadratic':
            var _point$curve4 = point.curve,
                qx1 = _point$curve4.x1,
                qy1 = _point$curve4.y1;

            d += 'Q' + qx1 + ',' + qy1 + ',' + x + ',' + y;
            break;
        }

        if (isLastPoint && x === firstPoint.x && y === firstPoint.y) {
          d += 'Z';
        }
      } else if (isLastPoint && x === firstPoint.x && y === firstPoint.y) {
        d += 'Z';
      } else if (x !== prevPoint.x && y !== prevPoint.y) {
        d += 'L' + x + ',' + y;
      } else if (x !== prevPoint.x) {
        d += 'H' + x;
      } else if (y !== prevPoint.y) {
        d += 'V' + y;
      }

      i++;
    }
  } catch (err) {
    _didIteratorError = true;
    _iteratorError = err;
  } finally {
    try {
      if (!_iteratorNormalCompletion && _iterator.return) {
        _iterator.return();
      }
    } finally {
      if (_didIteratorError) {
        throw _iteratorError;
      }
    }
  }

  return d;
};

var toPath = function toPath(s) {
  var isPoints = Array.isArray(s);
  var isGroup = isPoints ? Array.isArray(s[0]) : s.type === 'g';
  var points = isPoints ? s : isGroup ? s.shapes.map(function (shp) {
    return toPoints(shp);
  }) : toPoints(s);

  if (isGroup) {
    return points.map(function (p) {
      return pointsToD(p);
    });
  }

  return pointsToD(points);
};

var _typeof$5 = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var getErrors = function getErrors(shape) {
  var rules = getRules(shape);
  var errors = [];

  rules.map(function (_ref) {
    var match = _ref.match,
        prop = _ref.prop,
        required = _ref.required,
        type = _ref.type;

    if (typeof shape[prop] === 'undefined') {
      if (required) {
        errors.push(prop + ' prop is required' + (prop === 'type' ? '' : ' on a ' + shape.type));
      }
    } else {
      if (typeof type !== 'undefined') {
        if (type === 'array') {
          if (!Array.isArray(shape[prop])) {
            errors.push(prop + ' prop must be of type array');
          }
        } else if (_typeof$5(shape[prop]) !== type) {
          // eslint-disable-line valid-typeof
          errors.push(prop + ' prop must be of type ' + type);
        }
      }

      if (Array.isArray(match)) {
        if (match.indexOf(shape[prop]) === -1) {
          errors.push(prop + ' prop must be one of ' + match.join(', '));
        }
      }
    }
  });

  if (shape.type === 'g' && Array.isArray(shape.shapes)) {
    var childErrors = shape.shapes.map(function (s) {
      return getErrors(s);
    });
    return [].concat.apply(errors, childErrors);
  }

  return errors;
};

var getRules = function getRules(shape) {
  var rules = [{
    match: ['circle', 'ellipse', 'line', 'path', 'polygon', 'polyline', 'rect', 'g'],
    prop: 'type',
    required: true,
    type: 'string'
  }];

  switch (shape.type) {
    case 'circle':
      rules.push({ prop: 'cx', required: true, type: 'number' });
      rules.push({ prop: 'cy', required: true, type: 'number' });
      rules.push({ prop: 'r', required: true, type: 'number' });
      break;

    case 'ellipse':
      rules.push({ prop: 'cx', required: true, type: 'number' });
      rules.push({ prop: 'cy', required: true, type: 'number' });
      rules.push({ prop: 'rx', required: true, type: 'number' });
      rules.push({ prop: 'ry', required: true, type: 'number' });
      break;

    case 'line':
      rules.push({ prop: 'x1', required: true, type: 'number' });
      rules.push({ prop: 'x2', required: true, type: 'number' });
      rules.push({ prop: 'y1', required: true, type: 'number' });
      rules.push({ prop: 'y2', required: true, type: 'number' });
      break;

    case 'path':
      rules.push({ prop: 'd', required: true, type: 'string' });
      break;

    case 'polygon':
    case 'polyline':
      rules.push({ prop: 'points', required: true, type: 'string' });
      break;

    case 'rect':
      rules.push({ prop: 'height', required: true, type: 'number' });
      rules.push({ prop: 'rx', type: 'number' });
      rules.push({ prop: 'ry', type: 'number' });
      rules.push({ prop: 'width', required: true, type: 'number' });
      rules.push({ prop: 'x', required: true, type: 'number' });
      rules.push({ prop: 'y', required: true, type: 'number' });
      break;

    case 'g':
      rules.push({ prop: 'shapes', required: true, type: 'array' });
      break;
  }

  return rules;
};

var valid = function valid(shape) {
  var errors = getErrors(shape);

  return {
    errors: errors,
    valid: errors.length === 0
  };
};

var _typeof$6 = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var _extends$6 = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

function _toConsumableArray$3(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

function _objectWithoutProperties$1(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

/**
 * Shape data as specified by the
 * {@link https://github.com/colinmeinke/points Points spec}.
 *
 * @typedef {Object[]} Points
 */

/**
 * The data required to render a shape.
 *
 * @typedef {Object} FrameShape
 *
 * @property {Points} points
 * @property {Object} attributes
 * @property {FrameShape[]} childFrameShapes
 */

/**
 * A FrameShape array.
 *
 * @typedef {FrameShape[]} Frame
 */

/**
 * A number between 0 and 1 (inclusive).
 *
 * @typedef {number} Position
 */

/**
 * The structure of FrameShape Points.
 * An array represents a shape. A number represents a line.
 * An array that has nested arrays represents a group of shapes.
 *
 * @typedef {(number|number[])[]} PointStructure
 */

/**
 * The curve structure of FrameShape Points.
 * A boolean represents a point, and designates if the point is a curve.
 *
 * @typedef {(boolean|boolean[])[]} CurveStructure
 */

/**
 * Converts FrameShape Points to curves based on a CurveStructure.
 *
 * @param {FrameShape} frameShape
 * @param {CurveStructure} structure
 *
 * @returns {FrameShape}
 *
 * @example
 * applyCurveStructure(frameShape, stucture)
 */
var applyCurveStructure = function applyCurveStructure(frameShape, structure) {
  var points = frameShape.points;
  var childFrameShapes = frameShape.childFrameShapes;

  if (childFrameShapes) {
    var nextChildFrameShapes = [];

    for (var i = 0, l = childFrameShapes.length; i < l; i++) {
      nextChildFrameShapes.push(applyCurveStructure(childFrameShapes[i], structure[i]));
    }

    frameShape.childFrameShapes = nextChildFrameShapes;
  } else {
    var curves = false;

    for (var _i2 = 0, _l2 = structure.length; _i2 < _l2; _i2++) {
      if (structure[_i2]) {
        curves = true;
        break;
      }
    }

    if (curves) {
      var nextPoints = [];
      var cubifiedPoints = cubify(points);

      for (var _i3 = 0, _l3 = cubifiedPoints.length; _i3 < _l3; _i3++) {
        var point = cubifiedPoints[_i3];

        if (structure[_i3] && !point.curve) {
          nextPoints.push(_extends$6({}, point, {
            curve: {
              type: 'cubic',
              x1: points[_i3 - 1].x,
              y1: points[_i3 - 1].y,
              x2: points[_i3].x,
              y2: points[_i3].y
            }
          }));
        } else {
          nextPoints.push(point);
        }
      }

      frameShape.points = nextPoints;
    }
  }

  return frameShape;
};

/**
 * Restructures a FrameShape's Points based on a PointStructure.
 *
 * @param {FrameShape} frameShape
 * @param {PointStructure} structure
 *
 * @returns {FrameShape}
 *
 * @example
 * applyPointStructure(frameShape, stucture)
 */
var applyPointStructure = function applyPointStructure(frameShape, structure) {
  if (Array.isArray(structure[0])) {
    if (!frameShape.childFrameShapes) {
      frameShape.childFrameShapes = [clone(frameShape)];
      delete frameShape.points;
    }

    if (frameShape.childFrameShapes.length !== structure.length) {
      for (var i = 0, l = structure.length; i < l; i++) {
        if (i >= frameShape.childFrameShapes.length) {
          var previous = frameShape.childFrameShapes[i - 1].points;

          frameShape.childFrameShapes.push({
            attributes: clone(frameShape.attributes),
            points: [_extends$6({}, clone(previous[previous.length - 1]), { moveTo: true }), clone(previous[previous.length - 1])]
          });
        }
      }
    }

    var nextChildFrameShapes = [];

    for (var _i4 = 0, _l4 = frameShape.childFrameShapes.length; _i4 < _l4; _i4++) {
      nextChildFrameShapes.push(applyPointStructure(frameShape.childFrameShapes[_i4], structure[_i4]));
    }

    frameShape.childFrameShapes = nextChildFrameShapes;
  } else {
    var lines = splitLines$1(frameShape.points);

    for (var _i5 = 0, _l5 = structure.length; _i5 < _l5; _i5++) {
      var desiredPoints = structure[_i5];

      if (!lines[_i5]) {
        var previousLine = lines[_i5 - 1];

        lines[_i5] = [_extends$6({}, clone(previousLine[previousLine.length - 1]), { moveTo: true }), clone(previousLine[previousLine.length - 1])];
      }

      if (desiredPoints > lines[_i5].length) {
        lines[_i5] = add(lines[_i5], desiredPoints);
      }
    }

    frameShape.points = joinLines$1(lines);
  }

  return frameShape;
};

/**
 * Add a value to a PointStucture at a defined position.
 *
 * @param {PointStructure} structure
 * @param {(number|number[])} value - Value to add to PointStructure.
 * @param {number} i - Position to add value at.
 *
 * @example
 * addToPointStructure([], 9, 0)
 */
var addToPointStructure = function addToPointStructure(structure, value, i) {
  if (Array.isArray(value)) {
    if (!Array.isArray(structure[i])) {
      structure[i] = [structure[i]];
    }

    for (var _i = 0, l = value.length; _i < l; _i++) {
      structure[i] = addToPointStructure(structure[i], value[_i], _i);
    }
  } else {
    if (Array.isArray(structure[i])) {
      addToPointStructure(structure[i], value, 0);
    } else {
      structure[i] = Math.max(structure[i] || 0, value);
    }
  }

  return structure;
};

/**
 * Creates a common CurveStructure from an array of CurveStructures.
 *
 * @param {CurveStructure[]} structures
 *
 * @returns {CurveStructure}
 *
 * @example
 * commonCurveStructure(structures)
 */
var commonCurveStructure = function commonCurveStructure(structures) {
  var structure = structures[0];

  for (var i = 1, l = structures.length; i < l; i++) {
    var s = structures[i];
    var c = [];

    for (var _i = 0, _l = structure.length; _i < _l; _i++) {
      var x = structure[_i];

      if (Array.isArray(x)) {
        c.push(commonCurveStructure([x, s[_i]]));
      } else {
        c.push(x || s[_i]);
      }
    }

    structure = c;
  }

  return structure;
};

/**
 * Creates a common PointStructure from an array of PointStructures.
 *
 * @param {PointStructure[]} structures
 *
 * @returns {PointStructure}
 *
 * @example
 * commonPointStructure(structures)
 */
var commonPointStructure = function commonPointStructure(structures) {
  var structure = [];

  for (var i = 0, l = structures.length; i < l; i++) {
    var s = structures[i];

    for (var _i = 0, _l = s.length; _i < _l; _i++) {
      structure = addToPointStructure(structure, s[_i], _i);
    }
  }

  return structure;
};

/**
 * The current Frame of a Timeline.
 *
 * @param {Timeline} timeline
 * @param {number} [at]
 *
 * @returns {Frame}
 *
 * @example
 * frame(timeline)
 */
var frame = function frame(timeline$$1, at) {
  if ((typeof timeline$$1 === 'undefined' ? 'undefined' : _typeof$6(timeline$$1)) !== 'object' || !timeline$$1.timelineShapes || !timeline$$1.playbackOptions) {
    throw new TypeError('The frame function\'s first argument must be a Timeline');
  }

  if (typeof at !== 'undefined' && typeof at !== 'number') {
    throw new TypeError('The frame function\'s second argument must be of type number');
  }

  updateState(timeline$$1, typeof at !== 'undefined' ? at : Date.now());

  var frameShapes = [];
  var timelineShapes = timeline$$1.timelineShapes;

  for (var i = 0, l = timelineShapes.length; i < l; i++) {
    var timelineShape = timelineShapes[i];
    var shape = timelineShape.shape;
    var keyframes = shape.keyframes;
    var timelinePosition = timelineShape.timelinePosition;
    var start = timelinePosition.start;
    var finish = timelinePosition.finish;
    var position$$1 = timeline$$1.state.position;

    if (position$$1 <= start) {
      frameShapes.push(output$2(keyframes[0].frameShape, timeline$$1.middleware));
    } else if (position$$1 >= finish) {
      frameShapes.push(output$2(keyframes[keyframes.length - 1].frameShape, timeline$$1.middleware));
    } else {
      var shapePosition = (position$$1 - start) / (finish - start);
      frameShapes.push(frameShapeFromShape(shape, shapePosition, timeline$$1.middleware));
    }
  }

  return frameShapes;
};

/**
 * Creates a FrameShape from a PlainShapeObject.
 *
 * @param {PlainShapeObject} plainShapeObject
 *
 * @returns {FrameShape}
 *
 * @example
 * frameShapeFromPlainShapeObject(circle)
 */
var frameShapeFromPlainShapeObject = function frameShapeFromPlainShapeObject(_ref) {
  var childPlainShapeObjects = _ref.shapes,
      plainShapeObject = _objectWithoutProperties$1(_ref, ['shapes']);

  var attributes = _objectWithoutProperties$1(plainShapeObject, ['type', 'height', 'width', 'x', 'y', 'cx', 'cy', 'r', 'rx', 'ry', 'x1', 'x2', 'y1', 'y2', 'd', 'points', 'shapes']);

  if (plainShapeObject.type === 'g' && childPlainShapeObjects) {
    var childFrameShapes = [];

    for (var i = 0, l = childPlainShapeObjects.length; i < l; i++) {
      childFrameShapes.push(frameShapeFromPlainShapeObject(childPlainShapeObjects[i]));
    }

    return { attributes: attributes, childFrameShapes: childFrameShapes };
  }

  return {
    attributes: attributes,
    points: toPoints(plainShapeObject)
  };
};

/**
 * Creates a FrameShape from a Shape given the Position.
 *
 * @param {Shape} shape
 * @param {Position} position
 * @param {Middleware[]} middleware
 *
 * @returns {FrameShape}
 *
 * @example
 * frameShapeFromShape(shape, 0.75, [])
 */
var frameShapeFromShape = function frameShapeFromShape(shape, position$$1, middleware) {
  var keyframes = shape.keyframes;


  var fromIndex = 0;

  for (var i = 0, l = keyframes.length; i < l; i++) {
    if (position$$1 > keyframes[i].position) {
      fromIndex = i;
    }
  }

  var toIndex = fromIndex + 1;

  var from = keyframes[fromIndex];
  var to = keyframes[toIndex];
  var keyframePosition = (position$$1 - from.position) / (to.position - from.position);
  var forces = to.tween.forces;

  var frameShape = tween(from.frameShape, to.frameShape, to.tween.easing, keyframePosition);

  for (var _i6 = 0, _l6 = forces.length; _i6 < _l6; _i6++) {
    frameShape = forces[_i6](frameShape, keyframePosition);
  }

  return output$2(frameShape, middleware);
};

/**
 * Joins an array of Points into Points.
 *
 * @param {Points[]} lines
 *
 * @returns {Points}
 *
 * @example
 * joinLines([ shape1, shape2 ])
 */
var joinLines$1 = function joinLines(lines) {
  var _ref2;

  return (_ref2 = []).concat.apply(_ref2, _toConsumableArray$3(lines));
};

/**
 * Creates a CurveStructure from a FrameShape.
 *
 * @param {FrameShape} frameShape
 *
 * @returns {CurveStructure}
 *
 * @example
 * curveStructure(frameShape)
 */
var curveStructure = function curveStructure(_ref3) {
  var points = _ref3.points,
      childFrameShapes = _ref3.childFrameShapes;

  var s = [];

  if (childFrameShapes) {
    for (var i = 0, l = childFrameShapes.length; i < l; i++) {
      s.push(curveStructure(childFrameShapes[i]));
    }
  } else {
    for (var _i7 = 0, _l7 = points.length; _i7 < _l7; _i7++) {
      s.push(typeof points[_i7].curve !== 'undefined');
    }
  }

  return s;
};

/**
 * Creates a PointStructure from a FrameShape.
 *
 * @param {FrameShape} frameShape
 *
 * @returns {PointStructure}
 *
 * @example
 * pointStructure(frameShape)
 */
var pointStructure = function pointStructure(_ref4) {
  var points = _ref4.points,
      childFrameShapes = _ref4.childFrameShapes;

  if (childFrameShapes) {
    var s = [];

    for (var i = 0, l = childFrameShapes.length; i < l; i++) {
      s.push(pointStructure(childFrameShapes[i]));
    }

    return s;
  }

  var structure = [];

  for (var _i8 = 0, _l8 = points.length; _i8 < _l8; _i8++) {
    if (points[_i8].moveTo) {
      structure.push(1);
    } else {
      structure[structure.length - 1]++;
    }
  }

  return structure;
};

/**
 * Splits Points at moveTo commands.
 *
 * @param {Points} points
 *
 * @return {Points[]}
 *
 * @example
 * splitLines(points)
 */
var splitLines$1 = function splitLines(points) {
  var lines = [];

  for (var i = 0, l = points.length; i < l; i++) {
    var point = points[i];

    if (point.moveTo) {
      lines.push([point]);
    } else {
      lines[lines.length - 1].push(point);
    }
  }

  return lines;
};

/**
 * Tween between any two values.
 *
 * @param {*} from
 * @param {*} to - An identicle structure to the from param
 * @param {function} easing - The easing function to apply
 * @param {Position} position
 *
 * @returns {*}
 *
 * @example
 * tween(0, 100, easeOut, 0.75)
 */
var tween = function tween(from, to, easing, position$$1) {
  if (typeof from === 'number') {
    if (typeof to !== 'number') {
      throw new TypeError('The tween function\'s from and to arguments must be of an identicle structure');
    }

    if (from === to) {
      return from;
    }

    return easing(position$$1, from, to, 1);
  } else if (Array.isArray(from)) {
    if (!Array.isArray(to)) {
      throw new TypeError('The tween function\'s from and to arguments must be of an identicle structure');
    }

    var arr = [];

    for (var i = 0, l = from.length; i < l; i++) {
      arr.push(tween(from[i], to[i], easing, position$$1));
    }

    return arr;
  } else if (from !== null && (typeof from === 'undefined' ? 'undefined' : _typeof$6(from)) === 'object') {
    if (to !== null && (typeof to === 'undefined' ? 'undefined' : _typeof$6(to)) !== 'object') {
      throw new TypeError('The tween function\'s from and to arguments must be of an identicle structure');
    }

    var obj = {};

    for (var k in from) {
      obj[k] = tween(from[k], to[k], easing, position$$1);
    }

    return obj;
  }

  return from;
};

// t: current time, b: beginning value, _c: final value, d: total duration
var tweenFunctions = {
  linear: function(t, b, _c, d) {
    var c = _c - b;
    return c * t / d + b;
  },
  easeInQuad: function(t, b, _c, d) {
    var c = _c - b;
    return c * (t /= d) * t + b;
  },
  easeOutQuad: function(t, b, _c, d) {
    var c = _c - b;
    return -c * (t /= d) * (t - 2) + b;
  },
  easeInOutQuad: function(t, b, _c, d) {
    var c = _c - b;
    if ((t /= d / 2) < 1) {
      return c / 2 * t * t + b;
    } else {
      return -c / 2 * ((--t) * (t - 2) - 1) + b;
    }
  },
  easeInCubic: function(t, b, _c, d) {
    var c = _c - b;
    return c * (t /= d) * t * t + b;
  },
  easeOutCubic: function(t, b, _c, d) {
    var c = _c - b;
    return c * ((t = t / d - 1) * t * t + 1) + b;
  },
  easeInOutCubic: function(t, b, _c, d) {
    var c = _c - b;
    if ((t /= d / 2) < 1) {
      return c / 2 * t * t * t + b;
    } else {
      return c / 2 * ((t -= 2) * t * t + 2) + b;
    }
  },
  easeInQuart: function(t, b, _c, d) {
    var c = _c - b;
    return c * (t /= d) * t * t * t + b;
  },
  easeOutQuart: function(t, b, _c, d) {
    var c = _c - b;
    return -c * ((t = t / d - 1) * t * t * t - 1) + b;
  },
  easeInOutQuart: function(t, b, _c, d) {
    var c = _c - b;
    if ((t /= d / 2) < 1) {
      return c / 2 * t * t * t * t + b;
    } else {
      return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
    }
  },
  easeInQuint: function(t, b, _c, d) {
    var c = _c - b;
    return c * (t /= d) * t * t * t * t + b;
  },
  easeOutQuint: function(t, b, _c, d) {
    var c = _c - b;
    return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
  },
  easeInOutQuint: function(t, b, _c, d) {
    var c = _c - b;
    if ((t /= d / 2) < 1) {
      return c / 2 * t * t * t * t * t + b;
    } else {
      return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
    }
  },
  easeInSine: function(t, b, _c, d) {
    var c = _c - b;
    return -c * Math.cos(t / d * (Math.PI / 2)) + c + b;
  },
  easeOutSine: function(t, b, _c, d) {
    var c = _c - b;
    return c * Math.sin(t / d * (Math.PI / 2)) + b;
  },
  easeInOutSine: function(t, b, _c, d) {
    var c = _c - b;
    return -c / 2 * (Math.cos(Math.PI * t / d) - 1) + b;
  },
  easeInExpo: function(t, b, _c, d) {
    var c = _c - b;
    return (t==0) ? b : c * Math.pow(2, 10 * (t/d - 1)) + b;
  },
  easeOutExpo: function(t, b, _c, d) {
    var c = _c - b;
    return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
  },
  easeInOutExpo: function(t, b, _c, d) {
    var c = _c - b;
    if (t === 0) {
      return b;
    }
    if (t === d) {
      return b + c;
    }
    if ((t /= d / 2) < 1) {
      return c / 2 * Math.pow(2, 10 * (t - 1)) + b;
    } else {
      return c / 2 * (-Math.pow(2, -10 * --t) + 2) + b;
    }
  },
  easeInCirc: function(t, b, _c, d) {
    var c = _c - b;
    return -c * (Math.sqrt(1 - (t /= d) * t) - 1) + b;
  },
  easeOutCirc: function(t, b, _c, d) {
    var c = _c - b;
    return c * Math.sqrt(1 - (t = t / d - 1) * t) + b;
  },
  easeInOutCirc: function(t, b, _c, d) {
    var c = _c - b;
    if ((t /= d / 2) < 1) {
      return -c / 2 * (Math.sqrt(1 - t * t) - 1) + b;
    } else {
      return c / 2 * (Math.sqrt(1 - (t -= 2) * t) + 1) + b;
    }
  },
  easeInElastic: function(t, b, _c, d) {
    var c = _c - b;
    var a, p, s;
    s = 1.70158;
    p = 0;
    a = c;
    if (t === 0) {
      return b;
    } else if ((t /= d) === 1) {
      return b + c;
    }
    if (!p) {
      p = d * 0.3;
    }
    if (a < Math.abs(c)) {
      a = c;
      s = p / 4;
    } else {
      s = p / (2 * Math.PI) * Math.asin(c / a);
    }
    return -(a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
  },
  easeOutElastic: function(t, b, _c, d) {
    var c = _c - b;
    var a, p, s;
    s = 1.70158;
    p = 0;
    a = c;
    if (t === 0) {
      return b;
    } else if ((t /= d) === 1) {
      return b + c;
    }
    if (!p) {
      p = d * 0.3;
    }
    if (a < Math.abs(c)) {
      a = c;
      s = p / 4;
    } else {
      s = p / (2 * Math.PI) * Math.asin(c / a);
    }
    return a * Math.pow(2, -10 * t) * Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
  },
  easeInOutElastic: function(t, b, _c, d) {
    var c = _c - b;
    var a, p, s;
    s = 1.70158;
    p = 0;
    a = c;
    if (t === 0) {
      return b;
    } else if ((t /= d / 2) === 2) {
      return b + c;
    }
    if (!p) {
      p = d * (0.3 * 1.5);
    }
    if (a < Math.abs(c)) {
      a = c;
      s = p / 4;
    } else {
      s = p / (2 * Math.PI) * Math.asin(c / a);
    }
    if (t < 1) {
      return -0.5 * (a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
    } else {
      return a * Math.pow(2, -10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p) * 0.5 + c + b;
    }
  },
  easeInBack: function(t, b, _c, d, s) {
    var c = _c - b;
    if (s === void 0) {
      s = 1.70158;
    }
    return c * (t /= d) * t * ((s + 1) * t - s) + b;
  },
  easeOutBack: function(t, b, _c, d, s) {
    var c = _c - b;
    if (s === void 0) {
      s = 1.70158;
    }
    return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
  },
  easeInOutBack: function(t, b, _c, d, s) {
    var c = _c - b;
    if (s === void 0) {
      s = 1.70158;
    }
    if ((t /= d / 2) < 1) {
      return c / 2 * (t * t * (((s *= 1.525) + 1) * t - s)) + b;
    } else {
      return c / 2 * ((t -= 2) * t * (((s *= 1.525) + 1) * t + s) + 2) + b;
    }
  },
  easeInBounce: function(t, b, _c, d) {
    var c = _c - b;
    var v;
    v = tweenFunctions.easeOutBounce(d - t, 0, c, d);
    return c - v + b;
  },
  easeOutBounce: function(t, b, _c, d) {
    var c = _c - b;
    if ((t /= d) < 1 / 2.75) {
      return c * (7.5625 * t * t) + b;
    } else if (t < 2 / 2.75) {
      return c * (7.5625 * (t -= 1.5 / 2.75) * t + 0.75) + b;
    } else if (t < 2.5 / 2.75) {
      return c * (7.5625 * (t -= 2.25 / 2.75) * t + 0.9375) + b;
    } else {
      return c * (7.5625 * (t -= 2.625 / 2.75) * t + 0.984375) + b;
    }
  },
  easeInOutBounce: function(t, b, _c, d) {
    var c = _c - b;
    var v;
    if (t < d / 2) {
      v = tweenFunctions.easeInBounce(t * 2, 0, c, d);
      return v * 0.5 + b;
    } else {
      v = tweenFunctions.easeOutBounce(t * 2 - d, 0, c, d);
      return v * 0.5 + c * 0.5 + b;
    }
  }
};

var tweenFunctions_1 = tweenFunctions;

var _typeof$7 = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/**
 * An easing function.
 *
 * @param {(function|string)} easing - An easing function or the name of an easing function from https://github.com/chenglou/tween-functions.
 *
 * @returns {function}
 *
 * @example
 * easingFunc('easeInOutQuad')
 */
var easingFunction = function easingFunction(easing) {
  switch (typeof easing === 'undefined' ? 'undefined' : _typeof$7(easing)) {
    case 'string':
      if (tweenFunctions_1[easing]) {
        return tweenFunctions_1[easing];
      }

      {
        throw new TypeError('Easing must match one of the options defined by https://github.com/chenglou/tween-functions');
      }

      /* istanbul ignore next */
      break;

    case 'function':
      return easing;

    default:
      {
        throw new TypeError('Easing must be of type function or string');
      }

      /* istanbul ignore next */
      break;
  }
};

function _toConsumableArray$4(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

function _toArray(arr) { return Array.isArray(arr) ? arr : Array.from(arr); }

/**
 * A WeakMap where the key is a FrameShape and the value is
 * the index of the associated points within an array of Points.
 *
 * @typedef {weakmap} PointsMap
 */

/**
 * Applies a transform to a FrameShape.
 *
 * @param {FrameShape} frameShape
 * @param {(string|number)[]} transform
 *
 * @return {FrameShape}
 *
 * @example
 * transform(frameShape, [ 'rotate', 45 ])
 */
var apply$2 = function apply(frameShape, _ref) {
  var _ref2 = _toArray(_ref),
      name = _ref2[0],
      args = _ref2.slice(1);

  var _flattenPoints = flattenPoints(frameShape),
      points = _flattenPoints.points,
      pointsMap = _flattenPoints.pointsMap;

  var transformedPoints = transformFunctions[name].apply(transformFunctions, [points].concat(_toConsumableArray$4(args)));

  return pointsToFrameShape({
    frameShape: frameShape,
    points: transformedPoints,
    pointsMap: pointsMap
  });
};

/**
 * Creates an array of Points from a FrameShape.
 *
 * @param {FrameShape} frameShape
 * @param {Points[]} [points=[]]
 * @param {PointsMap} [pointsMap=new WeakMap()]
 *
 * @return {Object}
 *
 * @example
 * flattenPoints(frameShape)
 */
var flattenPoints = function flattenPoints(frameShape) {
  var points = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : [];
  var pointsMap = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : new WeakMap();

  var childFrameShapes = frameShape.childFrameShapes;

  if (childFrameShapes) {
    for (var i = 0, l = childFrameShapes.length; i < l; i++) {
      flattenPoints(childFrameShapes[i], points, pointsMap);
    }
  } else {
    pointsMap.set(frameShape, points.length);
    points.push(frameShape.points);
  }

  return { points: points, pointsMap: pointsMap };
};

/**
 * Applies an array of Points to a FrameShape using a PointsMap
 *
 * @param {Object} opts
 * @param {FrameShape} opts.frameShape
 * @param {Points[]} opts.points
 * @param {PointsMap} pointsMap
 *
 */
var pointsToFrameShape = function pointsToFrameShape(_ref3) {
  var frameShape = _ref3.frameShape,
      points = _ref3.points,
      pointsMap = _ref3.pointsMap;

  var childFrameShapes = frameShape.childFrameShapes;

  if (frameShape.points) {
    frameShape.points = points[pointsMap.get(frameShape)];
  }

  if (childFrameShapes) {
    for (var i = 0, l = childFrameShapes.length; i < l; i++) {
      pointsToFrameShape({
        frameShape: childFrameShapes[i],
        points: points,
        pointsMap: pointsMap
      });
    }
  }

  return frameShape;
};

/**
 * Applies an array of transforms to a FrameShape.
 *
 * @param {FrameShape} frameShape
 * @param {(string|number)[][]} transforms
 *
 * @return {FrameShape}
 *
 * @example
 * transform(frameShape, [[ 'rotate', 45 ]])
 */
var transform = function transform(frameShape, transforms) {
  for (var i = 0, l = transforms.length; i < l; i++) {
    frameShape = apply$2(frameShape, transforms[i]);
  }

  return frameShape;
};

function _toArray$1(arr) { return Array.isArray(arr) ? arr : Array.from(arr); }

/**
 * An SVG shape as defined by https://github.com/colinmeinke/svg-points.
 *
 * @typedef {Object} PlainShapeObjectCoreProps
 */

/**
 * Additional options specifically for a motion path.
 *
 * @typedef {Object} PlainShapeObjectMotionPathProps
 *
 * @property {number} accuracy - .
 * @property {boolean|number} rotate - .
 */

/**
 * The tween options to use when transitioning from a previous shape.
 *
 * @typedef {Object} PlainShapeObjectTweenProps
 *
 * @property {number} delay - Milliseconds before the tween starts.
 * @property {number} duration - Milliseconds until tween finishes.
 * @property {string|function} easing - The name of an easing function, or an easing function.
 */

/**
 * A static shape.
 *
 * @typedef {Object} PlainShapeObject
 *
 * @extends PlainShapeObjectCoreProps
 * @extends PlainShapeObjectMotionPathProps
 * @extends PlainShapeObjectTweenProps
 * @property {string|number} name
 */

/**
 * Validates PlainShapeObjectCoreProps.
 *
 * @param {PlainShapeObject[]} plainShapeObjects
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * if (corePropsValid([ circle ])) {
 *   console.log('circle has valid Plain Shape Object Core Props')
 * }
 */
var corePropsValid = function corePropsValid(plainShapeObjects) {
  var errors = [];

  for (var i = 0, l = plainShapeObjects.length; i < l; i++) {
    var result = valid(plainShapeObjects[i]);

    if (!result.valid) {
      var errs = result.errors;

      for (var _i = 0, _l = errs.length; _i < _l; _i++) {
        errors.push(errs[_i]);
      }
    }
  }

  if (errors.length) {
    throw new TypeError(errorMsg(errors));
  }

  return true;
};

/**
 * Joins an array of error messages into one error message.
 *
 * @param {string[]} errors
 *
 * @returns {string}
 *
 * @example
 * errorMsg([
 *   'cx prop is required on a ellipse',
 *   'cy prop must be of type number'
 * ])
 */
var errorMsg = function errorMsg(errors) {
  return 'Plain Shape Object props not valid: ' + errors.join('. ');
};

/**
 * Validates forces prop.
 *
 * @param {PlainShapeObject[]} plainShapeObjects
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * if (forcesPropValid([ circle ])) {
 *   console.log('circle has valid forces prop')
 * }
 */
var forcesPropValid = function forcesPropValid(plainShapeObjects) {
  var errors = [];

  for (var i = 0, l = plainShapeObjects.length; i < l; i++) {
    var forces = plainShapeObjects[i].forces;

    if (typeof forces !== 'undefined') {
      if (Array.isArray(forces)) {
        for (var _i = 0, _l = forces.length; _i < _l; _i++) {
          if (typeof forces[_i] !== 'function') {
            errors.push('each force item should be of type function');
          }
        }
      } else {
        errors.push('the forces prop must be of type array');
      }
    }
  }

  if (errors.length) {
    throw new TypeError(errorMsg(errors));
  }

  return true;
};

/**
 * Validates PlainShapeObjectMotionPathProps.
 *
 * @param {PlainShapeObject[]} plainShapeObjects
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * if (motionPathPropsValid([ circle ])) {
 *   console.log('circle has valid motion path props')
 * }
 */
var motionPathPropsValid = function motionPathPropsValid(plainShapeObjects) {
  var errors = [];

  for (var i = 0, l = plainShapeObjects.length; i < l; i++) {
    var _plainShapeObject = plainShapeObjects[i];
    var accuracy = _plainShapeObject.accuracy;
    var rotate = _plainShapeObject.rotate;

    if (typeof accuracy !== 'undefined' && !(typeof accuracy === 'number' && accuracy > 0)) {
      errors.push('the accuracy prop must be a number greater than 0');
    }

    if (typeof rotate !== 'undefined' && !(typeof rotate === 'boolean' || typeof rotate === 'number')) {
      errors.push('the rotate prop must be a of type boolean or number');
    }
  }

  if (errors.length) {
    throw new TypeError(errorMsg(errors));
  }

  return true;
};

/**
 * Validates name prop.
 *
 * @param {PlainShapeObject[]} plainShapeObjects
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * if (namePropValid([ circle ])) {
 *   console.log('circle has a valid name prop')
 * }
 */
var namePropValid = function namePropValid(plainShapeObjects) {
  var errors = [];

  for (var i = 0, l = plainShapeObjects.length; i < l; i++) {
    var name = plainShapeObjects[i].name;

    if (typeof name !== 'undefined' && !(typeof name === 'string' || typeof name === 'number')) {
      errors.push('the name prop must be of type string or number');
    }
  }

  if (errors.length) {
    throw new TypeError(errorMsg(errors));
  }

  return true;
};

/**
 * Validates transforms prop.
 *
 * @param {PlainShapeObject[]} plainShapeObjects
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * if (transformsPropValid([ circle ])) {
 *   console.log('circle has valid transforms prop')
 * }
 */
var transformsPropValid = function transformsPropValid(plainShapeObjects) {
  var errors = [];

  for (var i = 0, l = plainShapeObjects.length; i < l; i++) {
    var transforms = plainShapeObjects[i].transforms;

    if (typeof transforms !== 'undefined') {
      if (Array.isArray(transforms)) {
        for (var _i = 0, _l = transforms.length; _i < _l; _i++) {
          var _transforms$i = _toArray$1(transforms[i]),
              key = _transforms$i[0],
              args = _transforms$i.slice(1);

          switch (key) {
            case 'moveIndex':
            case 'rotate':
              if (args.length === 1) {
                if (typeof args[0] !== 'number') {
                  errors.push('moveIndex transform argument should be of type number');
                }
              } else {
                errors.push('moveIndex transform takes 1 argument');
              }

              break;

            case 'offset':
              if (args.length === 2) {
                if (typeof args[0] !== 'number' || typeof args[1] !== 'number') {
                  errors.push('both offset transform arguments should be of type number');
                }
              } else {
                errors.push('offset transform takes 2 arguments (x and y)');
              }

              break;

            case 'reverse':
              if (args.length > 0) {
                errors.push('reverse transform takes no arguments');
              }

              break;

            case 'scale':
              if (args.length > 0 && args.length < 3) {
                if (typeof args[0] !== 'number') {
                  errors.push('scale transform scaleFactor argument should be of type number');
                }

                if (typeof args[1] !== 'undefined' && typeof args[1] !== 'string') {
                  errors.push('scale transform anchor argument should be of type string');
                }
              } else {
                errors.push('scale transform takes 1 or 2 arguments');
              }

              break;

            default:
              errors.push(key + ' is not a valid transform');
          }
        }
      } else {
        errors.push('the transforms prop must be of type array');
      }
    }
  }

  if (errors.length) {
    throw new TypeError(errorMsg(errors));
  }

  return true;
};

/**
 * Validates PlainShapeObjectTweenProps.
 *
 * @param {PlainShapeObject[]} plainShapeObjects
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * if (tweenPropsValid([ circle, square ])) {
 *   console.log('circle and square have valid tween props')
 * }
 */
var tweenPropsValid = function tweenPropsValid(plainShapeObjects) {
  var errors = [];

  for (var i = 0, l = plainShapeObjects.length; i < l; i++) {
    var _plainShapeObjects$i = plainShapeObjects[i],
        delay = _plainShapeObjects$i.delay,
        duration = _plainShapeObjects$i.duration,
        easing = _plainShapeObjects$i.easing;


    if (typeof delay !== 'undefined' && !(typeof delay === 'number' && delay > 0)) {
      errors.push('the delay prop must be a number greater than 0');
    }

    if (typeof duration !== 'undefined' && !(typeof duration === 'number' && duration >= 0)) {
      errors.push('the duration prop must be a number greater than or equal to 0');
    }

    if (typeof easing !== 'undefined' && !(typeof easing === 'function' || typeof easing === 'string')) {
      errors.push('the easing prop must be a of type function or string');
    }
  }

  if (errors.length) {
    throw new TypeError(errorMsg(errors));
  }

  return true;
};

/**
 * Validates one or more PlainShapeObject.
 *
 * @param {...PlainShapeObject} plainShapeObjects
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * if (valid(circle)) {
 *   console.log('circle is a valid Plain Shape Object')
 * }
 */
var valid$1 = function valid$$1() {
  for (var _len = arguments.length, plainShapeObjects = Array(_len), _key = 0; _key < _len; _key++) {
    plainShapeObjects[_key] = arguments[_key];
  }

  return namePropValid(plainShapeObjects) && corePropsValid(plainShapeObjects) && forcesPropValid(plainShapeObjects) && transformsPropValid(plainShapeObjects) && tweenPropsValid(plainShapeObjects) && motionPathPropsValid(plainShapeObjects);
};

var _extends$8 = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

function _objectWithoutProperties$3(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

/**
 * The data required to render and tween to a shape.
 *
 * @typedef {Object} Keyframe
 *
 * @property {(string|number)} name - A unique reference.
 * @property {Position} position
 * @property {FrameShape} frameShape
 * @property {Object} tween
 */

/**
 * A Keyframe array and their total duration.
 *
 * @typedef {Object} KeyframesAndDuration
 *
 * @property {Keyframe[]} keyframes
 * @property {number} duration
 */

/**
 * Converts Keyframes so each has the same
 * PointStructure and CurveStructure.
 *
 * @param {Keyframe[]} keyframes
 *
 * @returns {Keyframe[]}
 *
 * @example
 * equaliseKeyframes(keyframes)
 */
var equaliseKeyframes = function equaliseKeyframes(keyframes) {
  var pointStrucs = [];
  var k = [];
  var curveStrucs = [];
  var result = [];

  for (var i = 0, l = keyframes.length; i < l; i++) {
    pointStrucs.push(pointStructure(keyframes[i].frameShape));
  }

  var pointStruc = commonPointStructure(pointStrucs);

  for (var _i = 0, _l = keyframes.length; _i < _l; _i++) {
    var keyframe = keyframes[_i];
    keyframe.frameShape = applyPointStructure(keyframe.frameShape, pointStruc);
    k.push(keyframe);
  }

  for (var _i2 = 0, _l2 = k.length; _i2 < _l2; _i2++) {
    curveStrucs.push(curveStructure(k[_i2].frameShape));
  }

  var curveStruc = commonCurveStructure(curveStrucs);

  for (var _i3 = 0, _l3 = k.length; _i3 < _l3; _i3++) {
    var _keyframe = k[_i3];
    _keyframe.frameShape = applyCurveStructure(_keyframe.frameShape, curveStruc);
    result.push(_keyframe);
  }

  return result;
};

/**
 * Creates a Keyframe array from a PlainShapeObject array.
 *
 * @param {PlainShapeObject[]} plainShapeObjects
 *
 * @returns {KeyframesAndDuration}
 *
 * @example
 * keyframes([ circle, square ])
 */
var keyframesAndDuration = function keyframesAndDuration(plainShapeObjects) {
  var keyframes = [];

  for (var i = 0, l = plainShapeObjects.length; i < l; i++) {
    var _plainShapeObjects$i = plainShapeObjects[i],
        delay = _plainShapeObjects$i.delay,
        duration = _plainShapeObjects$i.duration,
        easing = _plainShapeObjects$i.easing,
        _plainShapeObjects$i$ = _plainShapeObjects$i.forces,
        forces = _plainShapeObjects$i$ === undefined ? [] : _plainShapeObjects$i$,
        name = _plainShapeObjects$i.name,
        _plainShapeObjects$i$2 = _plainShapeObjects$i.transforms,
        transforms = _plainShapeObjects$i$2 === undefined ? [] : _plainShapeObjects$i$2,
        plainShapeObject = _objectWithoutProperties$3(_plainShapeObjects$i, ['delay', 'duration', 'easing', 'forces', 'name', 'transforms']);

    var frameShape = frameShapeFromPlainShapeObject(plainShapeObject);

    var keyframe = {
      name: typeof name !== 'undefined' ? name : i,
      frameShape: transform(frameShape, transforms)
    };

    if (i > 0) {
      keyframe.tween = {
        duration: typeof duration !== 'undefined' ? duration : config.defaults.keyframe.duration,
        easing: easingFunction(easing || config.defaults.keyframe.easing),
        forces: forces
      };

      if (delay) {
        var previousKeyframe = keyframes[keyframes.length - 1];

        var delayKeyframe = _extends$8({}, previousKeyframe, {
          name: previousKeyframe.name + '.delay',
          tween: { duration: delay }
        });

        keyframes.push(delayKeyframe);
      }
    }

    keyframes.push(keyframe);
  }

  var equalisedKeyframes = equaliseKeyframes(keyframes);
  var totalDuration = keyframesTotalDuration(keyframes);

  return {
    duration: totalDuration,
    keyframes: positionKeyframes(equalisedKeyframes, totalDuration)
  };
};

/**
 * Adds the position prop to each Keyframe in a Keyframe array.
 *
 * @param {Keyframe[]} keyframes
 * @param {number} totalDuration
 *
 * @returns {Keyframe[]}
 *
 * @example
 * positionKeyframes(keyframes)
 */
var positionKeyframes = function positionKeyframes(keyframes, totalDuration) {
  var k = [];

  var durationAtKeyframe = 0;

  for (var i = 0, l = keyframes.length; i < l; i++) {
    var keyframe = keyframes[i];
    var _keyframe$tween = keyframe.tween;
    _keyframe$tween = _keyframe$tween === undefined ? {} : _keyframe$tween;
    var _keyframe$tween$durat = _keyframe$tween.duration,
        duration = _keyframe$tween$durat === undefined ? 0 : _keyframe$tween$durat;


    durationAtKeyframe += duration;

    k.push(_extends$8({}, keyframe, {
      position: durationAtKeyframe === 0 ? 0 : durationAtKeyframe / totalDuration
    }));
  }

  return k;
};

/**
 * Adds the tween duration of a Keyframe array.
 *
 * @param {Keyframe[]} k
 *
 * @returns {number}
 *
 * @example
 * keyframesTotalDuration(keyframes)
 */
var keyframesTotalDuration = function keyframesTotalDuration(k) {
  var currentDuration = 0;

  for (var i = 0, l = k.length; i < l; i++) {
    var _k$i$tween = k[i].tween;
    _k$i$tween = _k$i$tween === undefined ? {} : _k$i$tween;
    var _k$i$tween$duration = _k$i$tween.duration,
        duration = _k$i$tween$duration === undefined ? 0 : _k$i$tween$duration;

    currentDuration += duration;
  }

  return currentDuration;
};

var _typeof$9 = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

function _toConsumableArray$5(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

/**
 * A sequence of static shapes.
 *
 * @typedef {Object} Shape
 *
 * @property {Keyframe[]} keyframes
 */

/**
 * An object containing PlainShapeObjects and shape options.
 *
 * @typedef {Object} SortedShapeProps
 *
 * @property {PlainShapeObject[]} plainShapeObjects
 * @property {Object} options
 * @property {(string|number)} options.name
 */

/**
 * Creates a Shape from one or more PlainShapeObject.
 * Optionally can take an options object as the last argument.
 *
 * @param {(PlainShapeObject|Object)[]} props
 *
 * @returns {Shape}
 *
 * @example
 * shape(circle, square)
 */
var shape = function shape() {
  for (var _len = arguments.length, props = Array(_len), _key = 0; _key < _len; _key++) {
    props[_key] = arguments[_key];
  }

  var _sort = sort$1(props),
      plainShapeObjects = _sort.plainShapeObjects,
      name = _sort.options.name;

  var _keyframesAndDuration = keyframesAndDuration(plainShapeObjects),
      duration = _keyframesAndDuration.duration,
      keyframes = _keyframesAndDuration.keyframes;

  var s = { duration: duration, keyframes: keyframes };

  if (typeof name !== 'undefined') {
    s.name = name;
  }

  return s;
};

/**
 * Sorts an array of props into a PlainShapeObject array and options.
 *
 * @param {(PlainShapeObject|Object)[]} props
 *
 * @returns {SortedShapeProps}
 *
 * @example
 * sort(props)
 */
var sort$1 = function sort(props) {
  var plainShapeObjects = props.filter(function (prop) {
    if ((typeof prop === 'undefined' ? 'undefined' : _typeof$9(prop)) !== 'object') {
      throw new TypeError('The shape function must only be passed objects');
    }

    return prop.type;
  });

  var options = props.length > 1 && typeof props[props.length - 1].type === 'undefined' ? props[props.length - 1] : {};

  var sortedProps = { plainShapeObjects: plainShapeObjects, options: options };

  if (validProps(sortedProps)) {
    return sortedProps;
  }
};

/**
 * Validates a PlainShapeObject array and shape options.
 *
 * @param {SortedShapeProps}
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * validProps({ plainShapeObjects, options })
 */
var validProps = function validProps(_ref) {
  var plainShapeObjects = _ref.plainShapeObjects,
      name = _ref.options.name;

  if (plainShapeObjects.length === 0) {
    throw new TypeError('The shape function must be passed at least one Plain Shape Object');
  }

  if (valid$1.apply(undefined, _toConsumableArray$5(plainShapeObjects))) {
    if (typeof name !== 'undefined' && typeof name !== 'string' && typeof name !== 'number') {
      throw new TypeError('The name option passed to the shape function must be of type string or number');
    }
  }

  return true;
};

var _typeof$a = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var _extends$9 = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

function _toConsumableArray$6(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

/**
 * A DOM node.
 *
 * @typedef {Object} Node
 */

/**
 * The data from a Node that is useful in FrameShape and PlainShapeObject creation.
 *
 * @typedef {Object} NodeData
 *
 * @property {Object} attributes - All HTML attributes of the Node (excluding blacklist).
 * @property {Object[]} childNodes
 * @property {string} type - The nodeName of the Node.
 */

/**
 * Attributes to ignore.
 */
var attributeBlacklist = ['data-jsx-ext', 'data-reactid'];

/**
 * Wilderness' accepted node types core props.
 */
var nodeCoreProps = [{
  type: 'circle',
  coreProps: ['cx', 'cy', 'r']
}, {
  type: 'ellipse',
  coreProps: ['cx', 'cy', 'rx', 'ry']
}, {
  type: 'g',
  coreProps: []
}, {
  type: 'line',
  coreProps: ['x1', 'x2', 'y1', 'y2']
}, {
  type: 'path',
  coreProps: ['d']
}, {
  type: 'polygon',
  coreProps: ['points']
}, {
  type: 'polyline',
  coreProps: ['points']
}, {
  type: 'rect',
  coreProps: ['height', 'rx', 'ry', 'width', 'x', 'y']
}];

/**
 * Generates Wilderness' accepted node types from core props object.
 *
 * @returns {string[]}
 *
 * @example
 * getNodeTypes()
 */
var getNodeTypes = function getNodeTypes() {
  var types = [];

  for (var i = 0, l = nodeCoreProps.length; i < l; i++) {
    types.push(nodeCoreProps[i].type);
  }

  return types;
};

/**
 * Wilderness' accepted node types.
 */
var nodeTypes = getNodeTypes();

/**
 * Core props for the defined node type.
 *
 * @param {string} type
 *
 * @returns {Object}
 *
 * @example
 * coreProps('rect')
 */
var coreProps = function coreProps(type) {
  for (var i = 0, l = nodeCoreProps.length; i < l; i++) {
    if (nodeCoreProps[i].type === type) {
      return nodeCoreProps[i].coreProps;
    }
  }

  return [];
};

/**
 * Creates a group Node from a FrameShape array.
 *
 * @param {FrameShape[]} childFrameShapes
 *
 * @returns {Node}
 *
 * @example
 * groupNode(childFrameShapes)
 */
var groupNode = function groupNode(childFrameShapes) {
  var nodes = [];

  for (var i = 0, l = childFrameShapes.length; i < l; i++) {
    nodes.push(node(childFrameShapes[i]));
  }

  var group = document.createElementNS('http://www.w3.org/2000/svg', 'g');

  for (var _i2 = 0, _l2 = nodes.length; _i2 < _l2; _i2++) {
    group.appendChild(nodes[_i2]);
  }

  return group;
};

/**
 * Creates a Node from a FrameShape.
 *
 * @param {FrameShape} frameShape
 *
 * @returns {Node}
 *
 * @example
 * node(frameShape)
 */
var node = function node(frameShp) {
  if (validFrameShape(frameShp)) {
    var attributes = frameShp.attributes;

    var el = frameShp.childFrameShapes ? groupNode(frameShp.childFrameShapes) : pathNode(frameShp.points);

    for (var attr in attributes) {
      el.setAttribute(attr, attributes[attr]);
    }

    return el;
  }
};

/**
 * Creates NodeData given a Node.
 *
 * @param {Node} el
 *
 * @returns {NodeData}
 *
 * @example
 * nodeData(el)
 */
var nodeData = function nodeData(el) {
  var attributes = {};

  if (el.hasAttributes()) {
    var attrs = [].concat(_toConsumableArray$6(el.attributes));

    for (var i = 0, l = attrs.length; i < l; i++) {
      var attr = attrs[i];
      var name = attr.name;

      if (attributeBlacklist.indexOf(name) === -1) {
        attributes[name] = attr.value;
      }
    }
  }

  return { attributes: attributes, childNodes: [].concat(_toConsumableArray$6(el.childNodes)), type: el.nodeName };
};

/**
 * Creates a path Node from Points.
 *
 * @param {Points} points
 *
 * @returns {Node}
 *
 * @example
 * pathNode(points)
 */
var pathNode = function pathNode(points) {
  var path = document.createElementNS('http://www.w3.org/2000/svg', 'path');

  path.setAttribute('d', toPath(points));

  return path;
};

/**
 * Creates a PlainShapeObject from a Node.
 *
 * @param {Node} el
 *
 * @returns {PlainShapeObject}
 *
 * @example
 * plainShapeObject(el)
 */
var plainShapeObject$1 = function plainShapeObject(el) {
  if (validNode(el)) {
    var data = nodeData(el);
    var attributes = data.attributes;
    var type = data.type;

    if (type === 'g') {
      var childNodes = data.childNodes;
      var shapes = [];

      for (var i = 0, l = childNodes.length; i < l; i++) {
        var n = childNodes[i];

        if (validNodeType(n.nodeName)) {
          shapes.push(plainShapeObject(n));
        }
      }

      return _extends$9({}, attributes, { type: type, shapes: shapes });
    }

    return _extends$9({}, attributes, plainShapeObjectFromAttrs(type, attributes));
  }
};

/**
 * Creates a PlainShapeObject from type and an attribute object.
 *
 * @param {string} type
 * @param {Object} attributes
 *
 * @returns {PlainShapeObject}
 *
 * @example
 * plainShapeObjectFromAttrs('rect', attributes)
 */
var plainShapeObjectFromAttrs = function plainShapeObjectFromAttrs(type, attributes) {
  var props = coreProps(type);
  var result = { type: type };

  for (var k in attributes) {
    if (props.indexOf(k) !== -1) {
      var v = attributes[k];
      var n = Number(v);
      result[k] = Number.isNaN(n) ? v : n;
    }
  }

  return result;
};

/**
 * Updates a Node from a FrameShape.
 *
 * @param {Node} el
 * @param {FrameShape} frameShape
 *
 * @returns {Node}
 *
 * @example
 * updateNode(el, frameShape)
 */
var updateNode = function updateNode(el, frameShp) {
  var changes = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : [];

  {
    if (!validNode(el)) ;

    if (!validFrameShape(frameShp)) ;
  }

  var shouldApplyChanges = changes.length === 0;
  var currentAttributes = el.attributes;
  var nextAttributes = frameShp.attributes;
  var childFrameShapes = frameShp.childFrameShapes;
  var changesKey = changes.push({ el: el, remove: [], update: {} }) - 1;

  for (var k in currentAttributes) {
    if (typeof nextAttributes[k] === 'undefined') {
      changes[changesKey].remove.push(k);
    }
  }

  for (var _k in nextAttributes) {
    var c = currentAttributes[_k];
    var n = nextAttributes[_k];

    if (typeof c === 'undefined' || c !== n) {
      changes[changesKey].update[_k] = n;
    }
  }

  if (!childFrameShapes) {
    var nextPath = toPath(frameShp.points);

    if (nextPath !== el.getAttribute('d')) {
      changes[changesKey].update.d = nextPath;
    }
  } else {
    var allChildNodes = [].concat(_toConsumableArray$6(el.childNodes));
    var childNodes = [];

    for (var i = 0, l = allChildNodes.length; i < l; i++) {
      var _n = allChildNodes[i];

      if (validNodeType(_n.nodeName)) {
        childNodes.push(_n);
      }
    }

    for (var _i3 = 0, _l3 = childFrameShapes.length; _i3 < _l3; _i3++) {
      updateNode(childNodes[_i3], childFrameShapes[_i3], changes);
    }
  }

  if (shouldApplyChanges) {
    for (var _i4 = 0, _l4 = changes.length; _i4 < _l4; _i4++) {
      var change = changes[_i4];
      var _el = change.el;
      var remove = change.remove;
      var update = change.update;

      for (var _i = 0, _l = remove.length; _i < _l; _i++) {
        _el.removeAttribute(remove[_i]);
      }

      for (var _k2 in update) {
        _el.setAttribute(_k2, update[_k2]);
      }
    }
  }

  return el;
};

/**
 * Is a FrameShape valid?
 *
 * @param {FrameShape} frameShp
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * validFrameShape(frameShape)
 */
var validFrameShape = function validFrameShape(frameShp) {
  {
    if ((typeof frameShp === 'undefined' ? 'undefined' : _typeof$a(frameShp)) !== 'object' || Array.isArray(frameShp)) {
      throw new TypeError('frameShape must be of type object');
    }

    var attributes = frameShp.attributes;
    var childFrameShapes = frameShp.childFrameShapes;
    var points = frameShp.points;

    if (typeof attributes === 'undefined') {
      throw new TypeError('frameShape must include an attributes property');
    }

    if ((typeof attributes === 'undefined' ? 'undefined' : _typeof$a(attributes)) !== 'object' || Array.isArray(attributes)) {
      throw new TypeError('frameShape attributes property must be of type object');
    }

    if (typeof childFrameShapes === 'undefined' && typeof points === 'undefined') {
      throw new TypeError('frameShape must have either a points or childFrameShapes property');
    }

    if (points && !Array.isArray(points)) {
      throw new TypeError('frameShape points property must be of type array');
    }

    if (childFrameShapes) {
      if (!Array.isArray(childFrameShapes)) {
        throw new TypeError('frameShape childFrameShapes property must be of type array');
      }

      for (var i = 0, l = childFrameShapes.length; i < l; i++) {
        var childFrameShape = childFrameShapes[i];

        if ((typeof childFrameShape === 'undefined' ? 'undefined' : _typeof$a(childFrameShape)) !== 'object' || _typeof$a(childFrameShape.attributes) !== 'object') {
          throw new TypeError('frameShape childFrameShapes property must be array of frameShapes');
        }
      }
    }
  }

  return true;
};

/**
 * Is a Node valid?
 *
 * @param {Node} el
 *
 * @throws {TypeError} Throws if not valid
 *
 * @returns {true}
 *
 * @example
 * validNode(el)
 */
var validNode = function validNode(el) {
  {
    if ((typeof el === 'undefined' ? 'undefined' : _typeof$a(el)) !== 'object' || !el.nodeName) {
      throw new TypeError('el must be a DOM node');
    }

    if (!validNodeType(el.nodeName)) {
      throw new TypeError('el must be an SVG basic shape or group element');
    }
  }

  return true;
};

/**
 * Is a node name one of the accepted node types?
 *
 * @param {string} nodeName
 *
 * @returns {boolean}
 *
 * @example
 * validNodeType(nodeName)
 */
var validNodeType = function validNodeType(nodeName) {
  return nodeTypes.indexOf(nodeName) !== -1;
};

/* globals __DEV__ */

/**
 * Is the tick function running?
 */
var ticks = 0;

/**
 * Calculate the active Timeline Shapes and update the corresponding Nodes.
 * Call recursively until there are no longer any active Timelines.
 *
 * @param {number} [at]
 *
 * @example
 * tick()
 */
var tick = function tick(at) {
  if (!ticks) {
    if (typeof at !== 'undefined' && typeof at !== 'number') {
      throw new TypeError('The tick functions at option must be of type number');
    }

    window.requestAnimationFrame(function () {
      var a = typeof at !== 'undefined' ? at : Date.now();

      var retick = false;

      ticks++;

      for (var i = 0, l = timelines.length; i < l; i++) {
        var t = timelines[i];
        var state = t.state;

        if (state.started && !state.finished && state.rendered) {
          var timelineShapes = t.timelineShapes;
          var frameShapes = frame(t, a);

          for (var _i = 0, _l = timelineShapes.length; _i < _l; _i++) {
            updateNode(timelineShapes[_i].shape.node, frameShapes[_i]);
          }

          events(t);

          retick = true;
        }
      }

      ticks--;

      if (retick) {
        tick();
      }
    });
  }
};

/**
 * Extends the Wilderness core timeline function.
 * Pushes each timeline into the timelines array.
 *
 * @param {...(Shape|Object[]|TimelineOptions)[]} props
 *
 * @returns {Timeline}
 *
 * @example
 * timeline(circle, [ square, { queue: -200 } ], { duration: 5000 })
 */
var timeline$1 = function timeline$$1() {
  var t = timeline.apply(undefined, arguments);
  timelines.push(t);
  return t;
};

/**
 * An array of every Timeline created.
 */
var timelines = [];

var _typeof$b = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/**
 * An object holding both Shapes and Timelines.
 *
 * @typedef {Object} ShapesAndTimelines
 *
 * @property {Shape[]} shapes
 * @property {Timeline[]} timelines
 */

/**
 * Renders Shapes or Timelines to a container Node.
 *
 * @param {Node} container
 * @param {...(Shape|Timeline)} shapesAndTimelines
 *
 * @example
 * render(svg, shape, timeline)
 */
var render = function render(container) {
  for (var _len = arguments.length, shapesAndTimelines = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
    shapesAndTimelines[_key - 1] = arguments[_key];
  }

  {
    if ((typeof container === 'undefined' ? 'undefined' : _typeof$b(container)) !== 'object' || !container.nodeName) {
      throw new TypeError('The render function must be a DOM node as first argument');
    }
  }

  var shapesToRender = [];
  var result = split(shapesAndTimelines);
  var shapes = result.shapes;
  var timelines = result.timelines;

  for (var i = 0, l = shapes.length; i < l; i++) {
    var shape$$1 = shapes[i];
    shape$$1.node = node(shape$$1.keyframes[0].frameShape);
    shapesToRender.push(shape$$1);
    shape$$1.rendered = true;
  }

  for (var _i2 = 0, _l2 = timelines.length; _i2 < _l2; _i2++) {
    var timeline$$1 = timelines[_i2];
    var timelineShapes = timeline$$1.timelineShapes;
    var frameShapes = frame(timeline$$1);

    for (var _i = 0, _l = timelineShapes.length; _i < _l; _i++) {
      var _shape = timelineShapes[_i].shape;
      _shape.node = node(frameShapes[_i]);
      shapesToRender.push(_shape);
    }

    timeline$$1.state.rendered = true;
  }

  for (var _i3 = 0, _l3 = shapesToRender.length; _i3 < _l3; _i3++) {
    var _shape2 = shapesToRender[_i3];

    if (_shape2.replace) {
      _shape2.replace.parentNode.replaceChild(_shape2.node, _shape2.replace);
      delete _shape2.replace;
    } else {
      container.appendChild(_shape2.node);
    }
  }

  tick();
};

/**
 * Splits a Shape and Timeline array into ShapesAndTimelines.
 *
 * @param {(Shape|Timeline)[]} shapesAndTimelines
 *
 * @returns {ShapesAndTimelines}
 *
 * @example
 * split([ shape, timeline ])
 */
var split = function split(shapesAndTimelines) {
  var result = { shapes: [], timelines: [] };

  for (var i = 0, l = shapesAndTimelines.length; i < l; i++) {
    var x = shapesAndTimelines[i];

    if ((typeof x === 'undefined' ? 'undefined' : _typeof$b(x)) === 'object' && x.keyframes) {
      {
        if (x.timeline) {
          throw new Error('You cannot render a shape that has been placed on a timeline, instead render the timeline');
        }

        if (x.rendered) {
          throw new Error('You cannot render the same shape twice');
        }
      }

      result.shapes.push(x);
    } else if ((typeof x === 'undefined' ? 'undefined' : _typeof$b(x)) === 'object' && x.middleware && x.playbackOptions && x.state && x.timelineShapes) {
      if (x.state.rendered) {
        throw new Error('You cannot render the same timeline twice');
      }

      result.timelines.push(x);
    } else {
      throw new Error('The render function only takes shapes and timelines from the second argument onwards');
    }
  }

  return result;
};

var _typeof$c = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var _extends$a = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

/**
 * Extends the Wilderness core shape function.
 * Adds the ability to pull keyframe attributes and points from a SVG DOM node.
 * Adds an optional replace property to a Shape, which is used during render.
 *
 * @param {(PlainShapeObject|Object)[]} props
 *
 * @returns {Shape}
 *
 * @example
 * shape({ el, style: '' }, { replace: el })
 */
var shape$1 = function shape$$1() {
  for (var _len = arguments.length, props = Array(_len), _key = 0; _key < _len; _key++) {
    props[_key] = arguments[_key];
  }

  var args = [];

  for (var i = 0, l = props.length; i < l; i++) {
    var prop = props[i];

    if (prop.el) {
      var p = _extends$a({}, plainShapeObject$1(prop.el), prop);

      delete p.el;

      args.push(p);
    }

    args.push(prop);
  }

  var s = shape.apply(undefined, args);

  var options = props.length > 1 && typeof props[props.length - 1].type === 'undefined' ? props[props.length - 1] : {};

  var replace = options.replace;

  if (replace) {
    if ((typeof replace === 'undefined' ? 'undefined' : _typeof$c(replace)) !== 'object' || !replace.nodeName) {
      throw new TypeError('The replace option must be a DOM node');
    }

    s.replace = replace;
  }

  return s;
};

/**
 * This file contains basic functionality to allow SVG diagrams to have behaviours and
 * animate between different versions.
 */
function reconcileAttributes(fromElement, toElement) {
    var toAtts = Array.from(toElement.attributes).map(a => a.name);
    var fromAtts = Array.from(fromElement.attributes).map(a => a.name);
    var toRemove = fromAtts.filter(a => -1 == toAtts.indexOf(a));
    toRemove.forEach(a => fromElement.removeAttribute(a));
    toAtts.forEach(a => {
        fromElement.setAttribute(a, toElement.getAttribute(a));
    });
}
function reconcileText(fromElement, toElement) {
    fromElement.textContent = toElement.textContent;
}
const canMorph = ["rect", "path"];
function merge(fromElement, toElement) {
    if (-1 != canMorph.indexOf(fromElement.tagName)) {
        const kf1 = { fromElement };
        const kf2 = { toElement };
        const shapeOptions = { replace: fromElement };
        const morph = shape$1(kf1, kf2, shapeOptions);
        const playbackOptions = {
            alternate: true,
            duration: 2000,
            iterations: Infinity
        };
        const animation = timeline$1(morph, playbackOptions);
        render(document.querySelector('svg'), animation);
        return;
    }
    reconcileAttributes(fromElement, toElement);
    var fi = 0;
    var ti = 0;
    while (fromElement.childElementCount > toElement.childElementCount) {
        fromElement.children[fromElement.childElementCount - 1].remove();
    }
    if (toElement.childElementCount == 0) {
        reconcileText(fromElement, toElement);
    }
    while (ti < toElement.childElementCount) {
        if (fi < fromElement.childElementCount) {
            // element on both sides.
            var newFromElement = fromElement.children[fi];
            var newToElement = toElement.children[ti];
            if (newFromElement.tagName == newToElement.tagName) {
                merge(newFromElement, newToElement);
                fi++;
                ti++;
            }
            else {
                console.log("Replacing: " + newFromElement.tagName + " " + newToElement);
                fromElement.insertBefore(newToElement, newFromElement);
                newFromElement.remove();
                fi++;
            }
        }
        else {
            const newToElement = toElement.children[ti];
            const newFromElement = document.createElementNS(newToElement.namespaceURI, newToElement.tagName);
            fromElement.appendChild(newFromElement);
            merge(newFromElement, newToElement);
        }
    }
}
function transition(documentElement) {
    merge(document.querySelector("body svg"), documentElement);
    // force the load event to occur again
    var evt = document.createEvent('Event');
    evt.initEvent('load', false, false);
    window.dispatchEvent(evt);
}
function tb() {
    return "does nothing";
}

export { transition, tb };
