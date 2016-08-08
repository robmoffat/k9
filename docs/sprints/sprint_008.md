# 8 August 2016 : Sprint 8 - Overhaul Object Model - Containers

- everything should be (`DiagramElement`s) and `Container`s.  Links should be reformatted.   
- Ideally, we are backwards-compatible with what came before.  So, you can load up the original diagram xml
- Containers are things within DiagramElements.
- ComposedDiagramElements (with type = glyph-simple, glyph-with-stereo etc.)

## Prelude: So, what changes do I need to see:

0.  I need a set of deterministic tests, otherwise, how can I be sure what I am doing?
1.  Everything is `Contained`s and `Containers`.  

## Step 1: Refactoring Containers

So, the point is, the `Container`s that are defined on a `ComposedDiagramElement` could contain a single element, or multiple ones.  
(So, I guess this should be another option).  What would happen if I add a second label to a Glyph?  Should it replace the first one, or be added to it?  Isn't this going
to create an over-abundance of containers in the svg?  A lot of the time, you can't even interact with a container, because it's obscured by
something else.  Should it be the case that a container *must have* padding, if it contains more than one thing?  What if it contains *nothing*?  Should 
it still be visible on the diagram?  Maybe there is a way to handle this in the GUI.

The problem is going to be the old XML format:  we have lots of tests just checked in as XML, and I don't see how they
are going to survive this change, although maybe there is a way:  we need to devise some kind of transform that allows
us to convert the whole xml library to the new format.  XLST is the obvious idea, I guess.

Also, we have the issue of tests.  We need to *not break any more of them*, but some are broken already.  So, I am going to 
label the currently-broken tests with `	@Ignore("Broken in sprint 7")`.  The main reason the tests are broken is because of
missing fonts, or the fact that the SVG renderer is no longer working, or something to do with background styles.  All of which are 
acceptable right now.

### Inside/Outside

If we go down the route that a `ComposedDiagramElement` has `Container`s which can have `Contained`s within them, when you planarize a part,
you need to decide what to do with those components:

- If an internal `DiagramElement` has linked `Connected`s, you need to render it using the 'corners' approach, as you need to handle the overlaps.
- If it doesn't, use the 'leaf' approach'.
- (in the future: `Port`s count as `DiagramElement`s).  
- If a part has a component which is a container, you need to render the container with the 'corners' approach too. (if it has contents).

So, we need to have a `PlanarizationMethod` enum:  

|Enum Name | Effect When Applied To Containers| Effect When Applied To ComposedDiagramElements|
|---------------------------------------------------------------------------------------------|
|CORNERS   | 4 vertices, one for each internal corner| 4 vertices, one for each external corner|
|LEAF      | not used                                | 1 vertex.  edge leaving direction not important|
|DERIVE   | no vertices, position derived later| no vertices, position derived later |
|GRID     |not used                                | many, divides up internal containers efficiently|

- `CORNERS` is used when there are interal links, and you care how they leave the current `Container`.  Or if there are multiple connected elements 
inside.  Otherwise, the orthogonalization step won't keep these elements in proximity in the final diagram.
- `LEAF` is where you don't care how the edge leaves the element (there are no further internal edges to consider), so you can put it in the planarization as a single 
vertex. 

These we can figure out later:
   
- `DERIVE` can be used in situations where we don't need to worry about the planarization of the container.  For example, it has a single element inside it,
so the container can be in same place as the contents.  We can derive the size and position of the container just by looking at the stuff inside it.  This can also be 
used for text labels on glyphs:  we can derive them later, and they don't need to be in the orthogonalization.
- 'GRID' is for where we are rendering a grid context, and we need to show the edges, even for empty containers (we'll implment this later)

In the short term (without `DERIVE`, we are going to get an explosion of elements, because every label will get turned into a Vertex (at least this will make it easy
to test).

### Surveying Existing Primitives

- `Leaf` indicates that the element doesn't contain anything with links.  Clearly, Glyph and Arrow (which currently implement this) are
going to be able to have containers, so this needs to go.  

- `SymbolTarget`: this should be removed. Symbols should be just instances of Parts.

- `LinkTerminator`: this really is the end of a Link.  It should replace the 'from' and 'to' in the link.

- `Container`:  stays as is.

### Defining The 'ComposedDiagramElement'

A part can be composed of components.  These components will be nested XML elements (in the final analysis).  We could use the tag name
to indicate the part name:  that's the simplest approach.  

### Default Container (For Context)

However, for (the existing) Context, we will need to 
override this behaviour to say "there's only one container, it contains everything".  (i.e. the default container, which contains everything).

### Sketch Of Approach

We need a method like:

```java

interface ComposedDiagramElement extends DiagramElement {

	public static final String DEFAULT = "default";

	List<String> getContainerNames();		// returns all container names, could be 'default' too, which is special
	
	Container getContainer(String name);  	
	
}

```	

And we'll need a CSS directive like:

```css

.glyph {
	components: stereotype, label, text, symbols;
	ports:  tl, tm, tr, lt, rt, rm, rb, br, bm, bl, lb, lm, lt;
}

```
	
Diagrams are Containers.  They are the only thing that is purely a container without something to put it in:

```java

class Container {

	ComposedDiagramElement getOwner();

}
```


