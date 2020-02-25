package com.kite9.k9server.adl.format.media;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.adl.holder.Payload;

/**
 * Will eventually render the GUI, I guess?  Although, maybe we won't do it this way.
 * 
 * @author robmoffat
 *
 */
public class HTMLFormat implements Format {
	
	private List<byte[]> format;
	
	public HTMLFormat() {
		super();
		String pageTemplate;
		try {
			pageTemplate = StreamUtils.copyToString(this.getClass().getResourceAsStream("/page-template.html"), Charset.defaultCharset());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		format = Arrays.stream(pageTemplate.split("\\{[a-z]+\\}"))
				.map(s -> s.getBytes())
				.collect(Collectors.toList());
	}

	@Override
	public MediaType[] getMediaTypes() {
		return new MediaType[] { MediaType.TEXT_HTML };
	}
	
	@Override
	public void handleWrite(ADL adl, OutputStream baos, boolean watermark, Integer width, Integer height) throws Exception {
		baos.write(format.get(0));	
		for (Map.Entry<String, String> e : adl.getMetaData().entrySet()) {
			baos.write("    <meta property=\"kite9:".getBytes());
			baos.write(e.getKey().getBytes());
			baos.write("\" content=\"".getBytes());
			baos.write(e.getValue().getBytes());
			baos.write("\" />".getBytes());
		}
		baos.write(format.get(1));
		baos.write(getSVGRepresentation(adl));
		baos.write(format.get(2));
	}
	
	/**
	 * Returns just the content element, not the DOCTYPE, PI etc.
	 */
	public byte[] getSVGRepresentation(ADL data) throws Exception {
		Document svg = data.getAsSVGRepresentation();
		Payload.insertEncodedADLInSVG(data, svg);
		Element e = svg.getDocumentElement();
		String xmlString = ADLImpl.toXMLString(e, true);
		return xmlString.getBytes();
	}

	public String getExtension() {
		return "html";
	}

	@Override
	public boolean isBinaryFormat() {
		return false;
	}
}
