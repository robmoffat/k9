package com.kite9.k9server.security;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.user.User;

/** 
 * Provides utility function for adding user details to the ADL meta and the headers.
 * 
 * @author robmoffat
 *
 */
public class Kite9HeaderMeta {

	public static void addUserMeta(ADL t) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		t.setMeta("user", authentication.getName());
		
		if (authentication.getDetails() instanceof User) {
			t.setMeta("email", Hash.generateMD5Hash(((User)authentication.getDetails()).getEmail().toLowerCase()));
		}
	}
	
	public static void addUserMeta(HttpHeaders headers) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		headers.add("kite9-user", authentication.getName());
		
		if (authentication.getDetails() instanceof User) {
			headers.add("kite9-email", Hash.generateMD5Hash(((User)authentication.getDetails()).getEmail().toLowerCase()));
		}
	}
	
	public static void transcribeMetaToHeaders(ADL t, HttpHeaders headers) {
		for (Map.Entry<String, String> item : t.getMetaData().entrySet()) {
			headers.set("kite9-"+item.getKey(), item.getValue());
		}
	}
}
