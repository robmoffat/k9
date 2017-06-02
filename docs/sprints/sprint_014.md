# 20th March 2017: Sprint 14: Labels And Centering

- Refactoring
- A different approach to `VertexArranger`
- Centering of content within a container
- Centering Edges / Rectangularization
- Container Labels

# Thinking

We're currently here:

![Messed Up](images/011_5.png)

In order to place connections at the center of a side, we need to know how long the element is going to be, but there are two reasons we don't, at the moment.

The first is that labels get added later.  When we have a label between two connections, this affects the size of the element they are connecting to.
The second reason is that because elements are now often containers, we have no idea how big they are.

One confounding factor now is going to be that things inside the containers will affect the centering.  This means that our centering logic can't just exist on
`BasicVertexArranger`:  it's got to be part of compaction proper.

## Notes on the old algorithm

- Faces are rectangularized one-at-a-time.
- We order the faces first, so that the inner-most ones are ordered first. 
- Once we set dart lengths in `BasicVertexArranger`, we're done - but it shouldn't work like this.

## A New Algorithm

**Size an Outer Face**:  if we have something that can do this, we can run it on the diagram level, and the run it on all the levels below, in order that we can work
out the size of glyphs and so on.  

**Setting Dart Lengths**: Rectangularization works really well, but it relies on us having correctly set the lengths of darts.  

**Container Sizes**: We currently support `MAXIMISE` and `MINIMISE`.  Really, we should also add something to say "if you're the only connection leaving the side, 
make sure it's middle-aligned".  

**Labels**:  When a label appears between two connections, this can expand the size of the element, and prevent mid-positioning.  

**When should Rectangularization Happen?**:  Face-depth is a blunt instrument:  it means that we cannot consider multiple faces at the same time. 
This is an issue because the solution to fixing the size in one face may lie in another.  Can we incorporate container-depth too?

**Running `MINIMIZE`**:  We want to be able to take a couple of faces, and run minimize over them early on, so we can see how big the thing is.
Once you've run minimize over the faces contained within a `Rectangle`, it's then possible to set the dart sizes for that rectangle.

**Running Outer-Face Insertion**:  Linked to the above, but not always.  

## New Lemmas

- A Rectangle will contain some faces.  You should be able to construct a hierarchy of elements to faces.  There will be more than one face any time you have a 
connection.

- Ideally, when using `MINIMIZE`, we should be able to set the minimum length on the `Dart`s and treat the whole element as a black box from then on.

- This means, we want to be able to Compact one element at-a-time.  (Which will be a number of faces).   Once we've done this, we should be able to embed the elements
contents, setting the perimeter dart lengths.

- A vertex can only have *one `Dart` leaving it in any direction*.  

# 1. Refactoring

Each stage of the layout should be the creation of a mapping:

- `Planarization` is about the mapping between `DiagramElement`s and `Vertex`s, `PlanariationEdge`s and `Face`s (with support of `EdgeOrdering`s around a `Vertex`).  It deals with **`Vertex` positioning** and **`Edge` insertion**.
- `Orthogonalization` is about the mapping between `DiagramElement`s and `Dart`s and `DartFace`s.  It deals with **turns**, **vertical and horizontal `Dart` sections** and **elements ignored in `Planarization`**. (i.e. `Label`s)
- `Compaction` is about the mapping between `DiagramElement`s and `Segment`s, with support in layout from `Slideable`s.  It deals with **sizing**.

We should try and enforce this, and not allow things to pollute-through to other layers.  This turns out to be quite a hard thing to achieve, and took * a lot * of
refactoring.   

## `Dart` / `DartFace` 

The big change that I wanted to effect was that `Orthogonalization` was the process of creating `Dart`s.  *No Darts should get created in other steps*.
`Compaction` therefore is the setting of lengths.    

We have fairly immutable structures for `Dart` and `DartFace`, which can only be manipulated through the `Orthogonalization`.  These don't have anything to 
do with the elements constructed during `Planarization`.

`Vertex` is still shared across many layers, though.

`Dart` no longer has any idea about size.  This means that the `VertexArranger` can just concentrate on creating the Darts in the right directions, and leave 
sizing entirely to the compaction process.  


## `PlanarizationEdge`

The effect of gridding is *still being felt* in the engine:  I've finally bitten the bullet on refactoring so that `PlanarizationEdge` no longer has a single underlying.
Instead, we have:

- `BiDirectionalPlanarizationEdge` (representing part of a `Connection`, or `ContainerLayoutEdge`) which has a single underlying.
- `TwoElementPlanarizationEdge` which has two underlyings, because it is the boundary edge between them (`BorderEdge`).

This was a massive change.

Secondly, removing `isReversed()`.  This means that the PlanarizationEdge is a bit more immutable (though sadly, not yet entirely). 

# 2.  `BasicVertexArranger`

