/**
 * Functions for programmatically building a javascript form.
 * 
 * general shape of this:
 * 
 * formElement(placeholder, value, {attName: value, class: classes}, contents)
 * 
 * id and name will be the same, based on the placeholder.
 * 
 * returns:  a div containing the form elements.
 * 
 * These are to be used with the ContextMenu class for building the menu.
 * 
 */
import { ensureJs, ensureCss } from '/public/bundles/ensure.js';

ensureCss('/public/external/normform-2.0.css');
ensureCss('/public/external/hint-2.6.0.css');

export function icon(id, title, image, onClick) {
	 var a = create('a', {'class': 'hint--bottom hint--bounce', 'aria-label': title, 'id': id}, [
		 create('img', { 'src': image })
	 ]);
     a.addEventListener("click", onClick);
     return a;
}


export function form(contents) {
	return create("form", {"class": "normform", "style": "background: #fff; "}, contents);
}

export function fieldset(legend, contents){
	return create("fieldset", {}, [ create("legend", {}, [ txt(legend) ] ), ...contents] )
}

export function text(placeholder, value, atts) {
	return input(placeholder, 'text', value, atts);
}

export function number(placeholder, value, atts) {
	return input(placeholder, 'number', value, atts);
}

export function email(placeholder, value, atts) {
	return input(placeholder, 'email', value, atts);
}

export function password(placeholder, value, atts) {
	return input(placeholder, 'password', value, atts);
}

export function select(placeholder, value, atts, options) {
	var id = idFrom(placeholder);
	return create('div', {}, [
		create('label', { "for" : id }, [ txt(placeholder) ]),
		create('div', { 'class' : 'select-dropdown' }, [
			create('select', {'id': id, 'name': name}, 
				options.map((o, i) => create('option', 
						{'selected' : (value != undefined) ? (value==o) : (i==0) }, [txt(o)])))
		])
	]);
	
}

export function inlineButtons(buttons) {
	return create('div', {'class' : 'inline-buttons' }, buttons);
}

export function ok(placeholder, atts, callback) {
	var id = idFrom(placeholder);
	return create("input", {...atts, 'type': 'submit', 'name' : id, 'id' : id, 'value': placeholder}, [ txt(placeholder)]);
}

export function cancel(placeholder, atts, callback) {
	var id = idFrom(placeholder);
	return create("input", {...atts, 'type': 'reset', 'name' : id, 'id' : id, 'value': placeholder}, [ txt(placeholder)]);
}

export function checkbox(placeholder, value, atts) {
	const id = idFrom(placeholder);
	return create("div", {}, [
		create('input', {...atts, 'type' : 'checkbox', 'id': id, 'value' : value}),
		create('label', {'for' : id}, [
			txt(placeholder)
		])
	]);
}

export function radios(placeholder, value, atts, options) {
	var id = idFrom(placeholder);
	return create('div', {}, options.map(o => {
		var oid = idFrom(o);
		return create('div', {}, [
			create('input', {'type': 'radio', 'id': oid, 'name': id }),
			create('label', {'for': oid } , [ txt(o) ])
		])
	}));
}


function input(placeholder, type, value, atts) {
	var id = idFrom(placeholder);
	return create('div', {}, [
		create('label', {"for" : id}, [ txt(placeholder)]),
		create('input', { ...atts, 'class' : 'form-control', 'placeholder': placeholder, 'type': type, 'value': value, 'id': id, 'name': id })
	]);
}

function idFrom(str) {
	return str.replace(/\W/g, '');
}

function objectEach(m, action) {
	for (var key in m) {
	    // skip loop if the property is from prototype
	    if (m.hasOwnProperty(key)) {
	    	const val = m[key];
	    	if (val) {
		        action(key, val);
	    	}
	    }
	}	
}
	
function txt(str) {
	var e = document.createTextNode(str);
	return e;
}

function create(tag, atts, contents) {
	var e = document.createElement(tag);
	objectEach(atts, (k, v) => e.setAttribute(k, v));
	
	if (contents) {
		contents.forEach(c => e.appendChild(c));
	}
	
	return e;
}