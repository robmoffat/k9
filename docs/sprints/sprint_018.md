# Editor 17th February 2018

- We’re going to need a lot of the original app now.  So, we need to structure this a bit better, and use webpack / npm stuff.
- Add in a javascript testing framework.

## Overview

So, as described above, we want to be able to create an application in which we can use.  Since *everything* is controlled
using CSS, but we now need to add in Javascript behaviours.

Also, we need to consider the difference between *updates* and loading the whole page.  Here are some use-cases:

1.  Person hits a URL, and it loads one of our SVG documents (latest revision of).  The content-type is set to HTML, so it
loads embedded in an HTML frame.  It looks nice, and everyone is happy.  Somehow, we specify *javascript behaviours* on the page, and 
these get loaded in and applied to the dom tree.  Elements flash as you hover, etc.

- Library:  basicCanvas(), addScreenFurniture(), select(), classMatch()

2.  Somehow, we embed a form in the page for login.  Person enters details and they get logged in.  This is a POST action.  But, 
the resultant returned object is SVG.   The HTML page needs to know how to re-render (process animations, etc).  So, this means
that there should be a javascript library brought in to handle that.

 - Library:   fetch(), animateUpdate(somesvg)
 
3.  The next page that comes back is a list of their projects (again, rendered using Kite9).   They can scroll through 
the list, and click the one they want, or add a new one.  So, we're going to need some simple translation between the project
objects and the Kite9 representation of those.  (So, transform the Projects into some ADL, and return it).

4.  Same for Documents.

5.  Person loads the doc.  Again, the screen animates.  However, more javascript is needed now:  let's say they're an *editor*.  

- Library:  edit(), popup(), hover(), select()

So, let's start at the beginning, and figure this out.

## Problem 1:  Javascript

I can easily return an HTML page with embedded SVG canvas.  That's no problem.  It'd be easy to just add the Javascript to 
the top of the page, but that's a non-starter because as you move through the pages, you're going to end up needing different
bits of Javascript.  And, we need to allow people to embed their javascript from anywhere.

I'm favouring one of two solutions:  embed into the CSS or, have a special tag that we use for this.  There is already supports
the `<script>` tag, so the principle of least surprise suggests using this.

Let's try this.

*If we set this in the CSS, could it get loaded in the HTML page?*  


