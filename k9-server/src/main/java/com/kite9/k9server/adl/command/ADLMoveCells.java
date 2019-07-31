package com.kite9.k9server.adl.command;

import org.kite9.diagram.dom.CSSConstants;
import org.kite9.diagram.dom.elements.ADLDocument;
import org.kite9.diagram.dom.elements.StyledKite9XMLElement;
import org.kite9.diagram.dom.managers.IntegerRangeValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.command.AbstractCommand;
import com.kite9.k9server.command.CommandException;

/**
 * Makes some space in a grid for new cells to be dropped in.
 * 
 * @author robmoffat
 *
 */
public class ADLMoveCells extends AbstractCommand {

	Integer push, from;
	Boolean horiz;
	
	public ADLMoveCells() {
	}
	
	public ADLMoveCells(String fragmentId, int from, int push, boolean horiz) {
		super(fragmentId, null);
		this.from = from;
		this.horiz = horiz;
		this.push = push;
	}

	@Override
	public ADL applyCommand(ADL adl) throws CommandException {
		ensureNotNull(this, "ADLMoveCells", "push", push);
		ensureNotNull(this, "ADLMoveCells", "fragmentId", fragmentId);
		ensureNotNull(this, "ADLMoveCells", "from", from);
		ensureNotNull(this, "ADLMoveCells", "horiz", horiz);
		
		ADLDocument doc = adl.getAsDocument();
		adl.getTranscoder().ensureCSSEngine(doc);
		validateFragmentHash(adl);
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
