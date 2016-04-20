package com.kite9.k9server.adl.holder;

import org.kite9.diagram.adl.Diagram;
import org.springframework.http.MediaType;

public interface ADL {

	MediaType getMediaType();
	
	Diagram getAsDiagram();
	
	String getAsXMLString();

}