export default {
  "background": {
    "@class": "java.awt.LinearGradientPaint",
    "attr": {
      "fill": "270-#d2d2d2-#fafafa:0-#d2d2d2"
    }
  },
  "boxFills": [
    {
      "Yellow": {
        "attr": {
          "fill": "270-#ffff87-#ffe65f"
        }
      },
      "Orange": {
        "attr": {
          "fill": "270-#fff233-#fbaa24"
        }
      },
      "Light Blue": {
        "attr": {
          "fill": "270-#33ebff-#24a5fb"
        }
      },
      "Grey": {
        "attr": {
          "fill": "270-#b6b6b6-#808080"
        }
      },
      "Dark Grey": {
        "attr": {
          "fill": "270-#5b5b5b-#404040"
        }
      },
      "Dark Blue": {
        "attr": {
          "fill": "270-#68a0d3-#497094"
        }
      },
      "Red": {
        "attr": {
          "fill": "270-#ff0000-#df0000"
        }
      }
    }
  ],
  "connectionBodyDefaultShape": {
    "@class": "org.kite9.diagram.visualization.display.java2d.style.shapes.RoundedRectFlexibleShape",
    "marginX": 0.0,
    "marginY": 0.0,
    "context": true,
    "a": 8
  },
  "connectionBodyStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-width": 0,
      "fill": "black",
      "fill-opacity": 1,
      "stroke": "none"
    },
    "castsShadow": true,
    "invisible": false,
    "filled": true,
    "labelTextFormat": {
      "attr": {
        "font-size": 12,
        "font-family": "Arial",
        "fill": "white",
        "fill-opacity": 1
      },
      "justification": "CENTER",
      "baseline": 12,
      "height": 15
    }
  },
  "connectionLabelDefaultShape": {
    "@class": "org.kite9.diagram.visualization.display.java2d.style.shapes.RoundedRectFlexibleShape",
    "marginX": 0.0,
    "marginY": 0.0,
    "context": true,
    "a": 3
  },
  "connectionLabelStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-dasharray": "",
      "stroke-width": 0,
      "fill": "none",
      "stroke": "none"
    },
    "castsShadow": false,
    "invisible": false,
    "filled": true,
    "labelTextFormat": {
      "attr": {
        "font-size": 12,
        "font-family": "Arial",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "LEFT",
      "baseline": 12,
      "height": 15
    }
  },
  "connectionTemplates": [
    {
      "BASIC": {
        "fromTerminator": "NONE",
        "toTerminator": "NONE",
        "linkStyle": "",
        "linkShape": "NORMAL"
      },
      "NOTE": {
        "fromTerminator": "NONE",
        "toTerminator": "BARBED ARROW",
        "linkStyle": "",
        "linkShape": "DOTTED"
      },
      "INHERITANCE": {
        "fromTerminator": "NONE",
        "toTerminator": "ARROW OPEN",
        "linkStyle": "",
        "linkShape": "NORMAL"
      },
      "COMPOSITION": {
        "fromTerminator": "NONE",
        "toTerminator": "DIAMOND OPEN",
        "linkStyle": "",
        "linkShape": "NORMAL"
      },
      "CONSTRUCTION": {
        "fromTerminator": "NONE",
        "toTerminator": "DIAMOND",
        "linkStyle": "",
        "linkShape": "NORMAL"
      },
      "DEPENDENCY": {
        "fromTerminator": "NONE",
        "toTerminator": "BARBED ARROW",
        "linkStyle": "",
        "linkShape": "NORMAL"
      }
    }
  ],
  "contextBoxDefaultShape": {
    "@class": "org.kite9.diagram.visualization.display.java2d.style.shapes.RoundedRectFlexibleShape",
    "marginX": 0.0,
    "marginY": 0.0,
    "context": true,
    "a": 20
  },
  "contextBoxInvisibleStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-opacity": 1,
      "stroke-dashoffset": 0,
      "stroke-linejoin": "miter",
      "stroke-dasharray": "..-",
      "stroke-width": 0.5,
      "fill": "none",
      "stroke": "rgb(160,160,255)",
      "stroke-linecap": "square",
      "stroke-miterlimit": 10
    },
    "castsShadow": false,
    "invisible": true,
    "filled": true
  },
  "contextBoxStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-opacity": 1,
      "stroke-dashoffset": 0,
      "stroke-linejoin": "miter",
      "stroke-dasharray": "",
      "stroke-width": 2,
      "fill": "none",
      "stroke": "rgb(50,50,50)",
      "stroke-linecap": "square",
      "stroke-miterlimit": 10
    },
    "castsShadow": false,
    "invisible": false,
    "filled": true
  },
  "contextLabelStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-dasharray": "",
      "stroke-width": 0,
      "fill": "white",
      "fill-opacity": 1,
      "stroke": "none"
    },
    "castsShadow": false,
    "invisible": false,
    "filled": true,
    "labelTextFormat": {
      "attr": {
        "font-size": 12,
        "font-family": "Arial",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "CENTER",
      "baseline": 12,
      "height": 15
    }
  },
  "copyrightStyle": {
    "attr": {
      "font-size": 13,
      "font-family": "Arial",
      "fill": "rgb(130,130,130)",
      "fill-opacity": 1
    },
    "justification": "CENTER",
    "baseline": 12,
    "height": 15
  },
  "debugLinkStroke": {
    "@class": "java.awt.BasicStroke",
    "dashPhase": 0.0,
    "endCap": 2,
    "lineJoin": 0,
    "lineWidth": 1.0,
    "miterLimit": 10.0
  },
  "debugTextFont": {
    "@class": "org.kite9.diagram.visualization.display.java2d.style.LocalFont",
    "baselineProportion": 0.922,
    "heightProportion": 1.118
  },
  "flexibleShapes": [
    {
      "fcCONNECTOR": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 0.0,
          "bottom": 10.0,
          "left": 0.0
        }
      },
      "fcDATA": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 10.0,
          "bottom": 0.0,
          "left": 10.0
        }
      },
      "fcDATABASE": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 30.0,
          "right": 0.0,
          "bottom": 15.0,
          "left": 0.0
        }
      },
      "fcDECISION": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": false
      },
      "fcDELAY": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": false,
        "a": false,
        "b": true
      },
      "fcDIRECT": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 5.0,
          "bottom": 0.0,
          "left": 5.0
        }
      },
      "fcDISPLAY": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 5.0,
          "right": 10.0,
          "bottom": 5.0,
          "left": 10.0
        }
      },
      "fcDOCUMENT": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 0.0,
          "bottom": 10.0,
          "left": 0.0
        }
      },
      "fcINTERNAL": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 5.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 5.0
        }
      },
      "fcLOOP LIMIT": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 5.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        }
      },
      "fcMANUAL INPUT": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 10.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        }
      },
      "fcMANUAL OPERATION": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 10.0,
          "bottom": 0.0,
          "left": 10.0
        }
      },
      "fcPREDEFINED": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 10.0,
          "bottom": 0.0,
          "left": 10.0
        }
      },
      "fcPREPARATION": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "a": 10.0
      },
      "fcPROCESS": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "a": 2
      },
      "fcREFERENCE": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": false
      },
      "fcSEQUENTIAL": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 60.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        },
        "a": 40.0
      },
      "fcSTART1": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": false
      },
      "fcSTART2": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 60.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        },
        "a": 40.0
      },
      "fcSTORED DATA": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 10.0,
          "bottom": 0.0,
          "left": 10.0
        }
      },
      "fcTERMINATOR": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": false,
        "a": true,
        "b": true
      },
      "umlACTOR": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 45.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        },
        "a": 40.0
      },
      "umlCLASS": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "a": 0
      },
      "umlCOMPONENT": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 20.0
        },
        "a": 10.0
      },
      "umlCONTAINER": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 10.0,
          "right": 10.0,
          "bottom": 0.0,
          "left": 0.0
        }
      },
      "umlINTERFACE1": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "a": 10
      },
      "umlINTERFACE2": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 45.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        },
        "a": 20.0
      },
      "umlNOTE": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 0.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        },
        "a": 10.0
      },
      "umlPACKAGE": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "reserved": {
          "top": 10.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        },
        "a": 5.0,
        "b": 0.5
      },
      "umlUSECASE": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": false
      },
      "CIRCLE": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": false
      },
      "DEFAULT": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "a": 20
      },
      "DIAMOND": {
        "marginX": 10.0,
        "marginY": 10.0,
        "context": false
      },
      "DIVIDER": {
        "margin": {
          "top": 0.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        }
      },
      "ELLIPSE": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": false
      },
      "HEXAGON": {
        "marginX": 0.0,
        "marginY": 0.0,
        "context": true,
        "a": 20.0
      }
    }
  ],
  "fontFamilies": [
    {
      "Arial Bold": {
        "baselineProportion": 0.922,
        "heightProportion": 1.118
      },
      "Arial": {
        "baselineProportion": 0.922,
        "heightProportion": 1.118
      }
    }
  ],
  "glyphBoxStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-opacity": 1,
      "stroke-dashoffset": 0,
      "stroke-linejoin": "miter",
      "stroke-dasharray": "none",
      "stroke-width": 2,
      "fill": "white",
      "fill-opacity": 1,
      "stroke": "black",
      "stroke-linecap": "square",
      "stroke-miterlimit": 10
    },
    "castsShadow": true,
    "invisible": false,
    "filled": true,
    "labelTextFormat": {
      "attr": {
        "font-size": 16,
        "font-family": "Arial",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "CENTER",
      "baseline": 15,
      "height": 19
    },
    "typeTextFormat": {
      "attr": {
        "font-size": 15,
        "font-family": "Arial Bold",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "CENTER",
      "baseline": 14,
      "height": 17
    }
  },
  "glyphCompositionalShapeStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-opacity": 1,
      "stroke-dashoffset": 0,
      "stroke-linejoin": "miter",
      "stroke-dasharray": "",
      "stroke-width": 2,
      "fill": "none",
      "stroke": "black",
      "stroke-linecap": "square",
      "stroke-miterlimit": 10
    },
    "castsShadow": false,
    "invisible": true,
    "filled": true
  },
  "glyphDefaultShape": {
    "@class": "org.kite9.diagram.visualization.display.java2d.style.shapes.RoundedRectFlexibleShape",
    "marginX": 0.0,
    "marginY": 0.0,
    "context": true,
    "a": 20
  },
  "glyphTextLineStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-width": 0,
      "fill": "none",
      "stroke": "none"
    },
    "castsShadow": false,
    "invisible": false,
    "filled": true,
    "labelTextFormat": {
      "attr": {
        "font-size": 13,
        "font-family": "Arial",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "LEFT",
      "baseline": 12,
      "height": 15
    },
    "typeTextFormat": {
      "attr": {
        "font-size": 13,
        "font-family": "Arial",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "LEFT",
      "baseline": 12,
      "height": 15
    }
  },
  "gridSize": 10,
  "id": "basic",
  "interSymbolPadding": 2.0,
  "keyBoxDefaultShape": {
    "@class": "org.kite9.diagram.visualization.display.java2d.style.shapes.RoundedRectFlexibleShape",
    "marginX": 15.0,
    "marginY": 15.0,
    "context": true,
    "a": 0
  },
  "keyBoxStyle": {
    "margin": {
      "top": 15.0,
      "right": 15.0,
      "bottom": 15.0,
      "left": 15.0
    },
    "attr": {
      "stroke-opacity": 1,
      "stroke-dashoffset": 0,
      "stroke-linejoin": "miter",
      "stroke-dasharray": "none",
      "stroke-width": 2,
      "fill": "white",
      "fill-opacity": 1,
      "stroke": "black",
      "stroke-linecap": "square",
      "stroke-miterlimit": 10
    },
    "castsShadow": true,
    "invisible": false,
    "filled": true,
    "labelTextFormat": {
      "attr": {
        "font-size": 13,
        "font-family": "Arial",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "LEFT",
      "baseline": 12,
      "height": 15
    },
    "typeTextFormat": {
      "attr": {
        "font-size": 13,
        "font-family": "Arial Bold",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "CENTER",
      "baseline": 12,
      "height": 15
    }
  },
  "keyDividerStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-opacity": 1,
      "stroke-dashoffset": 0,
      "stroke-linejoin": "miter",
      "stroke-dasharray": "",
      "stroke-width": 2,
      "fill": "none",
      "stroke": "black",
      "stroke-linecap": "square",
      "stroke-miterlimit": 10
    },
    "castsShadow": false,
    "invisible": true,
    "filled": true
  },
  "keyInternalSpacing": 10,
  "keySymbolStyle": {
    "margin": {
      "top": 0.0,
      "right": 0.0,
      "bottom": 0.0,
      "left": 0.0
    },
    "attr": {
      "stroke-dasharray": "",
      "stroke-width": 0,
      "fill": "none",
      "stroke": "none"
    },
    "castsShadow": false,
    "invisible": false,
    "filled": true,
    "labelTextFormat": {
      "attr": {
        "font-size": 13,
        "font-family": "Arial",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "LEFT",
      "baseline": 12,
      "height": 15
    },
    "typeTextFormat": {
      "attr": {
        "font-size": 13,
        "font-family": "Arial",
        "fill": "black",
        "fill-opacity": 1
      },
      "justification": "LEFT",
      "baseline": 12,
      "height": 15
    }
  },
  "linkEndSize": 8.0,
  "linkHopSize": 5.0,
  "linkStyles": [
    {
      "DOTTED": {
        "attr": {
          "stroke-opacity": 1,
          "stroke-dashoffset": 0,
          "stroke-linejoin": "miter",
          "stroke-dasharray": "10,10",
          "stroke-width": 1,
          "fill": "none",
          "stroke": "black",
          "stroke-linecap": "square",
          "stroke-miterlimit": 10
        },
        "castsShadow": true,
        "invisible": false,
        "filled": true
      },
      "INVISIBLE": {
        "attr": {
          "stroke-opacity": 1,
          "stroke-dashoffset": 0,
          "stroke-linejoin": "miter",
          "stroke-dasharray": "..-",
          "stroke-width": 2,
          "fill": "none",
          "stroke": "lime",
          "stroke-linecap": "square",
          "stroke-miterlimit": 10
        },
        "castsShadow": false,
        "invisible": true,
        "filled": true
      },
      "NORMAL": {
        "attr": {
          "stroke-opacity": 1,
          "stroke-dashoffset": 0,
          "stroke-linejoin": "miter",
          "stroke-dasharray": "none",
          "stroke-width": 3,
          "fill": "none",
          "stroke": "black",
          "stroke-linecap": "square",
          "stroke-miterlimit": 10
        },
        "castsShadow": true,
        "invisible": false,
        "filled": true
      }
    }
  ],
  "linkTerminatorStyles": [
    {
      "ARROW": {
        "attr": {
          "stroke-dashoffset": 0,
          "stroke-linejoin": "miter",
          "stroke-dasharray": "none",
          "stroke-width": 1,
          "fill": "none",
          "stroke": "none",
          "stroke-linecap": "square",
          "stroke-miterlimit": 10
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M0 8 L-4 8 L0 0 L4 8 Z",
        "margin": {
          "top": 0.0,
          "right": 4.0,
          "bottom": 8.0,
          "left": 4.0
        },
        "filled": true
      },
      "ARROW OPEN": {
        "attr": {
          "stroke-dashoffset": 0,
          "stroke-linejoin": "miter",
          "stroke-dasharray": "none",
          "stroke-width": 1,
          "fill": "white",
          "fill-opacity": 1,
          "stroke": "none",
          "stroke-linecap": "square",
          "stroke-miterlimit": 10
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M0 8 L-4 8 L0 0 L4 8 Z",
        "margin": {
          "top": 0.0,
          "right": 4.0,
          "bottom": 8.0,
          "left": 4.0
        },
        "filled": true
      },
      "CIRCLE": {
        "attr": {
          "stroke-width": 0,
          "fill": "none",
          "stroke": "none"
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M4 0 C4 2.2091 2.2091 4 0 4 C-2.2091 4 -4 2.2091 -4 0 C-4 -2.2091 -2.2091 -4 0 -4 C2.2091 -4 4 -2.2091 4 0 Z",
        "margin": {
          "top": 0.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        },
        "filled": true
      },
      "GAP": {
        "attr": {
          "stroke-width": 0,
          "fill": "none",
          "stroke": "none"
        },
        "castsShadow": true,
        "invisible": false,
        "margin": {
          "top": 0.0,
          "right": 3.0,
          "bottom": 3.0,
          "left": 3.0
        },
        "filled": false
      },
      "NONE": {
        "attr": {
          "stroke-width": 0,
          "fill": "none",
          "stroke": "none"
        },
        "castsShadow": true,
        "invisible": false,
        "margin": {
          "top": 0.0,
          "right": 0.0,
          "bottom": 0.0,
          "left": 0.0
        },
        "filled": false
      },
      "DIAMOND": {
        "attr": {
          "stroke-width": 0,
          "fill": "none",
          "stroke": "none"
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M0 16 L-4 8 L0 0 L4 8 Z",
        "margin": {
          "top": 0.0,
          "right": 4.0,
          "bottom": 8.0,
          "left": 4.0
        },
        "filled": true
      },
      "DIAMOND OPEN": {
        "attr": {
          "stroke-dashoffset": 0,
          "stroke-linejoin": "miter",
          "stroke-dasharray": "none",
          "stroke-width": 1,
          "fill": "white",
          "fill-opacity": 1,
          "stroke": "none",
          "stroke-linecap": "square",
          "stroke-miterlimit": 10
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M0 16 L-4 8 L0 0 L4 8 Z",
        "margin": {
          "top": 0.0,
          "right": 4.0,
          "bottom": 8.0,
          "left": 4.0
        },
        "filled": true
      },
      "BARBED ARROW": {
        "attr": {
          "stroke-dashoffset": 0,
          "stroke-linejoin": "miter",
          "stroke-dasharray": "none",
          "stroke-width": 1,
          "fill": "none",
          "stroke": "none",
          "stroke-linecap": "square",
          "stroke-miterlimit": 10
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M0 8 L0 0 L-4 8 M0 0 L4 8 M0 0",
        "margin": {
          "top": 0.0,
          "right": 4.0,
          "bottom": 8.0,
          "left": 4.0
        },
        "filled": false
      }
    }
  ],
  "shadowColour": {
    "attr": {
      "fill": "#808080"
    }
  },
  "shadowXOffset": 3,
  "shadowYOffset": 3,
  "symbolShapes": [
    {
      "HEXAGON": {
        "attr": {
          "stroke-width": 0,
          "fill": "black",
          "fill-opacity": 1,
          "stroke": "none"
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M13.9952 11.25 L13.9952 3.75 L7.5 0 L1.0048 3.75 L1.0048 11.25 L7.5 15 L13.9952 11.25",
        "filled": true
      },
      "DIAMOND": {
        "attr": {
          "stroke-width": 0,
          "fill": "black",
          "fill-opacity": 1,
          "stroke": "none"
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M7.5 15 L15 7.5 L7.5 0 L0 7.5 L7.5 15",
        "filled": true
      },
      "CIRCLE": {
        "attr": {
          "stroke-width": 0,
          "fill": "black",
          "fill-opacity": 1,
          "stroke": "none"
        },
        "castsShadow": true,
        "invisible": false,
        "path": "M14.5 7.5 C14.5 11.366 11.366 14.5 7.5 14.5 C3.634 14.5 0.5 11.366 0.5 7.5 C0.5 3.634 3.634 0.5 7.5 0.5 C11.366 0.5 14.5 3.634 14.5 7.5 Z",
        "filled": true
      }
    }
  ],
  "symbolSize": 15.0,
  "symbolTextStyle": {
    "attr": {
      "font-size": 12,
      "font-family": "Arial",
      "fill": "white",
      "fill-opacity": 1
    },
    "justification": "CENTER",
    "baseline": 12,
    "height": 15
  },
  "watermarkColour": {
    "attr": {
      "fill": "#000000"
    }
  }
}