package com.kite9.k9server.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

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
	
	public static String generatePasswordHash(String password) {
		return generateHash("sdkfhsdkjfsdj324329ibas"+password);
		
	}
}
