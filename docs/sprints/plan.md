# Kite9 Delivery Plan

Rather than a grand “ta-da”, is there any way I can split this out and do it in bits?

It would be really nice to not have to do it in one big go.  In fact, we should ensure this is the case.   How can we do this in 2-week (ish) “sprints”?

An alternative might be to “rebuild” Kite9 from scratch in a new project.   If this were done, we could start in a different container, and firstly just do building pages.  We could use a new mysql db with Spring boot.

It would be nice to do this so that we have something to see as we go along.
Each release would be to “production”, which would be an amazon EC2 instance, with load-balancer and MySql database at the back end.

## General Sprint Goals

1.  Release instantly.  i.e. full automated tests.  I don’t know yet what this means for Javascript, but I’ll figure it out.
2.  Proper load-balancing etc.
3.  Integration tests running against the server.
4.  BPML
5.  Swimlane Diagrams
6.  Sharing Entities (although, not in this plan)
7.  Fixing up the GUI so we can do quick releases.
8.  Unleashing the full power of SVG.

## The Sprints

### Set up a new container-based server.

- With the same entities in it as Grails. 
- It should be possible to query the model against the original database via REST and it still works.    DONE
- We write a bunch of tests that retrieve the objects as JSON (with bits of embedded xml string, I guess)  DONE
- At this stage, we don’t even need Kite9-core. 
- We need to be able to deploy to the cloud, and run integration tests there. DONE

[Sprint Notes](sprint_001.md)

### Documentation / Check in.  

- Move to github / markup and lose everything that no longer makes sense.  I.e. complete clear-out.  Just keep stuff that makes sense as we go along.
- Some Kite9 website should point to the documentation.
- Would be nice to publish the end-of-sprint stuff on the wiki or somewhere.
- Some vision documents
- Published via github.

[Sprint Notes](sprint_002.md)

### Port To Grails 3 in the new project.

*(sprint added 20/3/2016)*

- Upgrade the original Kite9 to Grails 3.
- We can keep our existing Kite9 infrastructure working by moving to Grails 3, which is based on Spring Boot.
- This probably means moving to gradle builds too, and therefore a gradle fabric8 plugin.
- I think ideally we should stick to the spring/jpa-based persistence because it's more future proof, so we need to move the rest of those entites and add tests for them.
- This means that Kite9 should be completely ported to AWS.
- Add a table for the diagram contents, unrendered and rendered
- Need to check email works

[Sprint Notes](sprint_003.md)

### Setting Up Travis + AWS

- Continuous build of master, (releasing to Amazon automatically? )
- Turn off Linode?
- Sort out DNS
- Scaling ?

### Visualisation Engine refactoring

- currently, this is groovy code.  Refactor so this is a first-class Java, Spring service.
- Use REST, use the user token to validate requests.
- get it to render PDF, PNG, XML files using REST POSTs. 
- If we’ve been refactoring carefully, this should also still work.
- Write some tests for this.
- Store results in the content table.
- hard-code the stylesheets for now.

### Time to overhaul the object model.

- everything should be parts and containers.  Links should be reformatted.   Ideally, we are backwards-compatible with what came before.  So, you can load up the original diagram xml and it comes back in the new format.
- Containers
- Parts (with type = glyph-simple, glyph-with-stereo etc.)
- Classes
- Text Areas
- Links broken down into Ends (which can have labels?)
- Ports
- Links as “straight” rather than LEFT, RIGHT etc.
- Aligns
- Stylesheet to use 
- After we’ve done this, Visualisation is pretty much unrecognisable from it’s original form, but we need for the tests to still pass.

*This would seriously break my existing GUI.  How to solve this problem?  I really don't want to refactor the GUI at this point.  Nore do I want to waste time on converting the XML back to the old format.  So, at this point, we would be really screwed.*

### We need to send our new object model to JSON.

- This is going to be a lot of JSON.
- It should be about creating groupings, setting paths and setting styles + classes.
- Everything that was in the original object model, plus layout information.

### React To Load JSON

- We should be able to render the JSON returned by passing it through a simple react component which turns it into SVG.
- Every item from the object model will be a group, which will potentially have some svg elements associated with it.
- Using D3 + React to display on the screen.

### Styling

- React should pass through the style tag to the JSON elements for inclusion on the diagram.
- Write a test to make sure this happens.

### CSS

- Write a Kite9 CSS file, which will be also loaded up by the react component.
- Turn off the style element coming from Kite9:  can we replace this with CSS class?
- FUN

### Server-side CSS

- Get the CSS loaded up on the server side by Batik.
- Remove style information from the java Stylesheet

### 9.  Client-Side CSS

- Get React to render the client using the same css file.
- Write some tests for this somehow to check it’s correct.

### 10.  Layout

- extract this so that there’s a separate class for each different layout.
- Just keep existing tests working.

### 11.  Extend CSS

- Add my own elements to the CSS so that I can see these being inherited by various elements.  I want to get to the stage where part layout has some CSS properties associated with it.
- Need to decide what these CSS elements are, but obviously they should be about layouts.

### 12. Glyph Using Grid Layout

- Write a few simple tests to do a grid layout.   We have 3 basic glyph-types that can use this.

13. Symbols Using Grid Layout

- Write a test that shows some symbols, each having a class, inheriting their outline shape, colour etc.  and being rendered correctly.

14. Grid-Layout Context

- Contexts should have the capacity now to store multiple grids.  There will need to be some clever interpretation here (i.e. we need to construct the grid on-the-fly) but it’s ok.
- This should be a nice example, with a test.

15. BPML:  Write a stylesheet for this in CSS

- Include most of the basic entities, render a diagram using it.

