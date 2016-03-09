package com.kite9.k9server.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
     * Json-serialized version of the (rendered) diagram.
     */
    String renderedJson;
    
    /**
	 * Generates the SHA-1 hash of the document.
	 */
	public static String generateHash(String document) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] data = document.getBytes();
			byte[] out = md.digest(data);

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < out.length; i++) {
				sb.append(Integer.toString((out[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Algorithm doesn't exist!", e);
		}
	}
}
