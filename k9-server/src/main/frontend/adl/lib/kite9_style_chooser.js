import jQuery from 'jquery'
const $ = jQuery

function setup_style_chooser(kite9, control) {
	
	const MAIN_ATTRIBUTES = ['stroke', 'stroke-width', 'stroke-dasharray', 'stroke-linejoin', 'stroke-linecap', 'fill'];
	const TEXT_ATTRIBUTES = ['font-family', 'font-size', 'fill'];
	
	function add_px(v) {
		return v+'px';
	}
	
	function remove_units(v) {
		return ""+parseFloat(v);
	}
	
	function remove_quotes(v) {
		return v.substring(1, v.length-1);
	}
	
	function add_quotes(v) {
		return "'"+v+"'";
	}
	
	function fill_post(v) {
		if (v.indexOf("-") > -1) {
			return add_quotes(v);
		} else {
			return v;
		}
	}
	
	function fill_pre(v) {
		if (v.indexOf("-") > -1) {
			return remove_quotes(v);
		} else {
			return v;
		}
	}
	
	const POST_PROCESSORS = { 'stroke-width' : add_px,
						'stroke-dasharray' : add_quotes,
						'font-size' : add_px,
						'fill' : fill_post
						//'font-family' : add_quotes
						};
	
	const PRE_PROCESSORS = { 'stroke-width' : remove_units,
		'font-size' : remove_units,
		//'font-family' : remove_quotes
		'stroke-dasharray' : remove_quotes,
		'fill' : fill_pre
		};
	
	/**
	 * Functions (placeholders) for parsing stuff from/into CSS.
	 */

	kite9.parse_styles = function(style) {
		var out = {}; // name -> value
		if (typeof(style) == 'string') {
			$.each(style.split(";"), function(k, v) {
				var cp = v.indexOf(":");
				if (cp != -1) {
					var name = v.substring(0, cp).trim();
					var value = v.substring(cp + 1).trim();
					var pre = PRE_PROCESSORS[name];
					if (pre) {
						value = pre(value);
					}
					out[name] = value;
				}
			});
		} else if (typeof(style) == 'object') {
			return style;
		}

		return out;
	}

	kite9.pack_styles = function(a) {
		var out = '';
		$.each(a, function(name, value) {
			var post = POST_PROCESSORS[name];
			if (post) {
				value = post(value);
			}
			out = out + name + ": " + value + ";";
		});

		return out;
	}
	
	
	
	function get_value(id) {
		if (id.indexOf("fill")!==-1) {
			var val = $(id).val();
			if (val == 'solid') {
				val = $(id+"-2").val();
			}
			return val;
		} else if ($(id).is(':input')) {
			return  $(id).val();
		} else if ($(id).hasClass('ui-buttonset')) {
			return $(id).children(":radio:checked").val();
		} else {
			return $(id).text().trim();
		}
		
	}
	

	function set_value(id, val) {
		if ($(id).hasClass("spectrum")) {
			$(id).spectrum("set", val);
		} else if (id.indexOf("fill")!==-1) {
			if ((val.indexOf("-") > -1) || (val == '')) {
				if ($(id).children('[value="'+val+'"]').size() == 0) {
					$(id).val("");
				} else {
					$(id).val(val);
					$(id+"-2").spectrum("disable");
				}
			} else { 
				$(id).val("solid");
				$(id+"-2").spectrum("enable").spectrum("set", val);
			}
		} else if ($(id).is(':input')) {
		  $(id).val(val);
		} else if ($(id).hasClass('ui-buttonset')) {
			$(id).children(":radio").prop("checked", false);
			$(id).children("[value='"+val+"']").prop("checked", true);
		} else if ($(id).hasClass('fontSelect')) {
			// for font-selector
			$(id).css('font-family', val).children("span").text(val);
		}
		
	}
	
	function set_shape(id, target) {
		var value = ''+get_value(id);
		if (has_been_set(id)) {
			$(target).attr("shape", value);
		}
	}
	
	function has_been_set(id) {
		var setIndicator = $(id+"-cancel");
		var style = setIndicator.attr("style");
		return ((style == undefined) || (style.indexOf('none') == -1));
	}
	
	function set_style(prefix, attributes, target) {
		var style= $(target).attr("style");
		var parsed = kite9.parse_styles(style); 
		
		$.each(attributes, function(k, name) {
			var id = '#'+prefix+name;
			var value = ''+get_value(id);
			if (has_been_set(id)) {
				if (value != '') {
					parsed[name] = value;					
				} else {
					delete parsed[name];
				}
			}
		});
		
		var packed = kite9.pack_styles(parsed);
		$(target).attr("style", packed);
	}
	
	/**
	 * call this to update the screen furniture if any style changes.
	 */
	function style_changed() {
		set_style('sc_label_', TEXT_ATTRIBUTES, $('#sc_label_sample'));
		set_style('sc_type_', TEXT_ATTRIBUTES, $('#sc_type_sample'));
		$('#sc_label_sample').css("color", $('#sc_label_fill').val());
		$('#sc_type_sample').css("color", $('#sc_type_fill').val());
	}


	
	function load_style(prefix, attributes, target, base_style) {
		var style= $(target).attr("style");
		var parsed = {};
		jQuery.extend(parsed, base_style, kite9.parse_styles(style)); 
		
		$.each(attributes, function(k, name) {
			var id = '#'+prefix+name;
			var val = parsed[name];
			if (val == undefined) {
				val = '';
			}
			set_value(id, val);
		});
		
		return parsed;
	}
	
	
	/**
	 * Clears out all the previously set values in the palette
	 */
	kite9.init_palette = function() {
		var memento = kite9.main_control.objectMap[kite9.lastSelectedId];
		
		load_style('sc_', MAIN_ATTRIBUTES, memento.definition, memento.style.attr);
		var active = 0;
		var path = $(memento.location).children("path").size() > 0; 
		if (path) {
			$( "#sc-tabs").tabs("enable", 0);
			$( "#sc-tabs").tabs("enable", 1);
		} else {
			$( "#sc-tabs").tabs("disable", 0);
			$( "#sc-tabs").tabs("disable", 1);
			active = 2;
		}
		
		if (memento.type == 'link') {
			$( "#sc-tabs").tabs("disable", 1);  //fill
			$( "#sc-tabs").tabs("disable", 4);  //shape
		}
		
		if (memento.labelStyle) {
			var from = $(memento.definition).children("label,text,bodyText");
			var style = load_style('sc_label_', TEXT_ATTRIBUTES, from, memento.labelStyle.attr);
			style['color'] = style['fill'];
			var packed = kite9.pack_styles(style);
			$('#sc_label_sample').attr('style', packed).text(from.text().trim());
			$( "#sc-tabs").tabs("enable", 2);
		} else {
			$( "#sc-tabs").tabs("disable", 2);
			if (active == 2) {
				active ++;
			}
		}
		if (memento.typeStyle) {
			var from = $(memento.definition).children("stereotype,boldText");
			var style = load_style('sc_type_', TEXT_ATTRIBUTES, from, memento.typeStyle.attr);
			style['color'] = style['fill'];
			var packed = kite9.pack_styles(style);
			$('#sc_type_sample').attr('style', packed).text(from.text().trim());
			$( "#sc-tabs").tabs("enable", 3);
		} else {
			$( "#sc-tabs").tabs("disable", 3);
		}

		if ((memento.type == 'glyph') || (memento.type=='arrow') || (memento.type == 'context')) {
			var shapeDropdown = $('#sc_shape');
			shapeDropdown.children().remove();
			shapeDropdown.append('<option value="">Default</option>');
			$.each(kite9.get_stylesheet().flexibleShapes[0], function (name, value) {
				if ((memento.type != 'context') || (value.context)) {
					shapeDropdown.append('<option value="'+name+'">'+name+'</option>');
				}
			})
			
			shapeDropdown.val($(memento.definition).attr("shape"));
			$( "#sc-tabs").tabs("enable", 4);
		} else if (memento.type == 'link') {
			var shapeDropdown = $('#sc_shape');
			shapeDropdown.children().remove();
			shapeDropdown.append('<option value="">Default</option>');
			
			$.each(kite9.get_stylesheet().linkStyles[0], function (name, value) {
				shapeDropdown.append('<option value="'+name+'">'+name+'</option>');
			})
			
			shapeDropdown.val($(memento.definition).attr("shape"));
			$( "#sc-tabs").tabs("enable", 4);
		} else {
			$( "#sc-tabs").tabs("disable", 4);			
		}
		
		$( "#sc-tabs").tabs("option", "active", active);

		$('.sc-cancel').hide();
		
		$('#sc').dialog("open");
	}
	
}

export default setup_style_chooser;