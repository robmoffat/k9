# 20th March 2017: Sprint 14: Labels And Centering

- Centering of content within a container
- Refactoring
- A different approach to `VertexArranger`

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

**Running `MINIMIZE`:  We want to be able to take a couple of faces, and run minimize over them early on, so we can see how big the thing is.
Once you've run minimize over the faces contained within a `Rectangle`, it's then possible to set the dart sizes for that rectangle.

**Running Outer-Face Insertion**:  Linked to the above, but not always.  

## New Lemmas

- A Rectangle will contain some faces.  You should be able to construct a hierarchy of elements to faces.  There will be more than one face any time you have a 
connection.

- Ideally, when using `MINIMIZE`, we should be able to set the minimum length on the `Dart`s and treat the whole element as a black box from then on.

- This means, we want to be able to Compact one element at-a-time.  (Which will be a number of faces).   Once we've done this, we should be able to embed the elements
contents, setting the perimeter dart lengths.

- A vertex can only have *one `Dart` leaving it in any direction*.  

## Refactoring

Each stage of the layout should be the creation of a mapping:

- `Planarization` is about the mapping between `DiagramElement`s and `Vertex`s, `PlanariationEdge`s and `Face`s (with support of `EdgeOrdering`s around a `Vertex`).  It deals with **`Vertex` positioning** and **`Edge` insertion**.
- `Orthogonalization` is about the mapping between `DiagramElement`s and `Dart`s and `DartFace`s.  It deals with **turns**, **vertical and horizontal Dart sections** and **elements ignored in `Planarization**.
- `Compaction` is about the mapping between `DiagramElement`s and `Segment`s, with support in layout from `Slideable`s.  It deals with **sizing**.

We should try and enforce this, and not allow things to pollute-through to other layers.  This turns out to be quite a hard thing to achieve, and took * a lot * of
refactoring.   

### `Dart` / `DartFace` 

We have fairly immutable structures for `Dart` and `DartFace`, which can only be manipulated through the `Orthogonalization`.  These don't have anything to 
do with the elements constructed during `Planarization`.

`Vertex` is still shared across many layers, though.

### `PlanarizationEdge`

The effect of gridding is *still being felt* in the engine:  I've finally bitten the bullet on refactoring so that `PlanarizationEdge` no longer has a single underlying.
Instead, we have:

- `BiDirectionalPlanarizationEdge` (representing part of a `Connection`, or `ContainerLayoutEdge`) which has a single underlying.
- `TwoElementPlanarizationEdge` which has two underlyings, because it is the boundary edge between them (`BorderEdge`).

This was a massive change.

Secondly, removing `isReversed()`.  This means that the PlanarizationEdge is a bit more immutable (though sadly, not yet entirely). 

# 1.  `BasicVertexArranger`

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

 - `VertexArranger now returns you a `List` of `DartDirection`s to use between one `PlanarizationEdge` ending and another starting, at a `Vertex`.  This is needed any time
 the `Vertex` has dimension (i.e. maybe has a label, size, content of it's own).  In this case `needsConversion()` would return `true`.
 - In order for this to work, the `MappedFlowGraphOrthBuilder` passes a `TurnInformation` object to the `VertexArranger`, which contains the details of which sides all
 the `PlanarizationEdge`s arrive on.  (Which it can easily calculate from the flow graph).
 - We have subclasses of this for the different `Vertex` types:  `ConnectedVertexArranger` and `MultiCornerVertexArranger`
 - We have a further subclass, `ContainerContentsVertexArranger`, which puts the contents *inside the vertex*, if they are not included in the planarization.
 - There is also `returnAllDarts()`, which is used for vertices not connected in the planarization.
 
## Sizing

`Dart` no longer has any idea about size.  This means that the `VertexArranger` can just concentrate on creating the Darts in the right directions, and leave 
sizing entirely to the compaction process.  

## Future

We can further extend the dart creation process to handle terminators and labels.  Again, we will be positioning these at this stage and not worrying about sizing.
I don't even think we really need to worry about the `LabelCompactionStep` anymore - which also simplifies massively the SlackOptimisation process (no more ordering of slack).
These will have `Dart`s and `DartFace`s in the Orthogonalization, and be handled in no different way to anything else.

# 2.  Compact By `Rectangular`, with a set of `DartFace`s

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

## Changing Rectangularization

The big change that I wanted to effect was that `Orthogonalization` was the process of creating `Dart`s.  *No Darts should get created in other steps*.
`Compaction` therefore is the setting of lengths.    So, only `Compaction` need know about `Display`.   This is working fine when considering the size of `Rectangular`
elements, however, we seem to have hit a snag setting the positions of `Segment`s which are parts of `Connection`s.  How to size these?

Secondly, we're rubbing up against a new problem with `SlackOptimisation`:  if we set a maximum distance between two elements, and then also set a contradictory minimum
distance, we end up with a stack-overflow error the next time we optimise anything.   Is there a way to fix this?

One option is that we somehow test the DAG-ness.  But, maybe this is a dead-end and too complex for now.  Let's try and solve the real problem...

## Set the `Rectangular` dart-lengths.  

Rectangularization is probably part of the phase where we insert the outer `DartFace`. (Which we already know how to do).  Then, we can size at this point.  This means, when considering
`MINIMIZE` functionality, we need to do them in the correct order, so the smallest elements are the minimized first.


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


At this point, we can handle mid-point setting.    This needs to happen when we do the sub-graph insertion, that's the obvious place.  The only problem with this is that
in order to determine `width`, we have to use the `Slideable`s, which are actually a separate part of the process.  *Do we know all the `Slideable`s up front?  No - 
because of label insertion.  We need to create the `Slideable` for that, in order that we can insert them later.  


## Extra Tests For Labels

We need to make sure we test out the multiple-edges-on-a-side with labels, and then make sure the links below it are correctly centered.
This means special handling for some labels.


## Simplify `Rectangularization`

It should be the case now that when we get to the `Rectangularization`, we know the length of every Dart.  So, can we 
simplify further?  (Maybe not).

- New Test Case:  two columns, connected so we can get fans on both sides
- Segment position?
- Vertex position setting (MultiCorner anyway)
- HiddenSideVertex
- Rename the branch to 14_.
- AbstractTempEdgeRouteFinder2 (use generics for the edge type)
- Add test that connections meet their connecteds
- Simplify / Remove a load of edge ordering logic (not needed for border edges)
- printlns
- isStraightInPlanarization
- protected RoutingInfo getPosition(Vertex v) { // AbstractRouteFinder
isSeparatingConnections - ConnectedVertex
- changes to AbstractVertexArranger etc.

# JaCoCo


```xml
 <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.7.9</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>generate-code-coverage-report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```