This has been completely re-done.  Instead we now have `VertexArranger` interface, which looks like this:

```java
public interface VertexArranger {
	
	public interface TurnInformation {
		
		/**
		 * Direction of dart arriving at this vertex, after orthogonalization.
		 */
		public Direction getIncidentDartDirection(Edge e);
		
	}

	/**
	 * Returns a subset of edges around the Rectangular perimeter which take you from the end of the incoming 
	 * connection edge to the start of the outgoing connection edge.
	 */
	public List<DartDirection> returnDartsBetween(PlanarizationEdge in, Direction outDirection, Vertex v, PlanarizationEdge out, Orthogonalization o, TurnInformation ti);
	
	/**
	 * This is used for any vertex which is unconnected in the planarization
	 */
	public List<DartDirection> returnAllDarts(Vertex v, Orthogonalization o);
	
	/**
	 * In the case of edge-crossing vertices etc.  we don't need to convert the vertex, so return false.
	 */
	public boolean needsConversion(Vertex v);
```

 - `VertexArranger` now returns you a `List` of `DartDirection`s to use between one `PlanarizationEdge` ending and another starting, at a `Vertex`.  This is needed any time
 the `Vertex` has dimension (i.e. maybe has a label, size, content of it's own).  In this case `needsConversion()` would return `true`.
 - In order for this to work, the `MappedFlowGraphOrthBuilder` passes a `TurnInformation` object to the `VertexArranger`, which contains the details of which sides all
 the `PlanarizationEdge`s arrive on.  (Which it can easily calculate from the flow graph).
 - We have subclasses of this for the different `Vertex` types:  `ConnectedVertexArranger` and `MultiCornerVertexArranger`
 - We have a further subclass, `ContainerContentsVertexArranger`, which puts the contents *inside the vertex*, if they are not included in the planarization.
 - There is also `returnAllDarts()`, which is used for vertices not connected in the planarization.
 

## Future

We can further extend the dart creation process to handle terminators and labels (labels are done below).  Again, we will be positioning these at this stage and not worrying about sizing.
I don't even think we really need to worry about the `LabelCompactionStep` anymore - which also simplifies massively the SlackOptimisation process (no more ordering of slack).
These will have `Dart`s and `DartFace`s in the Orthogonalization, and be handled in no different way to anything else.

# 3.  Compact By `Rectangular`, with a set of `DartFace`s

- Identify all contained `Rectangular` elements.  
- Order them in smallest-first.
- Compact them (i.e. recurse).
- After each one, do 3 below.
- Find all the `DartFace`s for this layer
- Rectangularize them all concurrently
- Compact and produce `Slideable`s (for now).

## `HierarchicalCompactionStep`

In order to enforce this bottom-up approach, we run the compaction like this:

```java

public class HierarchicalCompactionStep extends AbstractCompactionStep {

	@Override
	public void compact(Compaction c, Rectangular r, Compactor rc) {
		if (r instanceof Container) {
			for (DiagramElement de : ((Container) r).getContents()) {
				if ((de instanceof Connected) || (de instanceof Label)) {
					log.send("Compacting: "+de);
					rc.compact((Rectangular) de, c);
				}
			}
		}
	}
	
```

 - Multiple `CompactionStep` interfaces handle the details at a given level, for a given `Rectangular` object.
 - However, compaction must complete for the elements contained within it first.


# 4. Re-implementing Connection `Label`s

## Extra Tests

There are a few new constraint checks we can perform during layout:

 - Making sure that `Connection`s *meet their `Connected`s*.
 - Making sure that `Label`s are rendered, and, that they touch the `Connection`s at some point, and they don't overlap anything else.
 
This should segue nicely into fixing up the labels again.  

We need to make sure we test out the multiple-edges-on-a-side with labels, and then make sure the links below it are correctly centered.
This means special handling for some labels.  Firstly, though, just getting the labels working.   The good thing now is that a `Dart` can be part of the 
rectangle surrounding a label, and part of the connection which the label is on.  

This works very nicely, and ensures that labels will always be part of connection edges.  

## Problem with Labels and Templating

At the moment, a label in the source looks like this:

```xml
   <toLabel id="auto:2">to</toLabel>
```

The problem with this is that we can only expand it to a single `DiagramElement` - in this case a `LabelLeafImpl`.   It would be better if we could separate out
the XML-importing logic, so that this could work to create a `Container` and a `Leaf` to go inside it.  This way, there would be more shapes for the label to have.

In order to do this, we need to change templates a bit:  they should be allowed to import elements which *could turn out to be* Kite9 elements.  For example:

```xml
<svg:svg xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:svg='http://www.w3.org/2000/svg'><stylesheet xmlns='http://www.kite9.org/schema/adl' href="file:/Users/robmoffat/Documents/adl-projects/kite9-visualization/target/classes/stylesheets/designer.css" xml:space="preserve "/>
<diagram xmlns="http://www.kite9.org/schema/adl" id="The Diagram">
  ...
  <link drawDirection="RIGHT" id="arrow1-g2" rank="4">
    <from reference="arrow1"/>
    <to reference="g2"/>
    <toLabel id="auto:2">to</toLabel>
  </link>
</diagram>
</svg:svg>

`<tolabel>` here is using a template, because of the stylesheet (designer.css):  

```css
toLabel, 
fromLabel,
context > label,
diagram > label {
	type: label; 
    ...
	sizing: minimize;
	template: url(template.svg#connection-label)
}
```

This is declaring that labels use a template, from template.svg:

```xml
 <template id='connection-label'>
    <back style='type: decal; sizing: adaptive; '>
      <svg:rect x='0' y='0' width='{x1}' height='{y1}' rx='4' ry='4' style='fill: ccc; ' />
    </back>
    <text style="type: label; sizing: fixed">
      <svg:text>{{contents}}</svg:text>
    </text>
  </template>
```

And this now has 2 elements inside it, the `Decal` and the `Label`.  We can replace the text in at the point of 
inserting the element, but the {x1} and {y1} need to be resolved when we have finally *sized* the label.

This means templating is now two-step (for `Decal`s anyway). 

# 5.  Rectangularization

We're rubbing up against a new problem with `SlackOptimisation`:  if we set a maximum distance between two elements, and then also set a contradictory minimum
distance, we end up with a stack-overflow error the next time we optimise anything.   Is there a way to fix this?

One option is that we somehow test the DAG-ness.  But, maybe this is a dead-end and too complex for now.  Let's try and solve the real problem...

When we do the 'minimization' step described above, we are not really considering the links to/from the `Rectangular` being minimized.
When we get to the Rectangularization, it messes up, in a couple of obvious ways:

 - First, simple rectangularization - we extend the extender into 'meets', and increase the length of 'meets', but it's already fixed-length.
 - Second, we don't consider all the links arriving at the vertex before doing the minimization step.  Minimization needs to include incoming edges.
 
## The Minimization Process

This is now implemented as `MinimizeCompactionStep`:

```java
@Override
	public void compact(Compaction c, Rectangular r, Compactor rc) {
		DiagramElementSizing sizing = r.getSizing();
		
		if (sizing == DiagramElementSizing.MINIMIZE) {
			OPair<Slideable> lr = c.getXSlackOptimisation().getSlideablesFor(r);
			OPair<Slideable> ud = c.getYSlackOptimisation().getSlideablesFor(r);
			if ((lr != null) && (ud != null)) {
				// sometimes, we might not display everything (e.g. labels)
				log.send("Minimizing Distance "+r);
				minimizeDistance(c.getXSlackOptimisation(), lr.getA(), lr.getB());
				minimizeDistance(c.getYSlackOptimisation(), ud.getA(), ud.getB());
			}			
		}
	}
```

However, this isn't enough:  

 - it's not considering the edges arriving at this Rectangular
 - it's being processed *before* rectangularization, which causes the aforementioned stack-overflow.
 
Nevertheless, it's at this point we need to consider mid-point setting.  Let's have a think about this.  Things that affect the length of a `Connected` side are:

 - Terminators
 - Labels
 - Other glyphs that are somehow needing to be rectangularized first
 - Leaving edges
 - Fanning
 
So, at what point *can* we set the **Minimization** process off, above, and at the same time set the mid-point of the edges?  Clearly, it has to be done after we have
rectangularized those other elements.  

   

At this point, we can handle mid-point setting.    This needs to happen when we do the sub-graph insertion, that's the obvious place.  The only problem with this is that
in order to determine `width`, we have to use the `Slideable`s, which are actually a separate part of the process.  *Do we know all the `Slideable`s up front?  No - 
because of label insertion.  We need to create the `Slideable` for that, in order that we can insert them later.  

So, clearly, the only time we can set the mid-points is *during Rectangularization*:  it's part of the process!   What does it look like?

# 6.Container `Label`s.

Currently, these aren't placed on the diagram, and for that reason, a lot of tests are failing.  A good question right now is, should we fix these, and get the
labels working, or leave them for later?  

Container labels have changed:  we no longer have a single label in a container, and I guess if we are going to persist with this we need to give labels a specific
side.   This is probably not that hard to achieve, but can be left for now.    So, we'll make the simplifying assumption of a single label per container, 
in order that we can get back to having working tests.








# 5. JaCoCo


```xml

```





- New Test Case:  two columns, connected so we can get fans on both sides
- Segment position?
- HiddenSideVertex

- Simplify / Remove a load of edge ordering logic (not needed for border edges)
- printlns
- isStraightInPlanarization
- protected RoutingInfo getPosition(Vertex v) { // AbstractRouteFinder
isSeparatingConnections - ConnectedVertex

multiple labels per container, on different sides, different orientations.

