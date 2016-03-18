# Sprint 2:  Markdown and  GitHub Pages #

For this sprint, I want to get the documentation in order for Kite9, so that each time I *do* some 
sprint documentation, it's got somewhere to live.

 - Sprint Articles on Github Pages
 - Some Kite9 website should point to the documentation.
 - Some vision documents
 - Decomissioning info.kite9.com, and moving all the help articles off there.
 

## Step 1:  Placeholders 

I already have the text from the last sprint, so it's not a million miles away in terms of content.  All that's required is to push this to github and try to configure pages.  So far, i have this structure:

```
./help
./sprints
./sprints/images
./sprints/images/001_1.png
./sprints/sprint_001.md
./sprints/sprint_002.md
./vision
./vision/layout.md
./vision/problems.md
./vision/README.md
./vision/unique_features.md
```

A lot of this isn't fleshed out yet.  

## Help Documentation

So, I am missing some 'help' documentation.  I still have this from the old Kite9.  It's on a Wordpress blog served at info.kite9.com, and I initially wrote it using Evernote.  (There is a great Wordpress/Evernote sync plugin for Wordpress that I am using).  

After looking for an Evernote export, and finding nothing, I am thinking the answer might be exporting it from Wordpress.



## GitHub Pages

Once I commit my changes, everything is visible on Github.  However, it's not as nice as GitHub pages would look, so the next job is to set that up.  Github Pages essentially allows you to store your static HTML inside a Git repository, and use this as the source for serving up webpages.  In essence, this means that you 
have *built HTML* checked into your project, on the gh-pages branch.  

Yes, this sounds really weird:  it seems to make no sense to check in anything but the *source* into Git, but nevertheless, this is the model they're going for here.  It *would* make sense if we were hand-editing HTML, but really, who has time for that in this day and age?

The answer appears to be Jekyll.  From there, I can add Markdown files to my project, and then build them onto my gh-pages bran




## Re-pointing the URL

I need to move across info.kite9.com to it's new, GitHub Pages location.  

## Vision Documentation

A lot of the material about Kite9 is looking kind of old, and actually, I don't really want to keep it.  What I need is to clearly explain:

1.  The Shortcomings of existing tools
2.  Why Kite9 addresses these shortcomings.
3.  Why this is credible.
4.  What these tools empower, in terms of further functionality.
5.  Why this has a strong business case.
6.  The architecture of the new Kite9, and what that enables.




 
 

 