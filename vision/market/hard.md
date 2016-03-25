---
layout: vision
title: Why Other Tools Aren't Like Kite9
---

# Reason for the Failure – Hard in 3 Ways

Many diagrams you create in Visio have a geographic component of them
(such as designing a golf course or a kitchen layout).  However, lots don't.  

But for the ones that don't, Visio and other tools still make you spend time and effort placing your elements
geographically.

The basic reason why tools like Visio fail to automatically lay out diagrams is that this is a hard computational problem to solve.

It is hard in three key ways:

## Computational Hardness

It is hard in the standard computational sense of the word: For every extra element added to the
diagram, the number of different layouts increases, in an exponential
manner. 

## Aesthetic Hardness

It is hard in a second way too: it's very difficult to encode rules into
computers to explain what is aesthetically pleasing to humans.  At the same time, it's very easy for
us to see at a glance where a layout is 'wrong' and how it should be
fixed, because our brains are wired to understand visual scenes and how
things are connected together.

## Psychological Hardness

If there are aesthetic issues in the diagram layout (due to the above two problems), people will want to tinker with the layout themselves.  This means you are re-introducing geographic information.  

Getting people to give up geographic control of the diagram is a psychological barrier, the height of which is dependent on how many aesthetic issues you don't address.

Because of the difficulties in providing an aesthetically acceptable
algorithmic layout, there tends to be one of two approaches taken:

### Don't do it

This is the approach taken by Visio – all lines must be routed manually, and all content is positioned manually. 
Lots of tools take this approach and many are very successful.

### Have it as an option

Having an option is the approach used by yED (see below) and many other UML tools.The tool has a button,
which you press to lay out the diagram and position each element geographically.

Then, you can adjust the positions of the elements yourself in order to compensate for the layout engine's shortcomings.

The obvious downside of this (which makes it almost completely pointless) is that if you add some new elements to the diagram and press the 'layout' button again, you're back to square one, and you'll need to
make all your adjustments again.   All the geographic information you've added to the diagram is erased.

This means you're presented with the choice of letting the automatic layout break your work, or re-arranging everything yourself.

For this reason, this second approach is doomed to failure.

## Where We Are

In order to make automatic layout work, Kite9 must overcome all 3 kinds
of hardness.

This means producing a layout of a diagram fast, and producing it to a standard, which will
prevent people wanting to tinker with it manually.

## The Kite9 Core Competency

Kite9 is a state-of-the-art diagram layout engine, which contains
features not present in any commercially available software, but which
are nevertheless critical in overcoming the 3 hardness barriers to
automatic diagram generation.