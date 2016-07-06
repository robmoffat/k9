# 23 May 2016 : Sprint 8 - Overhaul Object Model

Notes from the plan:

- everything should be parts and containers.  Links should be reformatted.   Ideally, we are backwards-compatible with what came before.  So, you can load up the original diagram xml and it comes back in the new format.  (this means objects like Glyph still work...)
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

So, what changes do I need to see:

0.  I need a set of deterministic tests, otherwise, how can I be sure what I am doing?
1.  Everything is `Part`s and `Containers`.  
2.  Deconstructing the nature of the `Link`. 
3.  I need to be able to rely on styling information from the stylesheet (so, this means, do sprint 7)
 
