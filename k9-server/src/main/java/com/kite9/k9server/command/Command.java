package com.kite9.k9server.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.kite9.k9server.adl.command.ADLDelete;
import com.kite9.k9server.adl.command.ADLReplace;
import com.kite9.k9server.adl.command.CopyLink;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.document.commands.Redo;
import com.kite9.k9server.domain.document.commands.Undo;

/**
 * Performs some change on the ADL.
 *  
 * @author robmoffat
 *
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type", visible=true)
@JsonSubTypes({
	@Type(Delete.class), 
	@Type(Copy.class), 
	@Type(Move.class), 
	@Type(SetText.class),
	@Type(SetAttr.class),
	@Type(Replace.class),
	@Type(AppendXML.class),
	@Type(SetStyle.class),
	
	// document-specific
	@Type(Undo.class),
	@Type(Redo.class),
	
	// ADL-Specific
	@Type(CopyLink.class),
	@Type(ADLDelete.class),
	@Type(ADLReplace.class)
	
})
@JsonAutoDetect(fieldVisibility=Visibility.ANY, 
	getterVisibility=Visibility.NONE,
	setterVisibility =Visibility.NONE)
public interface Command {
					
	public ADL applyCommand(ADL in) throws CommandException;
	
}
