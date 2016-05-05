# 19th April 2016: Sprint 5: D3 To Load XML

- We should be able to render the XML returned by passing it through a simple d3 component which turns it into SVG.
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

## Step 1: Rendering XML

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
```

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


http://localhost:8080/api/renderer/test

### Setting Up Maven-Frontend-Plugin

Ok, so broadly I am going to use the [Maven Frontend Plugin](https://github.com/eirslett/frontend-maven-plugin) to handle javascript packages and testing.   It includes `node`, `karma` and `webpack` integration.

```xml
<plugin>
		        <groupId>com.github.eirslett</groupId>
		        <artifactId>frontend-maven-plugin</artifactId>
		        <version>1.0</version>
	            <configuration>
	            	<installDirectory>target/frontend</installDirectory>
			        <workingDirectory>src/main/frontend</workingDirectory>
			    </configuration>
```
This says:

 - Download `node` into the `target/frontend` directory.  
 - Get the list of modules to download from `src/main/frontend/package.json`.

There are now *lots* of packages downloaded, but broadly they fall into a few categories:

 - Things to make testing work (in the `devDependencies` section).
 - Things to make `webpack` work (listed under the webpack dependency)
 - Things to make `react` work (stuff at the top of the dependency list).
 
### Webpack

[Webpack](http://webpack.github.io) is a brain-bending creation that allows you to package up javascript (and css, fonts, etc) into
*bundles*, which can then be served as a single HTTP request.  

ES6 now includes the `import` directive, so, Webpack looks for these and resolves them to build large javascript modules.  The entry-point
of our application will be `app.jsx`, and this will import various other resources like this:

```
import React from 'react';
import { render } from 'react-dom'
import ADLSpace from './adl/components/ADLSpace.jsx'
```

Webpack has a config file `webpack.config.js`, which contains details about the *inputs* and *outputs*, and the transforms that need to apply
in the middle:

```js
module.exports = {
	...
    
    entry: {
    	bundle:  './app.jsx'
    },
    output: {
        path: __dirname + "/../../../target/classes/static/dist",
        filename: "bundle.js"
    },

    module: {
    	loaders: [
    	   ...
    	   { test: /\.less$/, loader: "style!css!less" },
    	   { test: /\.css$/, loader: "style!css" },
    	   { test: /\.(png|woff|woff2|eot|ttf|svg)$/, loader: "url-loader?limit=1000000" } 
    	]    	
    }

}
```

So the loaders are chained together.  The less loader for example chains, `less-loader`, `css-loader` and `style-loader` in that order, 
and bundles the output into the same bundle.js file as everything else goes in.

Note that webpack is building into the `target/classes` directory, which allows us to have a refreshing loader when we run:

```sh
mvn frontend:npm 
```

### Setting up React

`.jsx` is an extension for React files which allows them to include snippets of HTML which will be unpacked into React template code.

So our app.jsx calls this:

```js
render(
		<App />,
		document.getElementById('react')
)
```

Which renders the <App /> react module into the `react` element on the page.   At the moment, <App /> just renders to `Hello World`.

### HTMLFormat

Finally, we are ready to construct the `HTMLFormat` code, which will output our `ADLImpl` as a webpage, which effectively means just outputting this:

```html
<!DOCTYPE html>
	<html>
	<head lang="en">
	    <meta charset="UTF-8"/>
	    <title>Kite9</title>
	</head>
	<body>
		{content}
	
	    <div id="react"></div>

	    <script src="/dist/bundle.js"></script>

	</body>
</html>
```

The built webpack code, containing the `<App />` react template is loaded in from the `/dist/bundle.js` file, and rendered on the screen,
inside the `react` element.

This can be easily run in a browser, and is the default format, since browsers ask for the `text/html` content-type when they request the page.  
Again, I am skipping the choice of view/templating code on the server side as it's just not needed yet.

## Step 3: Further Design Work

### Groups

This is something we can plausibly do now:  each rendering element can be a group.

### Kite9 Control Object

This is the way you register callbacks and things.   We're still going to need this. I think when we set up our ADLSpace object, we should pass
in a set of functions, which register behaviours on this control object.  When we render an ADLSpace, we are going to be doing it sometimes as a full-screen thing, and sometimes as a dialog box for a palette, and sometimes as just a menu page.
So, it makes sense that the behaviours are going to need to be passed in somehow separately.

 i.e. each function is like:

```javascript
function someRegister(control) {
	...
}
```
So, the ADLSpace will create one of these, and then pass it round to load up with behaviours.

### Rendering in the future

*I believe* that we should base rendering entirely of the content of **renderingInformation**.  This way we achieve separation in the gui of 
before and after.  This way, we could take any given tag with an ID, and call render on it.  In fact, I am not going to go 
crazy on rendering client-side for now because of this.  I'm just going to get the basics in place and worry about fine-grained
rendering later, by changing the model. 

So, in this world, rendering information would contain an SVG "payload".  We would construct a group for the id'd element, and then 
dump the SVG payload into the group.  Things like offset / size and so on could also be set here.  I expect there is a way of 
using Batik to handle this outputting.

### D3?  

I am looking less favourably on this now.  I only really need animation - everything else is just straight svg.  I think making the animation in 
some way pluggable is a good idea.  So, I'd like to downplay this and try to do things in basic SVG for the time being.

### With Raphael for now?

Seems like an easier win.  Let's bring this in, and then slowly reactify/webpackify/derephaelify...

### Initial import

So, to start with, I want a react component that actually renders some SVG on the screen.  I'm going to import the old Kite9
code for now, and get this displaying to give a base from which to improve things.

This is the entire component for rendering some SVG:

```js
import React from 'react'
import ReactDOM from 'react-dom'
import jQuery from 'jquery'
import setup_rendering from '../lib/kite9_rendering'
import setup_primitives from '../lib/kite9_primitives'
import setup_style_chooser from '../lib/kite9_style_chooser'

