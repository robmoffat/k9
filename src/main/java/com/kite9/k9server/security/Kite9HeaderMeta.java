package com.kite9.k9server.security;

import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.kite9.k9server.adl.holder.ADL;

/** 
 * Provides utility function for adding user details to the ADL meta and the headers.
 * 
 * @author robmoffat
 *
 */
public class Kite9HeaderMeta {

	public static void addRegularMeta(ADL t, String self, String title) {
		perform((k, v) -> t.setMeta(k, v), self, title);
	}
	
	public static void addRegularMeta(HttpHeaders headers, String self, String title) {
		perform((k, v) -> headers.add("kite9-"+k, v), self, title);
	}
	
	public static void transcribeMetaToHeaders(ADL t, HttpHeaders headers) {
		for (Map.Entry<String, String> item : t.getMetaData().entrySet()) {
			headers.set("kite9-"+item.getKey(), item.getValue());
		}
	}
		
	private static void perform(BiConsumer<String, String> consumer, String self, String title) {	
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof OAuth2AuthenticationToken) {
			OAuth2User user = (OAuth2User) authentication.getPrincipal();
			consumer.accept("user", user.getAttribute("name"));
			consumer.accept("user-icon", user.getAttribute("avatar_url"));
			consumer.accept("user-page", "/home");
		} else {
			consumer.accept("user", "anonymousUser");
		}

		if (self != null) {
			consumer.accept("self", self);
		}
		if (title != null) {
			consumer.accept("title", title);
		}
	}
	

}
