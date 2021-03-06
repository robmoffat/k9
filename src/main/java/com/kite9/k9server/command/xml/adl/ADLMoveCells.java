package com.kite9.k9server.command.xml.adl;

import org.kite9.diagram.dom.CSSConstants;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.StyledKite9XMLElement;
import org.kite9.diagram.dom.managers.IntegerRangeValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.CommandException;
import com.kite9.k9server.command.xml.AbstractADLCommand;

/**
 * Makes some space in a grid for new cells to be dropped in.
 * 
 * @author robmoffat
 *
 */
public class ADLMoveCells extends AbstractADLCommand {

	Integer push, from;
	Boolean horiz;
	
	public ADLMoveCells() {
	}
	
	public ADLMoveCells(String fragmentId, ADL on, int from, int push, boolean horiz) {
		super(on, fragmentId);
		this.from = from;
		this.horiz = horiz;
		this.push = push;
	}

	@Override
	public ADL applyCommand() throws CommandException {
		ensureNotNull(this, "ADLMoveCells", "push", push);
		ensureNotNull(this, "ADLMoveCells", "fragmentId", fragmentId);
		ensureNotNull(this, "ADLMoveCells", "from", from);
		ensureNotNull(this, "ADLMoveCells", "horiz", horiz);
		
		ADLDocument doc = adl.getAsADLDocument();
		adl.getTranscoder().ensureCSSEngine(doc);
		Element container = doc.getElementById(fragmentId);	
		int moved = 0;
		
		NodeList contents = container.getChildNodes();
		for (int i = 0; i < contents.getLength(); i++) {
			if (contents.item(i) instanceof StyledKite9XMLElement) {
				StyledKite9XMLElement el = (StyledKite9XMLElement) contents.item(i);
				IntegerRangeValue yr = (IntegerRangeValue) el.getCSSStyleProperty(CSSConstants.GRID_OCCUPIES_Y_PROPERTY);
				IntegerRangeValue xr = (IntegerRangeValue) el.getCSSStyleProperty(CSSConstants.GRID_OCCUPIES_X_PROPERTY);
				CSSStyleDeclaration sd = el.getStyle();
				
				if ((xr != null) && (yr != null)) {
					if (horiz) {
						if (xr.getFrom() >= from) {
							xr = new IntegerRangeValue(xr.getFrom() + push, xr.getTo() + push);
							moved++;
						} else if (xr.getTo() >= from) {
							xr = new IntegerRangeValue(xr.getFrom(), xr.getTo() + push);
							moved++;
						}
						
						sd.setProperty(CSSConstants.GRID_OCCUPIES_X_PROPERTY, xr.getCssText(), "");
						el.setComputedStyleMap("", null);  // clears cache
					} else {
						if (yr.getFrom() >= from) {
							yr = new IntegerRangeValue(yr.getFrom() + push, yr.getTo() + push);
							moved++;
						} else if (yr.getTo() >= from) {
							yr = new IntegerRangeValue(yr.getFrom(), yr.getTo() + push);
							moved++;
						}
						
						sd.setProperty(CSSConstants.GRID_OCCUPIES_Y_PROPERTY, yr.getCssText(), "");
						el.setComputedStyleMap("", null);  // clears cache
					}
				}
			}
		}
		LOG.info("Processed move from "+from+" push "+push+" horiz="+horiz+",moved="+moved);
		
		return adl;
	}

	
}
