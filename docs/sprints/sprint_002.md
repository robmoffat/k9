# Sprint 2:  Markdown and  GitHub Pages #

For this sprint, I want to get the documentation in order for Kite9, so that each time I *do* some 
sprint documentation, it's got somewhere to live.

 - Sprint Articles on Github Pages
 - Some Kite9 website should point to the documentation.
 - Some vision documents
 - Marketing (SaaS) front-matter (at least a place-holder).  This was suggested by Andrew, adding it in to this sprint. 
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

After looking for an Evernote exporter, and finding nothing, I am thinking the answer might be exporting it from Wordpress.

I tried the "Export To Jekyll" plugin first, but actually this didn't really work all that well:  images didn't get exported, and, while the format of the export was good, a lot of the tags didn't get converted to markdown.  So, meh.

How about exporting from Evernote as HTML, and then converting the HTML to Markdown?  This was a good way to go.  Evernote has an export option, and then I was able to use pandoc as follows:

```
ls *.html | awk '{print "pandoc --from=html --to=markdown_strict \"" $0 "\" > \"" $0 ".md\""}' | sh
```

## GitHub Pages

Once I commit my changes, everything is visible on Github.  However, it's not as nice as GitHub pages would look, so the next job is to set that up.  Github Pages essentially allows you to store your static HTML inside a Git repository, and use this as the source for serving up webpages.  In essence, this means that you 
have *built HTML* checked into your project, on the gh-pages branch.  

Yes, this sounds really weird:  it seems to make no sense to check in anything but the *source* into Git, but nevertheless, this is the model they're going for here.  It *would* make sense if we were hand-editing HTML, but really, who has time for that in this day and age?

### Jekyll

The answer appears to be Jekyll.  From there, I can add Markdown files to my project, and then I think the github pages get built automatically without me having to do anything.  

By putting an instance of Jekyll in my `gh-pages` branch, github will *compile my markdown files* and show them as HTML at my Github pages site.   This is pretty cool, but seems like overkill.  

Also, it was *really hard* to figure out theming:  it's best to download something like [lanyan](http://lanyon.getpoole.com) and use this as a base for your `gh-pages` branch, adding your content on top.

### Vision Documentation

A lot of the material about Kite9 is looking kind of old, and actually, I don't really want to keep it.  What I need is to clearly explain:

1.  The Shortcomings of existing tools   DONE
2.  Why Kite9 addresses these shortcomings.  DONE
3.  Why this is credible. 
4.  What these tools empower, in terms of further functionality.
5.  Why this has a strong business case.
6.  The architecture of the new Kite9, and what that enables.

Ok, so I've added all of this.  The trick with Jekyll is *always remember the front-matter* and then it will generate something for you.  

### Index Page In Jekyll

It's possible to use the Liquid templates in Jekyll to index for you.  This is faq/index.html:

```
---
layout: page
title: FAQs
---

<ul>
  {% for page in site.pages %}
        {% if page.layout == 'faq' %}
          <li><a href="{{site.baseurl}}/{{ page.url }}">{{ page.title }}</a></li>
        {% endif %}   
  {% endfor %}  
</ul>
```

It simply scans through the list of pages and produces a set of links.  

### Nice Images

I added a bit of CSS to improve the look of images:

```css
.page img {
	width: 30rem;
	margin: auto;
	box-shadow: 3px 3px 14px #aaa;
}
```

This gives the images a drop-shadow, and just separates them from the body-text sufficiently to make them *definitely images*:  otherwise it's sometimes hard to tell what's an image and what isn't.

### Deploying to info.kite9.com

I added a CNAME file into the gh-pages root, containing

```
info.kite9.org
```

And now, github expects to host the repo when it's webserver sees this URL coming in.  I just need to change my DNS entries now so that info.kite9.com points to github (this is a CNAME).

To make this work properly, I needed to change my `_config.yml` file so that it expected the baseurl to be `/`.

This is basically saying which directory your files will be served from: on github, it was `/k9`. 

```
url:                 http://info.kite9.com
baseurl:             /
```

### Creating A SaaS Front Page





### Adding Tracking

 
 

 