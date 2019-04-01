package com.kite9.k9server.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.kite9.diagram.dom.elements.ADLDocument;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;

public class SetStyle extends AbstractCommand {

	String name, value;
	
	public SetStyle() {
		super();
	}

	public SetStyle(String fragmentId, String fragmentHash, String name, String value) {
		super(fragmentId, fragmentHash);
		this.name = name;
		this.value = value;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "setStyle", "name", name);
		
		ADLDocument doc = adl.getAsDocument();
		validateFragmentHash(adl);
		Element e = findFragmentElement(doc);
		String style = e.getAttribute("style");
		Map<String, String> parsed = parseSimple(style);
		if (value == null) {
			parsed.remove(name);
		} else {
			parsed.put(name, value);
		}
		e.setAttribute("style", compact(parsed));
		
		LOG.info("Processed setAttr of "+fragmentId);
		return adl;
	}
	
	private static Map<String, String> parseSimple(String overrideStyles) {
		String[] decls = overrideStyles.split(";");
		Map<String, String> out = new HashMap<>();
		Arrays.stream(decls).forEach(s -> {
			int colon = s.indexOf(":");
			if (colon != -1) {
				String key = s.substring(0, colon).trim();
				String value = s.substring(colon+1).trim();
				out.put(key, value);
			}
		});
		
		return out;
	}
	
	private static String compact(Map<String, String> in) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> me : in.entrySet()) {
			sb.append(me.getKey());
			sb.append(": ");
			sb.append(me.getValue());
			sb.append("; ");
		}
		
		return sb.toString();
	}
	
}
