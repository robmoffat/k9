package com.kite9.k9server.adl;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.kite9.diagram.visualization.display.style.sheets.BasicBlueStylesheet;
import org.kite9.diagram.visualization.display.style.sheets.BasicStylesheet;
import org.kite9.diagram.visualization.display.style.sheets.CGWhiteStylesheet;
import org.kite9.diagram.visualization.display.style.sheets.Designer2012Stylesheet;
import org.kite9.diagram.visualization.display.style.sheets.DesignerStylesheet;
import org.kite9.diagram.visualization.display.style.sheets.OutlinerStylesheet;

/**
 * Creates one-time stylesheets for rendering and arranging.
 * 
 * @author robmoffat
 *
 */
public class StylesheetProvider {

	public static final String DEFAULT = "designer2012";
	public static final String BASIC = "basic";
	public static final String DESIGNER2012_LINK = "designer2012-link";


	protected static Map<String, Class<? extends Stylesheet>> stylesheet;
	
	private static void addStylesheet(Stylesheet ss) {
		stylesheet.put(ss.getId(), ss.getClass());
	}
	
	static {
		stylesheet = new HashMap<String, Class<? extends Stylesheet>>();
		addStylesheet(new BasicStylesheet());
		addStylesheet(new CGWhiteStylesheet());
		addStylesheet(new BasicBlueStylesheet());
		addStylesheet(new OutlinerStylesheet());
		addStylesheet(new DesignerStylesheet());
		addStylesheet(new Designer2012Stylesheet());
		addStylesheet(new Designer2012Stylesheet() {

			@Override
			public String getId() {
				return DESIGNER2012_LINK;
			}

			/**
			 * For rendering links in menu items, we use a transparent background.
			 */
			@Override
			public Paint getBackground() {
				return new Color(1f, 1f, 1f, 0);
			}
		});
	}
	

	public static Stylesheet getStylesheet(String style) throws InstantiationException, IllegalAccessException {
		Class<? extends Stylesheet> out = stylesheet.get(style);
		if (out == null) {
			out = stylesheet.get(BASIC);
		}

		return (Stylesheet) out.newInstance();
	}
}
