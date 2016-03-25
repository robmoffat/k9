---
layout: vision
title: Unique Features
---

# Kite9's Unique Features

Kite9 is designed around an algorithm which can intelligently lay out a diagram as well as you can.

This gives rise to several unique possibilities, most of which *simply can't be done in other diagramming software*, because they lack the layout algorithm:

## Collaboration

Since Kite9 can lay out diagrams for you, you can work *in real time* on the diagram:  
 - You don't have to spend time moving all elements around so the diagram looks 'right' every time you change something.
 - You don't have to adjust the sizes of elements on the diagram just because you added some text to them or changed a label.  It's all done for you.
 - Because Kite9 runs in the browser, editors can work together to change a diagram.   Kite9 interleaves the changes and pushes the changes to each participant.
 
## Usability

Creating diagrams in Kite9 is initially confusing as it doesn't work like other tools.  Sometimes, users push back against this, but within a short period it's easy to see how Kite9 is helping you, rather than making your life harder.  

The [existing Kite9](http://kite9.com) demonstrates the basics of interaction, but the new version will go much further in terms of providing theming, improved layouts and better support for multiple users.

## Composability

Kite9 is designed around the concept of **projects** - sets of related diagrams.  It's perfectly possible to **compose** a new diagram using **entities** already described by existing diagrams.  

## Consistency

**Entities** can be shared between diagrams.  When you change one diagram, the others will update their representations to match.

Diagrams follow **constraints** too:   just as databases and XML documents have schemas, so do Kite9 diagrams.  You can design a schema for your own purposes, or use pre-existing ones.  

## Version Control

Kite9 remembers the entire historic state of your diagrams in it's database.  This means it's possible to go back in time and view the changes between different versions graphically. 

## Programmatic Diagram Generation

Because Kite9 publishes a REST API, it's possible for other tools to use this to add diagrams to a project themselves.   

As an example of this, Kite9 includes a tool to allow you to describe a diagram within your Java code and post it to the Kite9 server to join a project.

## Large-Scale Organisation

Modern software engineering has several common notions:
 - *releases*: software components are released when built.
 - *version numbers*: the releases of the software are numbered, so you can see how it evolves from one version to another
 - *dependencies*: one software library may have a dependency on another
 - *repositories*: these are public directories of released software versions.  New releases are held here.
 
Kite9 brings all of these organisational concepts to the modelling world:  you can divide your models up into different projects (which are worked on by different teams).  These teams can *publish* *releases*, which other projects can uses as *dependencies*.   

By this manner, the same organising techniques we use on our software projects can be brought to bear on our efforts at modelling large organisations.




