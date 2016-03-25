# Introducing Kite9 - Redux #

I have been working for a long time on some diagramming software, called Kite9.   It’s an attempt to bring Enterprise Architecture kicking and screaming into the 21st century, with automatic layout, single page web-design and collaborative, real-time editing capabilities.

But, it got stuck.  My original architecture got bogged down in a morass of technical debt.  After a long hiatus, I’ve decided that yes, I still want to work on this, and it’s time to dust off my original design and see if it can be repaired.   Specifically:

## Goals ##

1.  Release instantly.  i.e. full automated tests.  I don’t know yet what this means for Javascript, but I’ll figure it out.
2.  Proper load-balancing etc.  So, containerisation.
3.  Integration tests running against the server.
4.  BPML Support
5.  Swimlane Diagrams
6.  Sharing Entities between diagrams
7.  Fixing up the GUI so we can do quick releases.
8.  Unleashing the full power of SVG.
9.  New layouts, including hierarchical layout and gridded layout (more on these later).

... But, whatever - let's see what happens!

## Some Promises ##

Ok, so that’s what I want to do.  But, there are a couple of other changes I want to instill along the way:

### Releases Every Two Weeks ###

I really want to develop in an Agile way, releasing frequently.   This is a hard thing to master, especially when the functionality is complicated and intricate like Kite9.  The other problem is it's going to mean developing in a certain order, really thinking ahead about what I need to do next.   For this reason, I've pretty much planned out a year's worth of minor sprints - looking for areas of dependency between each of them to try and make sure we implement this in the right order.  Will be interesting to see how this works out.

### Release Notes ###

Each Sprint is going to get a release into "production", and each will have release notes detailing exactly what went on.  In addition, I am going to use this as an opportunity to document the development process, so other people can (maybe) learn what to do via the process.  By and large, a lot of the problems in Kite9 are now because the techniques I used were wrong, or are now out-of-date, so this is a chance to really use some best practices.  Maybe this will be interesting to other people.  

There was a good reason why this didn't happen before:  the first version of Kite9 was really an experiment:  Could I develop a really sound layout algorithm?  And then, could I make a usable editor based on this?  Since we are not in "experiment mode" now, but in "build some really decent software" mode, I think this changes matters, hence the blog posts.

[Review Progress Here](docs/sprints)

### Build Kite9 In Kite9 ###

I'd like Kite9 to "bootstrap" itself to a certain extent:  the user-interface that we build will as far as possible will be written using Kite9 itself.  Ideally, Kite9 could be used to build other user interfaces.  This is certainly an idea I've had before.  Let's see if we can make this work.

Ok, on with the show...
