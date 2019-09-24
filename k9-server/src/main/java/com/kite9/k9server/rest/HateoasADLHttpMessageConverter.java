package com.kite9.k9server.rest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;

import org.codehaus.stax2.XMLStreamWriter2;
import org.kite9.diagram.dom.ADLExtensibleDOMImplementation;
import org.kite9.diagram.dom.XMLHelper;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.security.Kite9HeaderMeta;

/**
 * Handles conversion of the Hateoas {@link ResourceSupport} objects to ADL, and therefore HTML, SVG etc..
 * All of the Kite9 domain objects extend {@link ResourceSupport}, and lists of them 
 * implement {@link org.springframework.hateoas.Resources}
 * 
 * @author robmoffat
 *
 */
public class HateoasADLHttpMessageConverter 
	extends AbstractGenericHttpMessageConverter<ResourceSupport> {

	public static final HttpHeaders EMPTY_HEADERS = new HttpHeaders();
	public static final Charset DEFAULT = Charset.forName("UTF-8");
	
	private final ObjectMapper objectMapper;
	final private FormatSupplier formatSupplier;
	private String template;
	private XmlFactory xmlFactory;
	private WstxOutputFactory wstxOutputFactory;
		
	public HateoasADLHttpMessageConverter(ObjectMapper objectMapper, FormatSupplier formatSupplier, String template) {
		super();
		this.objectMapper = objectMapper;
		this.formatSupplier = formatSupplier;
		this.template = template;
		setSupportedMediaTypes(formatSupplier.getMediaTypes());
		xmlFactory  = new XmlFactory();
		this.wstxOutputFactory = new WstxOutputFactory();
		this.wstxOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
	}
	
	public ADL createEmptyTemplateDocument(ResourceSupport rs) throws Exception {
		URI u = new URI(rs.getId().getHref());
		ADL container = new ADLImpl(template, u, EMPTY_HEADERS);
		return container;
	}
	
	
	public Element getDiagramElement(ADL container) throws Exception {
		NodeList nl = container.getAsDocument().getRootElement().getElementsByTagName("diagram");
		
		if (nl.getLength() != 1) {
			throw new IllegalArgumentException("Couldn't find single diagram element in template");
		}
		
		Element top = (Element) nl.item(0);
		return top;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return ResourceSupport.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean canRead(MediaType mediaType) {
		return false;	// this is for display formats only.
	}

	@Override
	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
		return false;
	}

	@Override
	public ResourceSupport read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Can't read with this converter");
	}
	
	@Override
	protected ResourceSupport readInternal(Class<? extends ResourceSupport> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Can't read with this converter");
	}


	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
		return super.canWrite(clazz, mediaType);
	}


	@Override
	protected void writeInternal(ResourceSupport t, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MediaType contentType = outputMessage.getHeaders().getContentType();	
		Format f = formatSupplier.getFormatFor(contentType);
		writeADL(t, outputMessage, f);
	}
	
	

	protected void writeADL(ResourceSupport t, HttpOutputMessage outputMessage, Format f) {
		ADL adl = null;
		try {
			adl = createEmptyTemplateDocument(t);
			Element diagramElement = getDiagramElement(adl);
			DOMResult domResult = new DOMResult(diagramElement);
			ToXmlGenerator generator = createXMLGenerator(domResult);

			objectMapper.writeValue(generator, t);
			
			System.out.println(new XMLHelper().toXML(adl.getAsDocument()));

			Kite9HeaderMeta.addUserMeta(adl);
			removeExcessNamespaces(adl.getAsDocument().getDocumentElement(), false);
			f.handleWrite(adl, outputMessage.getBody(), true, null, null);
			 
		} catch (Exception e) {
			if (adl != null) {
				System.out.println(new XMLHelper().toXML(adl.getAsDocument()));
			}
			throw new HttpMessageNotWritableException("Caused by: "+e.getMessage(), e);
		}
	}

	/**
	 * Fixing a bug in woodstox that means nearly every element gets a namespace declaration.
	 */
	private void removeExcessNamespaces(Element e, boolean remove) {
		boolean found = e.hasAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE) 
				&& e.getAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE).equals(XMLHelper.KITE9_NAMESPACE);
		
		if (found && remove) {
			e.removeAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);
		}
		
		NodeList ch = e.getChildNodes();
		for (int i = 0; i < ch.getLength(); i++) {
			if (ch.item(i) instanceof Element) {
				removeExcessNamespaces((Element) ch.item(i), found || remove);
			}
		}
	}

	/** 
	 * Way to convert JSON output to XML
	 */
	protected ToXmlGenerator createXMLGenerator(DOMResult domResult) throws XMLStreamException, IOException {
		XMLStreamWriter streamWriter = wstxOutputFactory.createXMLStreamWriter(domResult);
		streamWriter.setDefaultNamespace(XMLHelper.KITE9_NAMESPACE);
		streamWriter.setPrefix("", XMLHelper.KITE9_NAMESPACE);
		
		ToXmlGenerator generator = xmlFactory.createGenerator(streamWriter);
		
		// disable pretty-printing with DOM Write
		DefaultXmlPrettyPrinter pp = new DefaultXmlPrettyPrinter() {
			@Override
			public void writePrologLinefeed(XMLStreamWriter2 sw) throws XMLStreamException {
			}
		};
		pp.spacesInObjectEntries(false);
		pp.indentArraysWith(null);
		pp.indentObjectsWith(null);
		generator.setPrettyPrinter(pp);
		
		// set top-level element name
		generator.setNextName(new QName(XMLHelper.KITE9_NAMESPACE, "entity", ""));
		return generator;
	}


	@Override
	protected void addDefaultHeaders(HttpHeaders headers, ResourceSupport t, MediaType contentType) throws IOException {
		super.addDefaultHeaders(headers, t, contentType);
		Kite9HeaderMeta.addUserMeta(headers);
	}


	
}
