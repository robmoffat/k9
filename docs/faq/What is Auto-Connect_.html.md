In Kite9, you don’t control the exact position of diagram elements, just
the layouts of containers, and the directions of links.  This is both a
restriction, because you lose exact control, and an advantage, because
the computer now has to worry about this stuff instead.

  

Auto-connect is the term in Kite9 for the creation of new links between
parts.  By default, this is done when you add new Parts to your diagram.
   

 

**Auto-Connect For New Elements (The Default)**

  

So, if I drop a new element on the diagram like this:

  

<img src="What%20is%20Auto-Connect_.resources/auto_connect1.png" width="1010" height="358" />

  

The diagram will be resized, and the elements lined up like this:

  

<img src="What%20is%20Auto-Connect_.resources/auto_connect2.png" width="1272" height="332" />

The link created between the two elements is done by auto-connect, and
the direction will be fixed left-to-right for the link.

  

**Auto-Connect For Existing Elements: Turned Off By Default**

**  
**

Auto-Connect is turned off for elements already on the diagram, so if I
move something around on the diagram, Kite9 will suggest new layouts,
like so new links will not be created, but you can change the layout of
the diagram.

  

<img src="What%20is%20Auto-Connect_.resources/auto_connect3.png" width="784" height="574" />

  

The dotted-blue line shows that, instead of creating a new link, this
layout will be set.  When I let go of the element, the diagram redraws
like this:

  

<img src="What%20is%20Auto-Connect_.resources/auto_connect4.png" width="594" height="730" />

The Link will now always be rendered going down.

  

**Changing Auto-Connect Settings**

**  
**

<img src="What%20is%20Auto-Connect_.resources/auto_connect5.png" width="482" height="802" />

  

On the drop-down next to the **link** button, you can change the Auto
Connect between one of three options:

  

1.  **New (the Default):  **New links are only created when you drag
    items off the chooser.  
2.  **On:**  New links will be created between the element you are
    moving, and any other element you move it near, if there is no
    pre-existing link between those elements.
3.  **Off:  **Links are not created.  Layout is still enforced by layout
    links, as shown below:

  

<img src="What%20is%20Auto-Connect_.resources/auto_connect6.png" width="480" height="472" />
