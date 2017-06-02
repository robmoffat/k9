


# 7.  Stylesheet Concerns

1. Sizing is somewhat dependent on layout.  And layout is somewhat dependent on *type*.  We need to analyse this and make sure it can't go wrong.
2. What exactly does 'Label' mean?  And 'Connected' for that matter?  Really, it's a choice of how they are laid out, so these are totally the wrong terms to
use.   We can have a label with some connected elements in it, and it lays out fine, but we can't have one with labels in it, that breaks everything.  Something must
change.  This also affects the GridPositioner. We should call it "normal" or something I guess.