import Raphael from 'raphael'

class ADLSpace extends React.Component {

	render() {
		return (<svg id="ADLSpace" width="1000" height="1000"></svg>)
	}
	
	componentDidMount() {
		var dom = ReactDOM.findDOMNode(this);

		var kite9 = {}; 
		kite9.isTouch = jQuery("html").hasClass("touch");
		jQuery("body").addClass(kite9.isTouch ? "touch" : "mouse");
		setup_primitives(kite9);
		setup_rendering(kite9);
		kite9.main_control = kite9.new_control(Raphael("ADLSpace", 1, 1), dom);
        setup_style_chooser(kite9, kite9.main_control);
		kite9.load(kite9.main_control, "http://localhost:8080/dist/test-card-rendered.xml", undefined);
	}
}


export default ADLSpace
```

Effectively, we are not changing much here: we're still using Raphael, and we're loading the XML from a pre-rendered file (the test card).
I had to add Raphael to the `package.json` (for now), and also create hard-coded copies of the stylesheets for webpack. 

But, it mainly seems to work:

![First laid out page](images/005_01.png)

 - Fonts are wrong (webpack wants to add them to the bundle)
 - Background colours are also not present for some reason.
 
I think I can live with this:  it's a reasonable starting point to begin with removing the Raphael dependency.

## Step 3: Removing Raphael, Tidying Up

So there's still a bit of a mess here:

1.  We've got three large javascript files which control all the loading/updating rendering. 
2.  Two of them are referencing Raphael.
3.  They're all doing lots of XML manipulation, which in the future I think will just *go*.

I need to decide whether to pause this, and do the `renderingInformation` change, or try and pull out the Raphael.  Obviously, doing the R.I. change will *massively*
reduce the amount of XML we are importing, so maybe this is a good idea.

Also, we need to do the grouping change.  But, I definitely want to do that after renderingInformation.  So...
 
### Spike Solution: Rendering SVG Within Kite9 Visualisation

Some of this turned out to be easy:   I used Batik's `SVGGraphics2D` class to create a new SVGRenderer class in Kite9.  What doesn't work:

 - By default, Kite9 renders all the fonts into shapes before adding them to the graphics, so you end up with a much larger
SVG file than you expect (all the paths to describe each character).  
 - Any kind of fill, including background fills
 - Shadows
 - Literally *everything* is encoded on a per-element basis.. stylesheets are completely out-of-the-question.
 
 But on the plus side, it's pretty exact.  By not using text, I wonder if this improves speed?  Hard to say.

So, I could just spend the rest of this sprint sorting this out, and that would be great.  Is this worth doing? I think, yes:
if we can plug this into the rendering information, it's going to simplify things massively, and that's a huge win, 
and it should knock out the Raphael problem at the same time.  (Animation is likely to be made harder though I think).

### Fixing Background Fills + Shadows

 - Batik comes with it's own `LinearGradientPaint`.  Maybe I should use this instead of the AWT one?
 
Trying [this handler](https://gist.github.com/msteiger/4509119) from the internet.  This works really well and solves the issue of Gradient fills not being
supported.  I had to make a couple of changes:

1.  `LinearGradientPaint` is the object used in Java to represent the gradient fill.  However, I wrap one of these so that I can use the same paint for any size of
glyph.  This means it's not a `LinearGradientPaint` when it gets the handler.  So, I unwrap it first.
2.  As a result of (1), I have to modify the handler class to use percentages for gradient start/end points, which is pretty simple.
3.  Naively, each element with a gradient paint gets their gradient converted separately, e.g.

```xml
<linearGradient xmlns="http://www.w3.org/2000/svg" x1="50.0%" x2="50.0%" y1="0.0%" style="color-interpolation:sRGB;" y2="100.0%" id="gradient1" spreadMethod="pad"><stop style="stop-color:rgb(242,242,242);" offset="0%"/><stop style="stop-color:rgb(204,204,204);" offset="100%"/></linearGradient>
<linearGradient xmlns="http://www.w3.org/2000/svg" x1="50.0%" x2="50.0%" y1="0.0%" style="color-interpolation:sRGB;" y2="100.0%" id="gradient1" spreadMethod="pad"><stop style="stop-color:rgb(242,242,242);" offset="0%"/><stop style="stop-color:rgb(204,204,204);" offset="100%"/></linearGradient>
<linearGradient xmlns="http://www.w3.org/2000/svg" x1="50.0%" x2="50.0%" y1="0.0%" style="color-interpolation:sRGB;" y2="100.0%" id="gradient1" spreadMethod="pad"><stop style="stop-color:rgb(242,242,242);" offset="0%"/><stop style="stop-color:rgb(204,204,204);" offset="100%"/></linearGradient>
...
```

This is pointless duplication, so I introduced a cache.  But there is something called `GradientPaintValueManager` which I wrote to convert between Raphael's idea 
of gradients, and the one in Batik/Java.  So, this will need to be removed at some point soon.

What's the right way to do this?  I guess, if we are using CSS generally, then we will need some CSS for fill gradients, and Raphael's approach is both working and OK.
So, maybe we're barking up the wrong tree by removing this.  For now, I've added the fill property as the key for the cache, so that we can cache the gradients.  This fixes the problem, but 
on the whole this bit is messy now.

### Fixing the TestCard

 - Stuff seems to be offset incorrectly is all. This turned out to be because we had zero-size connection 
 bodies in the diagram, which, if you try to draw them cause the masks to go out of whack.  Easily fixed.

### Adding the SVG To The Rendering Information


### Rendering it on-screen




 




