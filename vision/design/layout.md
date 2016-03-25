---
layout: vision
title: Kite9 Layout
---

# About The Kite9 Layout Engine

The Kite9 layout engine is the secret sauce of the whole system.  Diagram layout is a *hard, unsolved problem* in computer science.  A lot of diagramming systems provide some optional level of automated layout, but because it is such a hard problem, they usually allow the user to override the algorithm when things get tough.

## What Kite9 Does Differently

Kite9 goes further and *insists* on the algorithm.   This makes the problem harder, because it means that it's not allowed to fail and do worse than a person could do on their own.

But there is a good reason for this insistence:  As soon as you allow a user to set the *exact* positions of elements, all the value is lost, because you can't then control sizing, and routing of lines, and you can't update diagrams automatically.  

The trick is therefore to give users *enough* control so the diagram looks how they want it, but not *so much* control that they end up having to do everything themselves.  Broadly, this corresponds to:
 - Saying what elements are connected together, but not how those connections route around the diagram.
 - Saying an element must be *above*, or *to the left of* another, but not give an exact position. 

## Orthogonal Layout

Kite9 is designed around support for [Orthogonal, or "Grid Plan" Layout](https://en.wikipedia.org/wiki/Grid_plan). Which is ideal for most software diagrams.  

It will automatically handle hierarchies, containers, swim-lane diagrams, grids, and the routing of links around the diagram to be as efficient as possible.

Since the shape and size of the diagram is dependent on what's in it, Kite9 automatically right-sizes your diagram, and lays it out again *after every change you make*.   This obviously means Kite9 needs to not only draw diagrams correctly but quickly.

## Palettes

Users can design [palettes of their own shapes](../schemas_palettes_behaviours) for Kite9, meaning Kite9's layout engine can be repurposed to drawing any kind of software diagram.


## Programmatic Interface ##

Kite9 has a simple Web API (using [REST](http://rest.elkstein.org)).  This allows other software compoonents (including the [user interface](../user_interface)) can contribute new versions and changes to diagrams.

This means that finally people and systems can both contribute diagrams into Kite9.