package com.kite9.k9server.adl;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.batik.svggen.SVGFont;
import org.kite9.diagram.visualization.display.style.LocalFont;
import org.kite9.diagram.visualization.display.style.OverrideableAttributedStyle;
import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.kite9.diagram.visualization.display.style.sheets.AbstractStylesheet;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Outputs Font information in a CSS stylesheet format.  
 * 
 * @author robmoffat
 * 
 */
@Controller
public class FontController {
	
	public static final String BASIC = "basic";
	
	@RequestMapping("/api/renderer/fonts/{name}.ttf")
	public void font(@PathVariable("name") String name, OutputStream os) throws IOException {
		InputStream is = AbstractStylesheet.getFontStream(name);
		StreamUtils.copy(is, os);
	}
	
	@RequestMapping("/api/renderer/stylesheet.css")
	public void guiStylesheetCss(@RequestParam(value = "name", required = false) String name,
			final HttpServletResponse sr) throws IOException, InstantiationException, IllegalAccessException {
		Stylesheet ss = StylesheetProvider.getStylesheet(name);

		Map<String, ? extends Font> textStyles = ss.getFontFamilies();
		sr.setContentType("text/css");
		Writer w = sr.getWriter();

		for (Map.Entry<String, ? extends Font> e : textStyles.entrySet()) {
			Font f = e.getValue();
			String family = SVGFont.familyToSVG(f);
			String weight = SVGFont.weightToSVG(f);
			String style = SVGFont.styleToSVG(f);
			w.write("@font-face { \n");
			w.write("\tfont-family: " + family +";\n");
			w.write("\tfont-weight: "+weight+";\n");
			w.write("\tfont-style: "+style+";\n");

			if (f instanceof LocalFont) {
				w.write("\tsrc: url('/api/renderer" + ((LocalFont) f).getFontFileName() + "') format('truetype');\n");
			}

			w.write("}\n\n");
		}
		w.flush();
		w.close();
	}

	public static String[] STRIP_QUOTES = new String[] { "fill", "stroke" };

	protected void outputAttributes(OverrideableAttributedStyle d, HierarchicalStreamWriter writer) {
		if (d == null) {
			return;
		}
		for (Map.Entry<String, String> entry : d.getElements().entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (contains(STRIP_QUOTES, name) && (value.startsWith("\""))) {
				value = value.substring(1, value.length() - 1);
			}

			outputEntry(writer, name, value);
		}
	}

	private boolean contains(String[] items, String name) {
		for (String string : items) {
			if (string.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private void outputEntry(HierarchicalStreamWriter writer, String name, String value) {
		try {
			Integer.valueOf(value);
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, Integer.class);
			writer.setValue(value);
			writer.endNode();
			return;
		} catch (NumberFormatException nfe) {
		}
		try {
			Float.valueOf(value);
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, Float.class);
			writer.setValue(value);
			writer.endNode();
			return;
		} catch (NumberFormatException nfe) {
		}

		writer.startNode(name);
		writer.setValue(value);
		writer.endNode();
	}

}
