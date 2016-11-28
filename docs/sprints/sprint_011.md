# ??? - Sprint 011:  SVG As The Output Format

- In order that we can properly take advantage of fills, we need to start using SVG as the output format.
- this means converting our displayers to use SVG rather than Graphics2D.  
- Test against SVG content, make sure it's deterministic.   

## Prelude

Ok, so we have an element (say a `Text` `DiagramElement`) which we want to render.   This will be turned into
SVG by applying the stylesheet, and rendering it into a number of elements in a number of layers.

A single diagram element can turn into multiple SVG elements.  So, we should hold them together 
with a group.  (We already do this, really)

And, the groups should be further layered to enforce drawing order.

Finally, we will have the special "flannel" layer and "container" layers, which will provide 
drop-containers and interactivity.  

*Any* SVG CSS will be allowed to be applied, so, this means animations will work, background images, etc.

So, we need to cover off a lot of that in the tests.

Also, we are going to need a "common" area of the diagram, where we can define fill patterns and so on.  
This is a key part of SVG.

# Step 1: Displayers

We need to refactor displayers so that we have an ordered list of layers, and each displayer knows how
to draw a layer (or layers?).  

We have the following layers:

```java

public enum GraphicsLayerName {

	BACKGROUND, SHADOW, MAIN, FLANNEL, WATERMARK, COPYRIGHT, DEBUG
}
```

We at minimum need to create a `text`-element displayer which handles:
- the `shadow` layer
- the `main` layer
- the `flannel` layer

And, for the `diagram` element, we need to handle `background`, `watermark`, `copyright` layers.

As a test, we should be testing the *actual content of the SVG diagram*.

# Step 2:  Path Transformations

From [Sprint 8](sprint_008.md) we have the following definition for grids:

```css
glyph.full-spec {
	type: composed;
	layout: grid;
	grid-size: 3 4;							// defines a 4x4 grid, from x1-x4 and y1-y4
	
	path: M x0 y0 H xe V y3 H x0 z;			// basically a rectangle. xs is an alias for x0 and xe is an alias for whatever the last one is (y3 in this case)
	padding: 6px 6px 6px 6px;				// leaves padding around the rectangle 
}

glyph stereotype {
	type: text;								// defines a label
	layout: right;
	occupies-x: 0 1;						// might be a better way to define position than the single 'occupies'
	occupies-y: 0 1;					
}
```

Where, we can define a path based on the shape of the grid.  That's cool, but we don't even need to go that far.  We could define a path based on just the size of
the stereotype.  The path may be related to the padding (we have minimum size padding, but layout may increase the padding to accommodate arriving connections).  Or, the 
path might be related to just the text within the text area.  Also, we have a margin which surrounds this, which the path might stray into, reasonably.  

What is the difference between the padding and the margin, then?  Padding is putatively "within" the shape, and connections meet up to it.  But, margin is the distance 
between other elements, and while margin might be a minimum distance from the another element on the diagram, it doesn't mean that you "own" this space, so 
anyone overlapping into it might end up overlapping with something else.

# Step 3: Managing Transitions

One problem we are going to face is that we need to transition between old and new versions of an SVG diagram.
This is going to be tricky.  We need to make sure:

- We have IDs on all the elements
- We use translations
- We use groups correctly, so each element has a group, which we can move around.  




