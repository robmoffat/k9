# Icons

[The Noun Project](thenounproject.com) has an API which will return you an icon for any noun.  And also a lot of verbs, it seems.  

This would be ideal if we want to generate icons for things pretty much automatically.   Holy fuck that's cool.

Ideally, we would download icons to store somewhere in our database table, and refer to them as entities, maybe?


## Compound Elements

This encompasses *ports* as well, which you're going to need.  This needs to come after getting two connections to meet at the same place.

We need to have a notion of the center-lines of an element, so we can align them, and say where the default ports are going to be.

But the idea is, we should be able to decorate an element, even though the element may not support this *per-se* in it's design.  For example, adding an icon to it to say "unread" or something.

These icons would change the outline of the shape.  They would attach to the shape itself, by having anchor points on both themselves and the underlying shape they are compositing with.

The user would pick up the whole thing from the bottom.  If you are connecting to a position within the shape's grid, you should be able to determine a nice relative position even if the shape changes size.

 