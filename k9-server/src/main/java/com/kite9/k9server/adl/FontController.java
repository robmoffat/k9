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
import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.kite9.diagram.visualization.display.style.sheets.AbstractStylesheet;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

}
