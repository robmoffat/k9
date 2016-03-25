# The Problem #

There are 3 problems that Kite9 is trying to address:

### 1.  Diagramming Tools ###

After all these years, the tools for building diagrams to represent software systems is still, arguably, awful.  Here are a selection of the problems:

#### Manual Tools

Although tools have varying levels of support for drawing diagrams, usually, there is a lot of manual work in arranging diagrams so that they look correct.

Diagram layout is a still an area of research (we consulted a lot of it to guide the development of Kite9.  Generally though, this is poorly integrated into the 
workflow of building a diagram.  Sometimes there will be a "layout" button you can click, and everything on your diagram moves somewhere new.  Usually the results are worse than they were before you clicked the button.

#### Poor Interactivity Support

Because laying out the diagram is painstaking, it's not possible to design a diagram in real-time, on a conference call, say.   It's not possible to work with other people, or review the changes made between different versions of diagrams.  This all stems from the fact that the layout isn't always-on and automatic, as it is in Kite9.

#### Not Designed For Extensibility

Diagramming tools often have support for extensions, but usually these have to be built by the team who built the product.  By embracing web standards like SVG, CSS, JavaScript and XMLSchemas, Kite9 can be extended by the community in new directions. 

#### Not Even Designed For the Internet

Most (though not all) tools are built around desktop-deployment and files-on-a-network.  This is certainly fine for the corporate intranet, but cloud storage is the way forward.

### 2.  The Types of Diagrams We Produce Are Poor

UML has been around for *donkeys years* now.  It's the de-facto standard for Object-Oriented diagramming.   But, it's not well liked.  [I discuss some of the reasons here.](uml.md)  

In the world of software engineering, there is a trend *away* from object-oriented systems towards things like microservices, functional programming, functional purity, containerization and distributed data stores.  These new paradigms are ill-served by a diagramming standard invented in the 90's.   

Why not design a new diagramming language for one of these using Kite9?

### 3.  Diagramming vs. Collaboration

Most enterprise in the computing world is collaborative, and most of the tools we use allow collaboration.

For example, when I check code into the source control system, I can be doing this at the same time as other 
developers.  Sometimes, (if we make conflicting changes) this causes an issue.  But the mark of good source
control software is that it allows the developers to solve these issues with as little pain as possible.

Shouldn't our diagramming software support different opinions, different views on reality and different levels of understanding?