16.  Container exit-sides CSS directive

- So we can dictate in the stylesheet where things can leave.

17.  Project CSS/JS Repository

- At this stage, we pretty much have CSS done, so we need an entity in the system to hold details about registered CSS stylesheets.  We will use the public URLs of these in the XML, but the actual values will be cached in the DB to speed things up.
- Should be an option to say “don’t update” or “update every…”, and the cache, when returning, will check and behave accordingly.
- So, handle this caching.

18.  Command Pattern

- Insert element (id, id-less xml bits)
- Modify element (id, what it looks like after, before)
- Remove element (id)
- Set attribute
- All basically stuff that allows you to manipulate XML (this allows palettes to work).
- After posting the command, it should return the new JSON (for react), or an error message should pop up. redux can handle that.
- Commands should attempt to apply to the active diagram.

19.  Porting Javascript

- We’re going to need a lot of the original app now.  So, we need to structure this a bit better, and use webpack / npm stuff.
- Add in a javascript testing framework.

20. Undo / Redo Commands

- These are going to simply take you back and forth through the diagram history and choose the “active” diagram.  Revisions will still be in numbered / timestamped order, but when you undo, you undo for everyone.  But nothing is lost.  NAILED IT.
- You should be able to head back to any previous version in the diagram history and say, “this is the active version”.  We should have some marker record somewhere to do this.  i.e in Document.
- All other commands end up creating a new revision, and setting the active document to that.

21.  Select Behaviour

- Some stylesheet should define the GUI behaviours.
- React has some callback that can modify the SVG as it’s being rendered, and add behaviours (this is how we plug in)
- We should see that React correctly loads the right javascript files that are mentioned in the stylesheets.
- We have a single GUI behaviour for now called select:
    - This is an onClick event for shapes, which fires a redux event to add the element to the selected state.  Also, I guess it should add a selected class to the SVG element, so that we can see the selected elements.
- We need some tests for this in the Javascript.  Clicking on containers, clicking on parts, clicking on links.
- JSON should already contain selection outline and line details.
- Somehow, select should extend the react layout to include the shapes needed to make select work… (not sure we will need these anymore though?)

22.  Context Menu

- Define the context menu plugin.   This should come up when people select stuff.  Once you select stuff, the menu should pop up.  What does it contain?   Nothing yet.  Just needs to come up in the right place.
- Add a couple of placeholder options.
- Options can appear and be greyed out.
- Write a test.

23.  Modal Plugin

- We want a javascript function that opens up a modal dialog.
- It should take an array of fields, and callbacks for validation, and possibly callbacks for options, if it’s going to be populated with some.

24.  Menu Plugins

- Add things like “edit text”, “delete”, “surround with container".   Edit text will need to use the modal dialog.  Plug these into the context menu.

25.  The Palette, part 1.

- First, a palette needs to pop up, with appropriate elements in it.  To do this, we need to look at palettes defined by the stylesheets, and also look at the context of where we are putting the element.   I think this is a call to the server for some JSON which can be rendered containing the palettes in question.
- Write a test that this comes up in the modal plugin.

26.  The Palette, part 2

- When you click an element, the palette should close, and you are dragging around the element that you want to place.
- What would be the command for this?  It could be a fairly complex piece of XML.   So, we need to create a command that adds XML.
- Extend the test to do this.

27:  The Palette, part 3

- We need to grey out elements that are not allowed.
- So, this is some kind of plugin to the react component again.  (We are going to need some general way of adding callbacks to react).
- We also need a way to say which elements a container can accept.  (or alternatively, which elements can go in container x).   Which way round makes more sense?  Either a container can accept anything, or it can accept only certain kinds of element.

28.  Top-Level Menu

- Again, this should be pluggable.
- We want to add the zoom controls, as well as undo/redo.
- Undo and redo are actually going to post commands to the server now.
- Write some javascript tests for the existence of these and that you can press them and the correct actions occur.

29.  Link

- So, the basic thing is, you click on an element, and you can select draw link.  And, then it draws a link to another element.

30.  Link to New

- Draws a link, but puts a new element down again (the last one from the palette).  So again, we need to create some kind of event that draws to the mouse pointer, recognises the container we’re under, etc.  And then adds an element to the container.   This is a combination of the palette-drop plus link functionality.
- Must add a test.  Redux must be able to have the state of what is going on, what is being dragged, etc.

31.  Fix aligns.

- We want to be able to create these from the gui too by selecting a bunch of align-able elements.
- You need to fix the compaction process to respect these too.
- Write a test that makes sure this works.
- Generally, the code we already have for the javascript is good.
- An align should have layout - it should just be a long line on the diagram in the end (if show layouts mode is on)

32.  Layouts

- Layouts need some kind of background image so that we can see how they are set up.  You can do this ridiculously easily with patterns, where you can define a pattern in SVG, and use it as a fill for another SVG element.  awesome.
- Use CSS to turn on layout information at the global level.
- Write a test to test what the screen looks like with layout info on or off.  Problem is, this is more global state, and we wanted to avoid global state (especially invisible global state)

33.  Hierarchical Layout

- Allow this as an option.  There is the secondary option of left, mid and right-aligned hierarchies.

34.  Reverse Link

- Swaps the ends of the link around… the Ends point at the opposite elements.
- Test

35.  Max-Size Algorithm

36.  Collapse Container

- Containers should have a smallest-size option.

37. Security

- Log-in Screen.
- Limiting the projects you can look up, based on who you are.

- Ports: should also be configured in stylesheet.  At the moment, we default these to the middle of each side.  That should change
- Line-lengths: we should be able to set these.

- We should be able to pull back JSON for the publications, too.  Again, we should take all this over into the new Spring world.