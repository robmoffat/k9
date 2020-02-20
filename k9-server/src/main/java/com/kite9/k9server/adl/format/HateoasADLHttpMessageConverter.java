package com.kite9.k9server.adl.format;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.utils.DefaultErrorHandler;
import org.codehaus.stax2.XMLStreamWriter2;
import org.kite9.diagram.batik.format.Kite9SVGTranscoder;
import org.kite9.diagram.dom.ADLExtensibleDOMImplementation;
import org.kite9.diagram.dom.XMLHelper;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;
import com.kite9.k9server.adl.format.media.Format;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.entity.RestEntity;
import com.kite9.k9server.security.Kite9HeaderMeta;

/**
 * Handles conversion of the Hateoas {@link ResourceSupport} objects to ADL, and therefore HTML, SVG etc..
 * All of the Kite9 domain objects extend {@link ResourceSupport}, and lists of them 
 * implement {@link org.springframework.hateoas.Resources}
 * 
 * @author robmoffat
 *
 */
@Component
public class HateoasADLHttpMessageConverter 
	extends AbstractGenericHttpMessageConverter<RepresentationModel<?>> {

	public static final HttpHeaders EMPTY_HEADERS = new HttpHeaders();
	public static final Charset DEFAULT = Charset.forName("UTF-8");
		
	private final ObjectMapper objectMapper;
	final private FormatSupplier formatSupplier;
	private XmlFactory xmlFactory;
	private WstxOutputFactory wstxOutputFactory;
	private ResourceLoader resourceLoader;
	private String resource = "clas";
	private TransformerFactory transFact;
	 
	public HateoasADLHttpMessageConverter(
			ObjectMapper objectMapper, 
			FormatSupplier formatSupplier, 
			ResourceLoader resourceLoader,
			@Value("${kite9.rest.transform:classpath:/static/public/context/admin/transform.xslt}") String resource) {
		super();
		this.objectMapper = objectMapper;
		this.formatSupplier = formatSupplier;
		this.xmlFactory  = new XmlFactory();
		this.wstxOutputFactory = new WstxOutputFactory();
		this.wstxOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
		this.resourceLoader = resourceLoader;
		this.resource = resource;
		this.transFact = TransformerFactory.newInstance();
		this.transFact.setErrorListener(new DefaultErrorHandler(true));
		setSupportedMediaTypes(formatSupplier.getMediaTypes());
	}
	
	@Override
	protected boolean supports(Class<?> clazz) {
		return RepresentationModel.class.isAssignableFrom(clazz);
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
	public RepresentationModel<?> read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Can't read with this converter");
	}
	
	@Override
	protected RepresentationModel<?> readInternal(Class<? extends RepresentationModel<?>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		throw new UnsupportedOperationException("Can't read with this converter");
	}


	@Override
	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
		return super.canWrite(clazz, mediaType);
	}


	@Override
	protected void writeInternal(RepresentationModel<?> t, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		try {
			MediaType contentType = outputMessage.getHeaders().getContentType();	
			Format f = formatSupplier.getFormatFor(contentType);
			writeADL(t, outputMessage, f);
		} catch (Exception e) {
			throw new HttpMessageNotWritableException("Couldn't create REST Response", e);
		}
	}
	
	

	protected void writeADL(RepresentationModel<?> t, HttpOutputMessage outputMessage, Format f) throws Exception {
		URI u = new URI(getSelfRef(t));
		Kite9SVGTranscoder transcoder = new Kite9SVGTranscoder();
		Document input = generateRestXML(t, transcoder.getDomImplementation());
		ADLDocument output = transformXML(input, transcoder.getDomImplementation());
		System.out.println("IN: " + new XMLHelper().toXML(input));
		ADL out = ADLImpl.domMode(u, transcoder, output, EMPTY_HEADERS);
		
		System.out.println("OUT: "+ out.getAsADLString());
		Kite9HeaderMeta.addRegularMeta(out, u.toString(), getTitle(t));
		f.handleWrite(out, outputMessage.getBody(), true, null, null);
	}

	private String getSelfRef(RepresentationModel<?> t) {
		return t.getLink(IanaLinkRelations.SELF).orElseThrow(
			() -> new Kite9ProcessingException("Couldn't get url for "+t))
				.getHref();
	}

	protected Document generateRestXML(RepresentationModel<?> t, DOMImplementation dom) throws XMLStreamException, IOException, JsonGenerationException, JsonMappingException {
		Document out = dom.createDocument(XMLHelper.KITE9_NAMESPACE, null, null);
		DOMResult domResult = new DOMResult(out);
		ToXmlGenerator generator = createXMLGenerator(domResult);
		objectMapper.writeValue(generator, t);
		removeExcessNamespaces(out.getDocumentElement(), false);
		return out;
	}
	
	public ADLDocument transformXML(Document in, ADLExtensibleDOMImplementation dom) throws Exception {
		// load the transform document
		InputStream is = resourceLoader.getResource(resource).getInputStream();
		Source xsltSource = new StreamSource(is);
		Source inSource = new DOMSource(in);
		ADLDocument out = (ADLDocument) dom.createDocument(XMLHelper.KITE9_NAMESPACE, null, null);
        DOMResult result = new DOMResult(out);

        // the factory pattern supports different XSLT processors
        Transformer trans = transFact.newTransformer(xsltSource);
        trans.transform(inSource, result);
        return out;
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
	protected void addDefaultHeaders(HttpHeaders headers, RepresentationModel<?> t, MediaType contentType) throws IOException {
		super.addDefaultHeaders(headers, t, contentType);
		Kite9HeaderMeta.addRegularMeta(headers, getSelfRef(t), getTitle(t));
	}

	private String getTitle(RepresentationModel<?> rs) {
		if (rs instanceof RestEntity) {
			return ((RestEntity) rs).getType()+ ": " + ((RestEntity) rs).getTitle();
		}
		
		return "";
	}
	
}
