package com.kite9.k9server.adl.arranger;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.position.RenderingInformation;

public interface DiagramArranger {

	/**
	 * Returns a diagram annotated with {@link RenderingInformation}, from which a 
	 * graphic can be generated.
	 * @throws Exception 
	 */
	public Diagram arrangeDiagram(Diagram input, String stylesheetName) throws Exception;
} 
