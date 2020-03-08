package com.kite9.k9server;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;

import org.apache.commons.io.input.ReaderInputStream;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonListener;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DOMDifferenceEngine;

public class XMLCompare {

	public static void compareXML(String a, String b) {
		DOMDifferenceEngine diff = new DOMDifferenceEngine();

		diff.addDifferenceListener(new ComparisonListener() {

			public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
				String item = comparison.getControlDetails().getValue().toString();
				if ((!item.contains("file:") && (!item.contains("http:"))) 
						&& (comparison.getType() != ComparisonType.XML_ENCODING)) {
					Assert.fail("found a difference: " + comparison);
				}
			}
		});

		diff.compare(stringToDom(a), stringToDom(b));
	}

	public static Source stringToDom(String a) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.parse(new ReaderInputStream(new StringReader(a)));
			return Input.fromNode(d).build();
		} catch (Exception e) {
			throw new RuntimeException("Couldn't create DOM Source", e);
		}
	}
}
