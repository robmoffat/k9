package com.kite9.k9server.security;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.io.Resources;

@Controller
public class PagesController {

	private final String passwordResetForm;
	public static final String PASSWORD_RESET_FORM = "/public/users/password-reset-form";
	
	public PagesController() throws IOException {
		super();
		passwordResetForm = Resources.toString(this.getClass().getResource("/password-reset-form.txt"), Charset.defaultCharset());
	}

	/**
	 * Handles displaying the password reset form when the user clicks the reset request link.
	 */
	@RequestMapping(path = UserController.PASSWORD_RESET_FORM_URL, method= {RequestMethod.GET}) 
	public  ResponseEntity<String> passwordResetForm(@RequestParam("code") String code, @RequestParam("email") String email) throws IOException {
		String html = passwordResetForm.replace("{code}", code).replace("{email}", email).replace("{url}", UserController.PASSWORD_RESET_RESPONSE_URL);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<String>(html, headers, HttpStatus.OK);
	}
	
}
