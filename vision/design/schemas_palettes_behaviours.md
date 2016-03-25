---
layout: vision
title: Customisation
---

# Schemas, Palettes and Behaviours

Kite9 broadly has 3 key methods for extension:

## Schemas

A *Schema* is another word for a *data format*.  That is, the "shape" that data takes in a system.  For example, a database might have a schema which says:

 - There are two tables, PEOPLE and FRIENDSHIPS
 - The PEOPLE table has a column for the name, and an ID.
 - The FRIENDSHIP table has two columns, which both contain IDs.
 - The FRIENDSHIP IDs must exist on the people table.
 
Kite9 allows you to define your own Schema model, to say what entities you have in your diagrams. 

## Palettes

A *Palette* is a set of symbols that can be used in a diagram.  Continuing our example, we might say that PEOPLE must be shown by a symbol of a person, and that a friendship must be shown by a double-headed arrow, like this:


In Kite9, you can *define your own palettes* by writing a Cascading Stylesheet (CSS).   CSS is a web standard for controlling the appearance of elements on a web-page.  Here, we are repurposing it to control the appearance of the elements on a diagram.   

CSS is very easy to write, and has a simple, declarative style.  

## Behaviours

When you click on an element in a Kite9 diagram, what can you do with it?  

This is a fundamental question when designing the user interface for Kite9.  For example, you might want to 
change the text, it's shape, or the colour.   Or, align it with other elements on the page.

The options are largely dependent on the *context* of what you are interacting with.  To maximise the extensibility of Kite9, it's possible to provide these behaviours to be used *on the client*.  

In fact, this is the way the Kite9 user interface works.   It means we can use the same layout engine for displaying the list of revisions as we use for editing a diagram. 

And, since we wrote Kite9 using *behaviours*, anyone can extend Kite9 in a completely new direction by writing behaviours of their own. 
 