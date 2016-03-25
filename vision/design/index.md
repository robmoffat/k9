---
layout: vision
title: Kite9 Design
---

# Kite9 Design

The design of Kite9 is basically around three parts:

## The Data Store

Kite9 has an increment-only data-model that handles change-over-time.   It has a [data model](data_model) to track the **entities** and **diagrams** in your project, and deal with versioning both past and potential future states of systems.  

## The Layout Engine

The [layout engine](layout) is the secret sauce of Kite9: it contains proprietary algorithms not available elsewhere designed for outputting high-quality SVG, PDF and PNG-format diagrams.

## The Interaction Layer

Kite9 supports two kinds of interaction:  
 - **human interaction**, via a browser-based [real-time diagram editor](user_interface).
 - **machine interaction** via secure [REST API](rest_api), which allows other software systems to post data to the Data Store, which can be then rendered by the Rendering Engine.  

 