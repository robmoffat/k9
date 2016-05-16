# Date TBD:  Sprint 7: Server-Side CSS #

- Modify XML Loader so that elements are annotated with CSS Attributes
- Get the CSS loaded up on the server side by Batik.
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