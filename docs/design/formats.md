# Data Formats - Design Thoughts

## Some Use Cases

 - Ok, we're after a data format essentially for diagrams.
 - XMI:  Do we want this?
 - Jim's Use Case:   hundreds of systems reporting into one giant store of crap.  How to curate this?
 - Filtering:  we want to filter what's on one or more diagrams to produce another.
 - Import:  XML would be ideal, I guess.
 - Java Import:  we have this already, we should make sure it continues to work.
 - Coding:  Maybe people will one day build software in Kite9.  What would this look like?
 - Hierarchies: hierarchies are *the* way to put a lid on complexity.   Labelling, tagging, folders, filesystems.  they all do this.  
 - Editing regular XML:  this would be good if we could just create some way of pulling in xml randomly.
 - processing instructions:  somehow, we need to also have the information about *how* to process the diagram, what stylesheet to use, what behaviours etc.  (i.e. the CSS).
 - since we're using CSS, it seems really natural that we should be processing some kind of XML.
 - deltas:  we need to be able to see how things change from one version to the next.
 
## Options

- Database schemas
- XML Schema
- JSON Schema
- Roll-our-own schema
- GIT (for complex tree handling)
- Filesystems
- Other organisational structures.

## Schema-Features

- What X's can we put in a Y.
- Properties on an X.
- What a link looks like.
- Formats of properties (string structure, etc)

## Reference / Declaration -or- Use / Mention

** Wiki's do this ** :  declarations are pages.  References are links.  Kite9 needs to do slightly better, as when we reference something, we might not just be including a link, but *what it looks like* and some other relevant facts pulled in.

** In Code ** :  In a Java code-base, the class files roughly follow the organisation of the source files.  It's not always this way.  But we still have reference-declaration type logic going on:  the classes are declared once, and otherwise, referenced.

So, when we pull in something, we need to say what we are going to show: 

 - **A reference** : just the shape of the thing, and it's name.
 - **Include attributes**:  add in all the elements that are contained within that thing.
 - **Include contained elements**:  add in all the stuff it contains too.
 - **Relevant Links**:  if it links to other elements (which are on our diagram already), include these.  
 - **All the Links**:  include any referenced elements, irrespective of whether they are currently on this diagram.
 
For each of these broad inclusion mechanisms, you may want to have some kind of filter, to reduce the amount of attributes/contained elements/links.  Not sure how this would work... likely we need some way of using tags or something.   *Obviously, XPath provides exactly the functionality we need here*.

## Open Structure

Ideally, if I pull some XML *back out* of Kite9, it should still be consistent.  This means that our references should be expressed as URLs, which resolve to REST resources.  (xinclude, whatever..)

The implication of this is that every entity in every diagram has an absolute URL, which can be referenced.  That way, we can always reference something in a diagram.

Diagrams, and all their versions, are clearly REST resources too.  (In fact, Diagrams are just a special kind of entity).

## Changing Structure

I suppose you could have a macro that pulls out that entity and places it in it's own file, and changes the references to suit.

From a java point of view, this would be better too.   Again, this brings us back to the issue of having global IDs for Java elements, and needing to be able to express those in the diagram.  So... we're back here again.




 
 
 
 

 