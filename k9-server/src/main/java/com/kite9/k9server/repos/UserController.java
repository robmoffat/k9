package com.kite9.k9server.repos;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.io.Resources;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.security.Hash;
import com.kite9.k9server.security.WebSecurityConfig;

@BasePathAwareController
public class UserController {

	private final UserRepository userRepository;
	private final String validationRequestTemplate;
	
	private static final Log LOG = LogFactory.getLog(UserController.class);
	
	@Autowired
	public UserController(UserRepository ur) throws IOException {
		this.userRepository = ur;
		validationRequestTemplate = Resources.toString(this.getClass().getResource("/email-validation-request.txt"), Charset.defaultCharset());
	}
	
	/**
	 * Public API - doesn't need security
	 */
	@RequestMapping(path = "/public/users/create", method=RequestMethod.GET) 
    public @ResponseBody ResponseEntity<User> createUser(
    		@RequestParam(name="username") String username, 
    		@RequestParam(name="password") String password, 
    		@RequestParam(name="email") String email) {
		
		User existing = userRepository.findByEmail(email);
		if (existing != null) {
			return new ResponseEntity<User>(HttpStatus.CONFLICT);
		}
		
		String passwordHash = Hash.generatePasswordHash(password);
		
		User u = new User(username, passwordHash, email);
		userRepository.save(u);
		return ResponseEntity.ok(u);
	}
	
	@RequestMapping(path = "/users/retrieve", method=RequestMethod.GET) 
    public @ResponseBody ResponseEntity<User> retrieveUser(Principal p) {
		if (p instanceof Authentication) {
			User u = (User) ((Authentication) p).getPrincipal();
			return ResponseEntity.ok(u);
		}
		
		return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
	}
	
	@Autowired
    private JavaMailSender mailSender;
	
	
	private String generateValidationCode(User principal) {
		String email = principal.getEmail();
		String api = principal.getApi();
		String salt = ""+principal.getUsername().hashCode()+"|sk&HFSJNA";
		
		String code = Hash.generateHash(email+"|"+api+"|"+salt);
		return code;
	}
	
	private String createResponseURL(User principal, HttpServletRequest request) {
		String serverName = request.getServerName();
		int port = request.getServerPort();
		String scheme = request.getScheme();
		Long id = principal.getId();
		String code = generateValidationCode(principal);
		return scheme+"://"+serverName+":"+port+"/public/users/email-validation-response?id="+id+"&code="+code;
	}
	
	/**
	 * Sends a validation request, but only to users who are logged in.
	 */
	@RequestMapping(path = "/users/email-validation-request", method=RequestMethod.GET) 
	public @ResponseBody ResponseEntity<String> emailValidationRequest(Principal authentication, HttpServletRequest request) throws IOException {
		
		if (authentication instanceof Authentication) {
			User u = (User) ((Authentication)authentication).getPrincipal();
			WebSecurityConfig.checkUser(u);
			MimeMessagePreparator preparator = new MimeMessagePreparator() {

	            public void prepare(MimeMessage mimeMessage) throws Exception {
	                mimeMessage.setRecipient(RecipientType.TO, new InternetAddress(u.getEmail()));
	                mimeMessage.setFrom(new InternetAddress("support@kite9.com"));
	                mimeMessage.setSubject("Kite9 - Email Validation Request");
	                mimeMessage.setText(validationRequestTemplate.replace("{username}", u.getUsername()) + createResponseURL(u, request));
	            }
	        };
	        
	        mailSender.send(preparator);
	        
	        LOG.info("Emailed "+u.getEmail()+" with email validation request");
		
			return ResponseEntity.ok("Please check your email for a message from Kite9 Support.");
			
		}
		
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		
	}
	
	@RequestMapping(path = "/public/users/email-validation-response", method=RequestMethod.GET) 
	public @ResponseBody ResponseEntity<String> emailValidationResponse(@RequestParam("code") String code, @RequestParam("id") Long id) throws IOException {
		User u = userRepository.findOne(id);
		WebSecurityConfig.checkUser(u);
		
		String expectedCode = generateValidationCode(u);
		if (!expectedCode.equals(code)) {
			return new ResponseEntity<String>("Code supplied to validate email address doesn't match expected", HttpStatus.BAD_REQUEST);
		}
		
		u.setEmailVerified(true);
		userRepository.save(u);
		return ResponseEntity.ok("Email validated");
	}
}
