# 23rd January 2018: Sprint 17: Rest-Based Rendering

- some new entities:  rendered data entity.  diagram xml entity.   (should we have a single entity for hashed content?  Might be a good idea)
- currently, this is groovy code.  Refactor so this is a first-class Java, Spring service.
- Use REST, use the user token to validate requests.
- Write some tests for this.
- Store results in the content table.
- hard-code the stylesheets for now.

# 1.  Looking Back

So, I am reviewing the k9-server project that I started in March 2016.  There are a few assumptions now that seem wrong.

- First, the approach to authentication seems off:  obviously, I was trying to create a stateless architecture for logins, 
but nowadays everyone is using JWT, so really I should upgrade spring to use that (also, I have experience of using that from
work so it shouldn't be too hard.   Nevertheless, in the meantime, it'll probably do.
- Second, I've got all these `Format` classes, which map to different ways you can bring back the diagram.   SVG is now our underlying
format, and everything else should be converted from it.  This is going to require more Batik-work.  I think the alternate formats
are a good idea, but it can wait.
- All the ADL classes are gone now:  we're dealing with pure XML all the way through.  This has blown up a lot of the old code.
- We convert `svg+xml` as the input to pure SVG as the output, now, so a lot of the `MediaTypes` seem off.
- We have some front-end code using React and D3.  Like the content types, this is from [Sprint 5](sprint_005.md).  This is used to transition
between one SVG diagram and another.   I think inevitably we're going to have to change that, but right now I'm not sure how:
  - Can we have a simple transitions package that allows animation without D3 or React?  This might be good.
  - Alternatively, is there a react-like package that allows transition from one XML doc to another? 
- I think we have lots of odd code lying around that does Batik-y SVG manipulation.  We need to remove this.
- FontController is a bit anachronistic now:  everything else just uses the regular stylesheet, and `ResourceRenderer`, so we need to upgrade to that,

# 2.  Fixing the Formats

First thing is to get the `RestRenderingIT` working again, and that means fixing up the image formats.  Notably HTML, PDF and PNG are broken

