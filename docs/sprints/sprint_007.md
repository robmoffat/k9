# Date 21st May 2016:  Sprint 7: Server-Side CSS #

- Modify XML Loader so that elements are annotated with CSS Attributes DONE
- Get the CSS loaded up on the server side by Batik.  DONE
- Extend CSS so that we add our new attributes for shapes, etc.
- Remove style information from the java Stylesheet

## Prelude:  Extending Batik

We are already using Batik in two places in Kite9:

 - Handling element styles and overriding
 - Converting to SVG format.
 
It makes sense to double-down and use it's capabilities for CSS parsing and styling of XML documents
generally. 

### `CSSStyleableElement`

This is used by Batik to hold XML that has been styled by a CSS.  So, we are going to need for our 
object model to implement this.  

Likewise, we need to add a `DomExtension` which declares all of the elements in our namespace, and how they
relate to each other.    This is plugged into `ExtensibleDOMImplementation`, which in turn is added to
the `SAXDocumentFactory`.

Realistically, this means that ADL becomes a proper DOM language, and that's going to mean a whole lot of
fixing up code. 

### Returning Styles 

```java
CSSContext ctx = new SVG12BridgeContext(new SomeUserAgent());
CSSEngine engine = ExtensibleDOMImplementation.createCSSEngine(d, ctx)

StyleMap sm = engine.getCascadedStyleMap(someELement, "")
Value v = sm.getValue(SVGCSSEngine.STROKE_WIDTH_INDEX)
```

### Adding CSS Properties to Batik

This is really easy:  just do:

```java
ExtensibleDOMImplementation.registerCustomCSSValueManager(new ValueManager ...

```

And then you have a new CSS property to play with.


### Adding Stylesheets to the document

Create an element that implements `CSSStyleSheetNode`.  It will be able to call:

```java
return getOwnerDocument().getCSSEngine().parseStyleSheet(xx)

### `'style'` Attribute

Implement `SVGStyleable`, with it's `getStyle` method.
```

## Step 1: A New XML Loader

In order to get to the stage where we can style things with Batik, we need to load the XML into a DOM.  So, the great 
thing is about DOM is that you can make it extensible, and use your own objects, using an `ExtensibleDOMImplementation`: 

```java
	public class ADLExtensibleDOMImplementation extends ExtensibleDOMImplementation {

	public ADLExtensibleDOMImplementation() {
		super();
		registerCustomElementFactory(XMLHelper.KITE9_NAMESPACE, "diagram", new ElementFactory() {
			
			public Element create(String prefix, Document doc) {
				Diagram out = new Diagram();
				out.setOwnerDocument(doc);
				return out;
			}
		});
		
		registerCustomElementFactory(XMLHelper.KITE9_NAMESPACE, "glyph", new ElementFactory() {
			
			public Element create(String prefix, Document doc) {
				Glyph g = new Glyph();
				g.setOwnerDocument(doc);
				return g;
			}
		});

		etc.
		
```

### Using DOM in the Object Model

So, this way, I've managed to preserve the existing object model, but use DOM behind the scenes.   This means that
generally speaking, my tests can stay relatively unharmed by all the changes.  The owner document presents a problem, however.

Rather than change all the tests to add this in, I changed `AbstractFunctionalTest` so that it creates a new owner document each time
a test is run:

```java
public class AbstractFunctionalTest extends HelpMethods {

	@Before
	public void initTestDocument() {
		AbstractDiagramElement.TESTING_DOCUMENT =  new ADLDocument();
	}

	...
```

This `TESTING_DOCUMENT` is then used by lots of the constructors that are used by the tests. e.g.:

```java

	public Glyph(String id, String stereotype, String label,  List<CompositionalDiagramElement> text, List<Symbol> symbols) {
		this(id, stereotype, label, text, symbols, false, TESTING_DOCUMENT);
	}

```

A second issue is this:  both the DOM (XML) and the object model are storing state, which is duplication.  To avoid this,
I changed the object model to *just use* the state from the dom.  Where things are attributes, this is easy:

```java

public abstract class AbstractIdentifiableDiagramElement extends AbstractDiagramElement implements IdentifiableDiagramElement, Serializable, StyledDiagramElement {


	public final String getID() {
		return getAttribute("id");
	}
	
	public void setID(String id) {
		setAttribute("id", id);
	}

```

Where they are embedded elements, it's harder.  So, I constructed some helper methods:

