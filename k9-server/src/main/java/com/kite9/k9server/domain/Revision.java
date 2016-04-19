package com.kite9.k9server.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;


/**
 * Contains a single diagram revision.  
 */
@Entity
public class Revision extends AbstractLongIdEntity {

	@ManyToOne(targetEntity=Revision.class, optional=false, fetch=FetchType.LAZY)
    Document document;
    
    /**
     * xml defining the diagram (input)
     */
	String diagramXml;

	/**
     * hash of the xml.
     */
	@Column(length=32)
    String diagramHash;
    
    Date dateCreated = new Date();
    
    @ManyToOne(targetEntity=User.class, optional=false, fetch=FetchType.LAZY)
    User author;
    
    /**
     * XML of the (rendered) diagram.
     */
    String renderedXml;
}
