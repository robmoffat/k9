# 25th January 2018: Project CSS/JS/SVG/Icon/Font Repository

- We need an entity in the system to hold details about registered CSS stylesheets.  We will use the public URLs of these in the XML, but the actual values will be cached in the DB to speed things up.
- Should be an option to say “don’t update” or “update every…”, and the cache, when returning, will check and behave accordingly.
- So, handle this caching.
- //We need an "Entity" element in the database, which we'll also use later for indexing the XML.// Don't do this yet


# 1.  Do We Need This?

Possibly.  When we are running tests, it would be really handy to be able to use URLs that are part of a repository.  But, what's the 
workflow here?

- Someone writes a stylesheet
- They attach the stylesheet to their Kite9 diagram, and begin editing.
- They want to change the stylesheet, so maybe they upload a new version.
- We can use a CDN in front of Kite9 to cache this big stuff (eventually, so long as we don't allow changes)

What if they *don't* use the repository?  Is there any issue with loading files from elsewhere?  I am worried about an attack vector
I guess, especially since we have to load fonts.  Work happens when you load a Kite9 diagram.

In the first instance, probably, there won't be much going on, and it might be possible to have an in-memory cache of some of the fonts 
and whathaveyou.  What about images?  Templates?  These all would need to be stored too.

# 2.  Do We Need This *Now*?

The only reason I can think of why we might need this now is so that we can load stuff up for testing.  If we pull in these files 
and stick them in the repository, maybe that helps for testing.   But we could write some really minimal tests that don't use any 
URLs and just have nested stylesheet information in them.

I don't know how docker is going to handle a `file:` URL.  That'd be interesting to see.  If it worked, maybe we should dodge this step 
for now?


