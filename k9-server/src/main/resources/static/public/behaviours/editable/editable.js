/**
 * This composes the basic edit behaviour of the application
 */


import 

*/
@script url('/public/behaviours/editable/delete.js');
@script url('/public/commands/move/move.js');
@script url('/public/commands/link/link.js');

import { Instrumentation } from "/public/classes/instrumentation/instrumentation.js";
import { Metadata } from "/public/classes/metadata/metadata.js";



import { zoomableInstrumentationCallback } from "./zoomable.js";
import { zoomableInstrumentationCallback } from "./zoomable.js";
import { identityInstrumentationCallback, identityMetadataCallback } from "./identity.js";



import { createUndoableInstrumentationCallback, undoableMetadataCallback } from "./undoable.js";

new Metadata([undoableMetadataCallback]);
new Instrumentation([createUndoableInstrumentationCallback((e, c) => alert("Undo "+c), (e, c) => alert("Redo "+c))]);

new Instrumentation([zoomableInstrumentationCallback]);