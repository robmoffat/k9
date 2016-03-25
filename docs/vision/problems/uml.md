# Problems With UML

*Note:  This was taken from an old, much longer article about the issues with UML. *

## Problem 1: Mystery Meat

UML has lots of mystery meat. There are diamonds (filled and unfilled), various different types of arrow (including filled heads and outline heads), dotted lines, stick men, boxes, ovals and so on.  Only UML experts know what these mean, and this means that people who aren't "in the know" are excluded from the process.

## Problem 2: UML doesn’t fit my language

UML was conceived initially for object modelling, and showing the relationships and dependencies between various objects in a system.  

But objects are not created equally in all languages:
 - languages have different primitive types
 - some languages support [Generics](https://en.wikipedia.org/wiki/Generic_programming#Generics_in_Java), some don't.
 - Languages have different classes for supporting collections, with different performance implications.

If we’re using UML to communicate to our fellow coders, or just people trying to understand what we’ve built, then there is a translation needed: you need to translate out of the native language that you wrote the code in, and turn it into UML, which everyone can read… and then back again.

## Problem 3: Code Generation Is A Bad Idea

Many UML tools support turning a UML diagram into actual code (in a langugage of your choice).

The first problem with code generation is that you don’t get something: if you didn't specify it in the diagram, it won't be in your code.  But often with UML there isn't sufficient precision in the diagram to dictate *exactly* what you need in the code, and so you end up re-coding by hand.

The second problem with code generation is that now you’ve got two versions of everything: the original UML diagram contains the model you spent hours crafting and the Java code which also contains the model you spent hours crafting… minus a few bits that didn’t make it through because the languages don’t fit (see problem 2). 

This violates the [Once and Only Once](http://c2.com/cgi/wiki?OnceAndOnlyOnce) principle.

## Problem 4: Refactoring Support

Many modern IDEs allow you to refactor the code you’ve written.  For example, you can change the name of a field in one of your objects from "name" to "fullName", and all the references to this will be updated to match, across your entire code-base.

Of course, this could include any code that the UML tool has generated too, but generally speaking, this will not include the UML model, and this introduces yet another headache for anyone working with UML: your refactoring tools are now broken.   Your UML model will still refer to something called 'name', which doesn't exist anymore.

## Problem 5: Omissions from UML

There isn’t a standardized format for UML models which allows them to be effortlessly refactored on any editor and kept in synchronization with all of the different generated code, and here is my reasoning why: the UML model really only represents *a few aspects* of the system being built. 

We don’t have a modelling language for **thread control**, or a modelling language for **log messages** or a modelling language for **transaction processing**, but we do have the UML which attempts to model a grab-bag of concerns such as message passing, state control, object model relationships and so on.

Unless, that is, we had some kind of way of making UML totally extensible, so that it could do everything a programming language could&#8230;

## Problem 5: Meta-Meta-Models Are Problematic

To address the problem of extensibility, UML adds the notion of Meta-Meta-Models.

What is a Meta-Meta-Model?  Lets say you are making a Victoria Sponge Cake. Let’s call this the model. Now, the implements that you use to construct this model are called the meta-model. For example, you may require an oven, a whisk and some heatproof gloves. Those are the parts of your meta-model.

Now what makes ovens and whisks? That would be the meta-meta-model, which is a workshop containing drills, a lathe, a jigsaw and metal cutting tools etc.

So, if you are missing certain things from the meta-model (say an oven timer) you can construct one in your workshop (the meta-meta-model).

A further slight-of-hand also means that the meta-meta-model is it’s own meta-meta-model! This is the equivalent of saying that with your workshop you can, if you want, construct other similar or even identical workshops to the one you already have.

So where’s the problem with this? In layman's terms what it means that if you want to bake the cake you could try using the garlic press and the meat cleaver that UML provides, or you can get into the workshop and fashion a baking tin yourself. 

Now this sounds unfair: Surely the UML standard is empowering tool companies to be blacksmiths and extend the UML ecosystem?  Well, yes, and of course that is what happened:  new and unusual tools were built, based on the meta-meta model, but the coherence of the standard suddenly starts to slip away. [And I&#8217;m not the first to point this out.](http://www.cit.gu.edu.au/~noran/Docs/UML-Issues.pps).

## Summary

We can do better.  Code generation, meta-models, extensions, mystery meat.. all of these *miss the point* of why we should use diagrams in software at all.

Models should be used primarily for two purposes:

### Plans

UML can be a **plan**, in the same manner as the blueprints of a house.  *It's not the house*, but it gives you some idea of what it will look like when it's built, and it gives people a line-in-the-sand from which do discuss changes.  

### Maps

UML can also document the systems we have built.  It can provide us with a **map** of how to navigate around a system.  The mistake of code generation is to *confuse the map with the territory*.   The whole point of a map is that it is a simplification of reality - it doesn't have all the detail of reality itself.  
