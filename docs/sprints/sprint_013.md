# 1 December 2016 : Sprint 13 - Overhaul Object Model - Glyphs Using Grids

 - Grid layout for Glyphs (i.e. layout=grid ) 
 - Removing @Ignores
 - Default Layout for glyphs (containing a label)
 - Stereotype and text content layouts.
 
## Padding / Margins

One question arising from this is:  where exactly is the vertex?  We might assume for Glyphs that it is 
outside of the padding.  In fact, from a rectangularization perspective, this is a good answer.  It's a shame that 
this in fact means that we've got our grid model wrong for containers.

However, maybe that's not so important, it just means that we're going to unify it for containers and glyphs..
 
# Step 1: Fixing Orthogonalization / Use of `Displayer.size()`

At the moment, we:
	- Planarize
	- Create an orthogonalization
	- Modify glyphs so they have corner vertices
	- Set sizes on the darts, so that the vertices are the right distance apart.
	- Use all that for the compaction process (slidables, etc)
	
This has served us well. What we need to do now is make a few small changes.

1.  We need to make this less specific to Glyphs.  My first thought is that it should be `Leaf` elements
that get sized like this.  Everything should be converted to vertices, irrespective of whether it's a glyph
or it's contents.
2.  This means that we are going to have a separate displayer for `Text`, `Leaf` elements, and for `Connected` / `Containers`.
3.  We should be able to render a Glyph with a single element of text content within it, using two different displayers.

# Step 2: A Glyph Using Grid

Take our example glyph, and try and render it.  The `size()` method should *only* be called on the text. 


# Step 3: When / When Not To Planarize The Grid

- If a Glyph has a grid, and all the elements are labels, then you shouldn't be planarizing it.
- Same goes for a container
- But, if an element in the grid has a connection somewhere, you have to planarize it.


