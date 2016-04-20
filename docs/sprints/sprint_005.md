# 19th April 2016: Sprint 5: D3 To Load XML

- We should be able to render the XML returned by passing it through a simple d3 component which turns it into SVG.
- Every item from the object model will be a group, which will potentially have some svg elements associated with it.
- Using D3 to display on the screen.
- This should be a simple drop-in replacement to Raphael, and clear out this tech debt.
- Tests should look like "here's some rendering information, handle it".
- (Bonus Goal) make sure animation still looks good, so handle updates.

## Prelude:  Content Types

Outputting SVG (or a web page containing SVG) is a *Content-Type* issue, and this is supported by the HTTP spec.  We should make
use of this.  The content types related to diagrams in Kite9 are:

 - **text/adl+xml** : Unrendered ADL (Kite9's internal XML Format).  
 - **text/rendered-adl+xml**:  Same as the above, but including rendering information to say where each element goes.
 - **application/pdf**:  PDF type
 - **image/png**: PNG Image format (the usual web format)
 - **image/svg+xml**:  SVG Images
 - **text/html**: Our rendered page, with an SVG image embedded in it.
 
To handle these in Spring, we use a [HttpMessageConverter](http://docs.spring.io/spring/docs/3.0.x/javadoc-api/org/springframework/http/converter/HttpMessageConverter.html)
subtype.  

By default, browsers handle a variety of content types themselves.  So, PNG, SVG and HTML would be handled automatically.  Then it would be up to the browser to ask 
for the right content type beyond this.

## Second Prelude: JavaScript

One of the reasons I'm doing this part of the project *right now* is that I want to get the JavaScript libraries sorted out properly.   I am going to use
`maven-frontend-plugin`, which combines several technologies:  Node.js, NPM, Webpack, and Karma.   I'm using these all at work as it is, and I'm going to now port them 
into this project to use.

This will actually be the main bulk of the project, I think.  

Step 1: Rendering XML

For this we need two classes to encapsulate the XML, and therefore the content types.  To do all of that, I need an `ADLMessageConverter`,
and a class to hold the XML:

```java
public interface ADL {

	MediaType getMediaType();
	
	Diagram getAsDiagram();
	
	String getAsXMLString();

}
```

Implementation for this below.  You can initialize with either the xml or the `Diagram` object, and both getters will work:\\

```java
public class ADLImpl implements ADL {
		
	private Diagram diagram;
	private String xml;
	private final MediaType mt;
	
	public ADLImpl(String content, MediaType mt) {
		this.xml = content;
		this.mt = mt;
	}
	public ADLImpl(Diagram content, MediaType mt) {
		this.diagram = content;
		this.mt = mt;
	}

	@Override
	public Diagram getAsDiagram() {
		if (diagram == null) {
			diagram = (Diagram) new XMLHelper().fromXML(xml);
		}
		
		return diagram;
	}

	@Override
	public MediaType getMediaType() {
		return mt;
	}
	@Override
	public String getAsXMLString() {
		if (xml == null) {
			xml = new XMLHelper().toXML(diagram);
		}
		
		return xml;
	}
	
}

### Media Types

And here are the `MediaType`s we are going to convert from/to:

```java
public class MediaTypes {

	public static final MediaType SVG = new MediaType("image", "svg+xml");
	public static final MediaType PDF = new MediaType("application", "pdf");
	public static final MediaType ADL_XML = new MediaType("text", "adl+xml");
	public static final MediaType RENDERED_ADL_XML = new MediaType("text", "rendered-adl+xml");
	public static final MediaType CLIENT_SIDE_IMAGE_MAP = new MediaType("text", "html-image-map");
	
}
```

The rest are available in the `MediaType` class already (e.g. PNG).

### The Converter

Ok, so now we need to implement the `ADLMessageConverter`:

```java
@Component
public class ADLMessageConverter extends AbstractHttpMessageConverter<ADL>{
	/**
	 * This is the list of media types we can support writing.
	 */
	public ADLMessageConverter() {
		super(MediaTypes.ADL_XML, MediaTypes.RENDERED_ADL_XML, MediaType.IMAGE_PNG, MediaTypes.SVG, MediaTypes.PDF, MediaType.TEXT_HTML);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return ADL.class.isAssignableFrom(clazz);
	}
	
	/**
	 * List of things we can read in is much more limited than things we can write back out - just the XML formats, basically.
	 */
	@Override
	protected boolean canRead(MediaType mediaType) {
		return MediaTypes.ADL_XML.includes(mediaType) || MediaTypes.RENDERED_ADL_XML.includes(mediaType);
	}
```

So *reading* the POST body is much more limited, (to an XML format), and looks like this:

```java
	@Override
	protected ADL readInternal(Class<? extends ADL> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		MediaType mt = inputMessage.getHeaders().getContentType();
		Charset charset = mt.getCharSet();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
		StreamHelp.streamCopy(inputMessage.getBody(), baos, true);
		String s = baos.toString(charset.name());
		return new ADLImpl(s, mt);
	}
```

Writing is more complex.  If the client asks for unrendered XML, we simply return it.  Otherwise, we need to 
render any unrendered XML, and then select the correct `Format` object, and output it.  `Format` is a bit
of already-built Kite9 code that can render diagrams into the different formats.  

```java
	@Override
	protected void writeInternal(ADL t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MediaType contentType = outputMessage.getHeaders().getContentType();
		Charset charset = contentType.getCharSet() == null ? Charset.forName("UTF-8") : contentType.getCharSet();
		String stylesheet = StylesheetProvider.DEFAULT;  (1)
		
		if (MediaTypes.ADL_XML.isCompatibleWith(contentType)) { (2)
			outputMessage.getBody().write(t.getAsXMLString().getBytes(charset));
			return;
		}
			
		try {
			Diagram d = t.getAsDiagram();
			if (MediaTypes.ADL_XML.isCompatibleWith(t.getMediaType())) {
				// unrendered, so render.
				d = arranger.arrangeDiagram(d, stylesheet); (3)
			}

			Format f = formatSupplier.getFormatFor(contentType);  (4)
			Stylesheet ss = StylesheetProvider.getStylesheet(stylesheet);
			
			f.handleWrite(d, outputMessage.getBody(), ss, true, null, null);  (5)
		} catch (Exception e) {
			throw new HttpMessageNotReadableException("Caused by: "+e.getMessage(), e);
		}
		
	}
```

1.  Stylesheet is currently set to default.  Eventually it will be part of the Diagram object/xml.
2.  This returns unrendered XML, if that is what is asked for.
3.  Arranges the diagram.  This adds `RenderingInformation` objects to each element, telling them where they need to be placed.
4.  We choose the `Format` instance based on the requested Content-Type.
5.  Writes to the stream using the chosen `Format` instance.

### The Controller

Is ridiculously simple:  we are receiving POSTed XML in the HTTP Request, and then outputting it again in another format:

```java
@Controller
public class RenderingController {

	@RequestMapping(path="/api/renderer")
	public @ResponseBody ADL echo(@RequestBody ADL input, @RequestHeader HttpHeaders headers) {
		return input;
	}
}
```

Obviously, this will change in the future, and we will do caching, etc.

### Testing

I just wrote a simple test which creates a `Diagram` object, and then POSTs it.  The response is examined to check that
actually it's in the right format, and is passably likely to contain what I asked for. For example:

```java
public class RestRenderingIT extends AbstractAuthenticatedIT {
	
	private static final int EXPECTED_HEIGHT = 204;
	public static final int EXPECTED_WIDTH = 264;

	protected byte[] withBytesInFormat(MediaType output) throws URISyntaxException {
		String xml = createDiagramXML();
		HttpHeaders headers = createKite9AuthHeaders(u.getApi(), MediaTypes.ADL_XML, output);
		RequestEntity<String> data = new RequestEntity<String>(xml, headers, HttpMethod.POST, new URI(urlBase+"/api/renderer"));
		byte[] back = getRestTemplate().exchange(data);
		return back;
	}

	@Test
	public void testPNGRender() throws URISyntaxException, IOException {
		byte[] back = withBytesInFormat(MediaType.IMAGE_PNG);
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(back));
		Assert.assertEquals(EXPECTED_WIDTH, bi.getWidth());
		Assert.assertEquals(EXPECTED_HEIGHT, bi.getHeight());
	}
	
```

## Step 2:  A Web Page

Now that we have plumbed in the original Kite9 Visualization, and got it working, we need to produce a page
containing our diagram, rendered as SVG.  

To do this, I am going to use a React.js component, which will draw on the screen, and then render the D3 contained
within it.


