package com.kite9.k9server.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.kite9.k9server.command.domain.NewDocument;
import com.kite9.k9server.command.domain.RenameDocument;
import com.kite9.k9server.command.revision.Redo;
import com.kite9.k9server.command.revision.Undo;
import com.kite9.k9server.command.xml.AppendXML;
import com.kite9.k9server.command.xml.Copy;
import com.kite9.k9server.command.xml.Delete;
import com.kite9.k9server.command.xml.Move;
import com.kite9.k9server.command.xml.Replace;
import com.kite9.k9server.command.xml.SetAttr;
import com.kite9.k9server.command.xml.SetStyle;
import com.kite9.k9server.command.xml.SetText;
import com.kite9.k9server.command.xml.adl.ADLDelete;
import com.kite9.k9server.command.xml.adl.ADLMoveCells;
import com.kite9.k9server.command.xml.adl.ADLReplace;
import com.kite9.k9server.command.xml.adl.CopyLink;

/**
 * Performs some change on the ADL.
 *  
 * @author robmoffat
 *
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type", visible=true)
@JsonSubTypes({
	// xml specific
	@Type(Delete.class), 
	@Type(Copy.class), 
	@Type(Move.class), 
	@Type(SetText.class),
	@Type(SetAttr.class),
	@Type(Replace.class),
	@Type(AppendXML.class),
	@Type(SetStyle.class),

	// ADL-Specific
	@Type(CopyLink.class),
	@Type(ADLDelete.class),
	@Type(ADLReplace.class),
	@Type(ADLMoveCells.class),

	// document-specific
	//@Type(Undo.class),
	//@Type(Redo.class),
	
	// domain-specific
	//@Type(RenameDocument.class),
	//@Type(NewDocument.class)
})

@JsonAutoDetect(fieldVisibility=Visibility.ANY, 
	getterVisibility=Visibility.NONE, 
	setterVisibility =Visibility.NONE)
public interface Command {
					
	public Object applyCommand() throws CommandException;
	
}
