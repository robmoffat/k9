package com.kite9.k9server.adl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.adl.Link;
import org.kite9.diagram.primitives.Connected;
import org.kite9.diagram.primitives.Connection;
import org.kite9.diagram.primitives.DiagramElement;
import org.kite9.diagram.visitors.DiagramElementVisitor;
import org.kite9.diagram.visitors.VisitorAction;
import org.kite9.framework.serialization.XMLHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.io.Resources;
import com.kite9.k9server.adl.format.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Handles rendering of Kite9 ADL content.
 * 
 * @author robmoffat
 *
 */
@Controller
public class RenderingController {
	
	@RequestMapping(path="/api/renderer", 
		consumes= {MediaTypes.ADL_XML_VALUE, MediaTypes.ARRANGED_ADL_XML_VALUE})
	public @ResponseBody ADL echo(@RequestBody ADL input) {
		return input;
	}
	
	@RequestMapping(path="/api/renderer/test")
	public @ResponseBody ADL testCard(@RequestHeader HttpHeaders headers) throws IOException {
		MediaType expected = headers.getAccept().get(0);
		String xml = Resources.toString(this.getClass().getResource("/test-card.xml"), Charset.defaultCharset());
		return new ADLImpl(xml, expected);
	}
	
	@RequestMapping(path="/api/renderer/random")
	public @ResponseBody ADL testCardRandom(@RequestHeader HttpHeaders headers) throws IOException {
		MediaType expected = headers.getAccept().get(0);
		String xml = Resources.toString(this.getClass().getResource("/test-card.xml"), Charset.defaultCharset());
		XMLHelper xmlHelper = new XMLHelper();
		Diagram d = (Diagram) xmlHelper.fromXML(xml);
		
		removeOldLinks(d);
		List<Connected> c = getConnecteds(d);
		Random r = new Random();
		for (int i = 0; i < 7; i++) {
			Connected from = c.get(r.nextInt(c.size()));
			Connected to = c.get(r.nextInt(c.size()));
			
			if (from != to) {
				new Link(from, to);
			}
		}
		
		return new ADLImpl(d, expected);
	}

	public void removeOldLinks(Diagram d) {
		for (Connection l : d.getAllLinks()) {
			l.getFrom().getLinks().remove(l);
			l.getTo().getLinks().remove(l);
		}
		
		d.getLinks().clear();
		d.getAllLinks().clear();
	}
	
	public List<Connected> getConnecteds(Diagram d) {
		Set<Connected> out = new HashSet<Connected>();
		new DiagramElementVisitor().visit(d, new VisitorAction() {
			
			@Override
			public void visit(DiagramElement de) {
				if (de instanceof Connected) {
					out.add((Connected) de);
				}
			}
		});
		
		return new ArrayList<>(out);
	}
	
}
