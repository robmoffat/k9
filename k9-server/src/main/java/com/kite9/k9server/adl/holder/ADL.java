package com.kite9.k9server.adl.holder;

import org.kite9.diagram.adl.Diagram;

public interface ADL {

	boolean isArranged();
	
	Diagram getAsDiagram();
	
	String getAsXMLString();

}