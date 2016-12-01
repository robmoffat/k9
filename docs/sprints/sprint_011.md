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

## SVG-Holder

It would be nice if we had a component that was a container for SVG. i.e:
- It has a minimum size
- It's rendered in SVG as a group
- Within the XML, it contains some SVG instructions, which get piped into the output.
- Potentially, maybe we can expand the size of the group later. 

This would be useful for things like the background image, and also the horizontal line (or any other fixed shapes
that we want to output- ports, perhaps).

Later, we might be able to extend this to cover terminator shapes.  For now, I think it's enough to just create one.

## Palettes

From the point of view of a palette, it seems really cool to pull a  load of XML out of one document (i.e the palette)
 and stick it into another and off we go.

However, in practice, this obviously won't work, unless the representations are completely the same, or at least, reversible.

*Can we create an augmented SVG, that allows us to drop from one place into another?*

Answer: no.  The output from rendering *is just different*.  You've got layers, etc.  Isn't this going to
make it really hard to work with palettes?  Yes.   Well.   Maybe.

I guess the way a palette works is that you drop an element from one place into another.    So, when we add
a palette into a diagram, we are basically adding a bunch of `defs`.  These `defs` get copied across to the diagram.


## `use`

SVG already provides facilities for this, via the `<use>` tag.  Out of the box, we wouldn't support this. 

- **A palette is an XML file**, an output format that we produced, that can be shown on the screen.
- **There should also be a stylesheet**:  A stylesheet can define some `<defs>` somehow, which are used by the palette? 
- When you copy a bunch of XML onto your document, defined as SVG, can you edit it?   The obvious answer is
no:  it's static SVG.  We're not building an SVG editor after all. 
- But, we're kind of headed this way:  we have colour-pickers, we want to be able to upload background images.  Where 
will it end?

## Containment

Our own XML specifies the container hierarchy.  This is a big part of knowing what to render.  However, when we convert to 
SVG, this is always lost.  Also, you can't (I think) support both containment and animation, so obviously containment has to go.

But, well we need to have this specified in the diagram, otherwise copying and moving won't work, so I think the `flannel` layer
(which should be called `control`) will manage this... somehow. (maybe we'll have some extra tags or attributes to do this?).

In fact, I think maybe the control layer should contain the snippet of the original XML that represents the shape from the 
original diagram.  That way, we can reconstruct the original XML when we do the palette copy.

So, the control layer is for behaviours (`onclick`, `mouseover` etc) as well as important meta-data about the structure of the diagram.

## Text

Take this example diagram:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<diagram xmlns="http://www.kite9.org/schema/adl" id="The Diagram">
  <arrow id="One" rank="0">
    <label id="auto:0">One</label>
  </arrow>
  <arrow id="Two" rank="1">
    <label id="auto:1">Two</label>
  </arrow>
</diagram>
```

We're now at the stage where we need to consider the label and the arrow as separate parts.  Currently, our displayer is treating both at the same
time.  We shouldn't do this anymore.  

But, how to achieve this?
- Often, displayers are asked to size up their content.  This is ok where we know exactly what the content is, and this is what we've relied on in the past.
- Containers also have the problem of working out their sizes.  Most of what we are doing seems to be moving things in the direction of being more like containers. 
- Sizing is only used in 3 places:
	- `LabelInsertionOptimisationStep` (for working out the size of a label)
	- `BasicVertexArranger` For setting the sizes of the darts around a vertex.  Happens after orthogonalization.
	- `VertexArrangementOrthogonalizationDecorator`: Which basically calls the above.

So, it seems like we need to do some work on deciding what to represent in the diagram, and how to represent it, post orthogonalization.



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

# Step 2:  Random SVG

[This page](https://developer.mozilla.org/en-US/docs/Web/SVG/Element) covers details about all of the different SVG elements.  

Now, since we are using CSS attributes to determine content, it seems perfectly reasonable that we will be able to nest any of this stuff within
one of our elements.  

# Step 3:  Path Transformations

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

## Point Transformations

As well as paths (which are in SVG specified using d=""), we could also perform transformations on points:

```xml
<line x1="20" y1="100" x2="100" y2="20"
      stroke-width="2" stroke="black"/>
```

or 

```xml
  <polygon points="60,20 100,40 100,80 60,100 20,80 20,40"/>
```

Remember, groups will help us do simple translations, but if we want to scale to the size of the container, or to particular grid positions, we'll have to do
something cleverer.

One problem with this is that we will struggle to load the code in using Batik, I would think?  It's going to hate parsing CSS entities if they contain weird 
syntax.  

# Step 4: Managing Transitions

One problem we are going to face is that we need to transition between old and new versions of an SVG diagram.
This is going to be tricky.  We need to make sure:

- We have IDs on all the elements
- We use translations
- We use groups correctly, so each element has a group, which we can move around.  


# Step 5: Definitions

In SVG, it's possible to define a filter or texture in one place, and then re-use it across the whole diagram.  If we import an element using a palette
then it's important we also bring in the `defs` from it.  


# Step 



