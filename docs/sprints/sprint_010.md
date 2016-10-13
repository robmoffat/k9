# ??? : Sprint 10 - Overhaul Object Model - Ports / Decals

- Ports & Decals (overlapping elements on a glyph)
- Links as “straight” rather than LEFT, RIGHT etc.
- STRAIGHT directive

### Ports & Decals

These are interesting.  They are effectively "extra" pieces attached to the edge of a `Glyph` (or whatever).  While
ports are effectively zero-size, the idea of a Decal is that it does have size, which is considered as part of rendering.  

Therefore, I think, Symbols are Decals?

We already have something a bit like ports:  when we create containers, and attach links to them, we create ports then.  So, really, when we
decide how to represent an object on the planarization we need to consider:

1.  Does it, or subcomponents have leaving edges?  If not, then maybe it doesn't need to be in the planarization at all (e.g. labels, but 
potentially also text lines)

2.  If a container doesn't have leaving edges, you can ignore it from the planarization too.  (i.e. this means that the contents of the container
don't have edges).  *But, this is an optimisation which could come later.*

3.  Does the component have edges leaving from a particular port?  If so, then really the component should be represented by it's corners on the diagram,
and the ports should be represented too, in the same way (we already have code for this, we just need to generalize the approach, rather than making it 
specific to Containers).

#### Port Approaches

- **Port position is significant**, therefore overlap before you reorder the edges.   (An edge meets a particular point).  This is true of 
directed edges, which should meet the "middle" of each `Contained` (so, we define ports for the middle).
- **Port order is significant**:   maybe order indicates something?  In this case, we should create ports ahead-of-schedule in the right order.
- **Port side only is significant**:  therefore, instead of having one link per port, we create lots of links, and then split them out.  I believe
we already have code for this too.
- **Side is insignificant**: therefore, just have a central position.

So, we're going to need to factor this out so that it's not specific to Glyphs/Containers/Arrows.  This is also going to impact the behaviour
of the GUI. 

#### Defining Ports

The available ports are a property of the thing being connected to.  There are a couple of approaches which could work:  

A glyph has a number of them, defined positionally much like any other components (using `occupies` ?).  Then, you would need
to represent them in the xml, with an ID, for them to be usable.  This seems like the most *apropos* solution, because then the Glyph can 
show extra detail (by way of it's port).  This does open you up to the possibility of ports getting accidentally deleted.  (Certainly, they can be selected)

Another idea is that to draw the port, we might need to define where it is, and then draw from that point.  Within the context of layout, ports and decals
should therefore be defined by their *center*, and drawn from that point.   Defining the center might be tricky, you could end up with something like:

x: (xs-x2)/2
y: xe;

`xs`, `xe`, `ys`, `ye` should *always* exist, even when we don't have a grid.

This allows us to define some standard port-positions, and use them.  So, we should be able to include a child XML element, which represents a port, add it to 
the planarization, and then link to it. 

*We already have logic that allows this to work for containers*.

It's then up to the Rectangularization process to ensure that these elements are sized correctly, when possible.  This is going to require some quite complex 
changes in the rectangularization and compaction processes (we need to do these concurrently).

But centering ports is a problem for another day.