```java
public abstract class AbstractDiagramElement extends AbstractElement implements XMLDiagramElement, CompositionalDiagramElement {

	...

	@SuppressWarnings("unchecked")
	public <E extends Element> E getProperty(String name, Class<E> expected) {
		E found = null;
		for (int i = 0; i < getChildNodes().getLength(); i++) {
			Node n = getChildNodes().item(i);
			if ((expected.isInstance(n)) && (((Element)n).getTagName().equals(name))) {
				if (found == null) {
					found = (E) n;
				} else {
					throw new Kite9ProcessingException("Not a unique node name: "+name);
				}
			}
		}
	
		return found;
	}
	
	public <E extends Element> E replaceProperty(String propertyName, E e, Class<E> propertyClass) {
		E existing = getProperty(propertyName, propertyClass);
		if (e == null) {
			if (existing != null) {
				this.removeChild(existing);
			}
		 	return null;
		}

		if (!propertyClass.isInstance(e)) {
			throw new Kite9ProcessingException("Was expecting an element of "+propertyClass.getName()+" but it's: "+e);
		}

		if (e instanceof XMLDiagramElement) {
			((XMLDiagramElement)e).setTagName(propertyName);
			((XMLDiagramElement)e).setOwnerDocument((ADLDocument) this.ownerDocument); 
		}
		
		if (!e.getNodeName().equals(propertyName)) {
			throw new Kite9ProcessingException("Incorrect name.  Expected "+propertyName+" but was "+e.getNodeName());
		}
		
		if (existing != null) {
			this.removeChild(existing);
		}
		
		this.appendChild(e);
		
		return e;
	}
	
```

I was able to replace all of the state get* and set* methods in the other classes using these two.

For lists of items (`Symbol`s, `TextLine`s, etc.) I used a new class, `ContainerProperty`, which implemented
`Iterable`, but extended `AbstractDiagramElement`, so it was a full XML element type.  This meant that persistence
worked correctly.
 
### DOM and Links

Links are a bit more tricky to handle with the DOM.  The references (to/from) are handled with an ID, which has to refer to 
something else in the document.  

This requires a lookup, which is not ideal.  `Edge` objects continue to work with direct java object references, but `Link`s 
extend `AbstractConnection`, which uses the lookup:

```java

public abstract class AbstractConnection extends AbstractIdentifiableDiagramElement implements Connection {

	Element fromEl = getProperty("from", Element.class);
		String reference = fromEl.getAttribute("reference");
		Connected from = (Connected) ownerDocument.getChildElementById(ownerDocument, reference);
		return from;
	}

	public void setFrom(Connected v) {
		Element from = ownerDocument.createElement("from");
		from.setAttribute("reference", v.getID());
		replaceProperty("from", from, Element.class);
		from = v;
	}

	...
```

### Builders

The Java API originally had builder classes, but I removed these for now (along with their tests).  This is just extra 
code to carry round.  I will re-instantiate these as needed.  In any case, the builders were undergoing *a lot* of 
changes in a separate branch, so I don't really care for updating all this now

### Writing XML Out Again

We have two round-trip XML tests: `Test1SerializeDiagram` and `Test4StyledDiagram` (the latter doesn't *actually* seem to do much with styles,
it just has some classes attached to a few nodes.  We need to improve later in the sprint).

Because of the problems with DOM Canonicalization, I found it was necessary to use `xmlunit` to do the comparison (attribute ordering)
and so there is a class called XMLCompare in the same directory as the tests now.

### RenderingInformation

Unfortunately, because of the way the DOM works, we have to construct the object based entirely on the tag.  So, the polymorphism we used to have
around `RenderingInformation` is gone, and I have a single concrete class, `BasicRenderingInformation`.  

This contains the `displayData` stuff from [Sprint 5](sprint_005.md), as well as the elements needed to render routes. It works for now, but it's 
not pretty.

### Fixing Tests

There were lots of broken tests as a result of this massive change.   Most I was able to get working again.  A few I couldn't, but these were mainly 
due to styling anyway, so they'd just get broken when we do the next part of this change.  

### Fixing Background

When we set the size of the background, it seems like the component doesn't change shape to it.  This needs to be sorted out.  Again, I'd rather push
on and break some more things before I fix this.   

## Using CSS Instead Of Java Stylesheets

Ok, so this is the next big change:  we want to use Batik to load the stylesheet information up for a node, from the attached stylesheets.  Broadly,
I want to have each *type of component* (e.g. glyph) having a CSS class, and have the CSS classes listed in a stylesheet.   Then, we can use the CSS stylesheet
attached to the XML document to render the right fonts etc.  

We should be able to do this in the first instance with *existing CSS classes*, and the existing Java stylesheet information (somehow).
