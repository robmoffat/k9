package com.kite9.k9server.security;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.domain.user.User;

/** 
 * Provides utility function for adding user details to the ADL meta and the headers.
 * 
 * @author robmoffat
 *
 */
public class Kite9HeaderMeta {

	public static void addRegularMeta(ADL t, String self, String change, String title) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof OAuth2AuthenticationToken) {
			OAuth2User user = (OAuth2User) authentication.getPrincipal();
			t.setMeta("user", user.getAttribute("name"));
			t.setMeta("user-icon", user.getAttribute("avatar_url"));
			t.setMeta("user-page", "/api/users/"+authentication.getName());
			
		} else {
			t.setMeta("user", "anonymousUser");
		}
		if (self != null) {
			t.setMeta("self", self);
		}
		if (change != null) {
			t.setMeta("content", change);
		}
		if (title != null) {
			t.setMeta("title", title);
		}
	}
	
	public static void addRegularMeta(HttpHeaders headers, String self, String change, String title) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		headers.add("kite9-user", authentication.getName());
		
		if (authentication.getDetails() instanceof User) {
			headers.add("kite9-user-icon", ((User)authentication.getDetails()).getIcon());
			headers.add("kite9-user-page", ((User)authentication.getDetails()).getLocalId());
		}
		
		if (self != null) {
			headers.add("kite9-self", self);
		}
		if (change != null) {
			headers.add("kite9-content", change);
		}
		if (title != null) {
			headers.add("kite9-title", title);
		}
	}
	
	public static void transcribeMetaToHeaders(ADL t, HttpHeaders headers) {
		for (Map.Entry<String, String> item : t.getMetaData().entrySet()) {
			headers.set("kite9-"+item.getKey(), item.getValue());
		}
	}
}
