package com.kite9.k9server.security;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.w3c.dom.Node;

public class Hash {

	private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	private static TransformerFactory tf = TransformerFactory.newInstance();

	
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
	
	/**
	 * Generates a hash of an xml node by converting it to a string first.
	 */
	public static String generateHash(Node in) {
	    try {
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(in), new StreamResult(writer));
			return generateHash(writer.toString());
		} catch (Exception e) {
			throw new RuntimeException("Couldn't generate hash: ", e);
		}
	}
	
	public static String generatePasswordHash(String password) {
		return encoder.encode(password);
	}
	
	public static boolean checkPassword(String rawPassword, String encodedPassword) {
		return encoder.matches(rawPassword, encodedPassword);
		
	}
}
