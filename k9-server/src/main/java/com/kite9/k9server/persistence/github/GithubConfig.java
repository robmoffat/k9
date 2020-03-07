package com.kite9.k9server.persistence.github;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.util.StreamUtils;

import com.kite9.k9server.command.content.ContentAPIFactory;
import com.kite9.k9server.persistence.queue.ChangeQueue;

/**
 * This configures the basic API to talk to github.
 * 
 * @author robmoffat
 */
public class GithubConfig implements InitializingBean {
	
	
	
	@Autowired
	ResourceLoader resourceLoader;
	
	public String pem;
	public PrivateKey pk;
	
	@Value("${kite9.app-key-location:file:kite9-automatic-diagrams.2020-02-04.private-key.pem}")
	private String privateKeyLocation;
	
	
	public void afterPropertiesSet() throws IOException {
		pem = StreamUtils.copyToString(resourceLoader.getResource(privateKeyLocation).getInputStream(), Charsets.UTF_8);
		pk = createPrivateKeyFromString(pem);
	}

	@Bean(name = "githubContentAPIFactory")
	public ContentAPIFactory createGithubAPIFactory() throws IOException {
		
				
		return new GithubContentAPIFactory(this, changeQueues);
	}
	
	 // PKCS#8 format
    static final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
    static final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

    // PKCS#1 format
    static final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
    static final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";
    
    static final String PEM_CERT_START = "-----BEGIN CERTIFICATE-----";
    static final String PEM_CERT_END = "-----END CERTIFICATE-----";

    static final String PEM_PUBLIC_START = "-----BEGIN PUBLIC KEY-----";
    static final String PEM_PUBLIC_END =  "-----END PUBLIC KEY-----";

	
	protected static String removeFurniture(String in) {
		in = in.replace(PEM_PRIVATE_START, "").replace(PEM_PRIVATE_END, "");
		in = in.replace(PEM_RSA_PRIVATE_START, "").replace(PEM_RSA_PRIVATE_END, "");
		in = in.replace(PEM_PUBLIC_START, "").replace(PEM_PUBLIC_END, "");
		in = in.replace(PEM_CERT_START, "").replace(PEM_CERT_END, "");
		in = in.replaceAll("\n", "");
		in = in.replaceAll("\\s", "");
		return in;
	}

	public static RSAPrivateCrtKey createPrivateKeyFromString(String privateKeyPem) {
        if (privateKeyPem.indexOf(PEM_PRIVATE_START) != -1) { // PKCS#8 format
            privateKeyPem = removeFurniture(privateKeyPem);
    		byte[] bytes = Base64.getDecoder().decode(privateKeyPem);
            return createPrivateKeyFromPKCS8(bytes);
        } else if (privateKeyPem.indexOf(PEM_RSA_PRIVATE_START) != -1) {  // PKCS#1 format
            privateKeyPem = removeFurniture(privateKeyPem);
    		byte[] bytes = Base64.getDecoder().decode(privateKeyPem);
            return createPrivateKeyFromPKCS1(bytes);
        } else {
            throw new UnsupportedOperationException("Not supported format of a private key", null);
        }
    }
	
	private static RSAPrivateCrtKey createPrivateKeyFromPKCS1(byte[] pkcs1Bytes) {
		int pkcs1Length = pkcs1Bytes.length;

		int totalLength = pkcs1Length + 22;
		byte[] pkcs8Header = new byte[] { 0x30, (byte) 0x82, (byte) ((totalLength >> 8) & 0xff),
				(byte) (totalLength & 0xff), // Sequence + total length
				0x2, 0x1, 0x0, // Integer (0)
				0x30, 0xD, 0x6, 0x9, 0x2A, (byte) 0x86, 0x48, (byte) 0x86, (byte) 0xF7, 0xD, 0x1, 0x1, 0x1, 0x5, 0x0, // Sequence:
																														// 1.2.840.113549.1.1.1,
																														// NULL
				0x4, (byte) 0x82, (byte) ((pkcs1Length >> 8) & 0xff), (byte) (pkcs1Length & 0xff) // Octet string +
																									// length
		};
		byte[] pkcs8bytes = join(pkcs8Header, pkcs1Bytes);
		return createPrivateKeyFromPKCS8(pkcs8bytes);
	}

	private static byte[] join(byte[] byteArray1, byte[] byteArray2){
	    byte[] bytes = new byte[byteArray1.length + byteArray2.length];
	    System.arraycopy(byteArray1, 0, bytes, 0, byteArray1.length);
	    System.arraycopy(byteArray2, 0, bytes, byteArray1.length, byteArray2.length);
	    return bytes;
	}
		
	protected static RSAPrivateCrtKey createPrivateKeyFromPKCS8(byte[] pkcs8Bytes) {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			return (RSAPrivateCrtKey) factory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8Bytes));
		} catch (Exception e) {
			throw new UnsupportedOperationException("Couldn't create private key", e);
		}
	}

}
