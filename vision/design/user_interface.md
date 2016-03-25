---
layout: vision
title: User Interface
---

# The Kite9 User Interface

## Supporting Multiple Diagram Types

The Kite9 user interface is flexible enough that we built the user interface for Kite9 *in Kite9 itself*:  since Kite9 **Palettes** [support adding behaviours](../schemas_palettes_behaviours), we were able to add functionality for choosing diagrams, making changes 

## Supporting Multiple Users

Kite9 works on the web, and supports any number of users viewing and editing a diagram.  Changes to the diagram are sent to the server via it's REST API in the form of "commands".  The server applies the commands to the model, and then pushes the changes back to the clients via Web Sockets, so they see the changes locally as soon as they happen.

If users try to perform conflicting changes, Kite9 will notice this and block commands that are running on out-of-date state.

## Web Standards & Animation

Kite9 uses SVG to display the diagram elements.  This is part of the HTML5 standard, and is supported by all modern browsers.  SVG animates nicely from one version of the diagram to the next, so you can see exactly what changes have been made to the diagram you're working on.
