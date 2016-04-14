package com.kite9.k9server.security;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.io.Resources;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.repos.RestDataConfig;
import com.kite9.k9server.repos.UserRepository;

@BasePathAwareController
public class UserController {

	private final UserRepository userRepository;
	private final String emailValidationRequestTemplate;
	private final String passwordResetRequesTemplate;
	
	private static final Log LOG = LogFactory.getLog(UserController.class);
	public static final String EMAIL_VALIDATION_RESPONSE_URL = "/public/users/email-validation-response";
	public static final String EMAIL_VALIDATION_REQUEST_URL = "/public/users/email-validation-request";
	public static final String PASSWORD_RESET_RESPONSE_URL = "/public/users/password-reset-response";
	public static final String PASSWORD_RESET_FORM_URL = "/public/users/password-reset-form";
	public static final String PASSWORD_RESET_REQUEST_URL = "/public/users/password-reset-request";
	
	@Autowired
	public UserController(UserRepository ur) throws IOException {
		this.userRepository = ur;
		emailValidationRequestTemplate = Resources.toString(this.getClass().getResource("/email-validation-request.txt"), Charset.defaultCharset());
		passwordResetRequesTemplate = Resources.toString(this.getClass().getResource("/password-reset-request.txt"), Charset.defaultCharset());
	}
	
	/**
	 * Public API - doesn't need security.
	 * You can call this with curl like:
	 * <pre>
	 * curl -v -H "Content-Type: application/json" -d '{ "username" : "bob", "password": "pass", "email" : "rob@kite9.com" }' http://localhost:8080/api/public/users
	 * </pre>
	 */
	@RequestMapping(path = "/public/users", method=RequestMethod.POST) 
    public @ResponseBody ResponseEntity<User> createUser(@RequestBody User newUser) {
		String email = newUser.getEmail();
		String password = newUser.getPassword();
		String username = newUser.getUsername();
		User existing = userRepository.findByEmail(email);
		if (existing != null) {
			return new ResponseEntity<User>(HttpStatus.CONFLICT);
		}
		
		String passwordHash = Hash.generatePasswordHash(password);
		
		User u = new User(username, passwordHash, email);
		userRepository.save(u);
		return ResponseEntity.ok(u);
	}
	
	/** 
	 * Returns just your logged-in user.  
	 */
	@RequestMapping(path = "/users", method=RequestMethod.GET) 
    public @ResponseBody ResponseEntity<User> retrieveUser(Principal p) {
		if (p instanceof Authentication) {
			User u = (User) ((Authentication) p).getPrincipal();
			return ResponseEntity.ok(u);
		}
		
		return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
	}
	
	/**
	 * If a party knows the user's details (including their private API key) then they can
	 * validate an email address this way.   Is this something we want?
	 */
	public static String generateValidationCode(User principal, String path) {
		String email = principal.getEmail();
		String api = principal.getApi();
		String salt = principal.getSalt();
		String source = email+"|"+api+"|"+salt+"|"+path;
		LOG.info("Hashing: "+source);
		String code = Hash.generateHash(source);
		return code;
	}
	
	/**
	 * Returns the URL required to validate the email is correct
	 */
	private String createResponseURL(User principal, HttpServletRequest request, String path) {
		String serverName = request.getServerName();
		int port = request.getServerPort();
		String scheme = request.getScheme();
		String email = principal.getEmail();
		String basePath = RestDataConfig.REST_API_BASE;
		String code = generateValidationCode(principal, path);
		return scheme+"://"+serverName+":"+port+basePath+path+"?email="+email+"&code="+code;
	}
	
	@Autowired
    private JavaMailSender mailSender;

	/**
	 * Sends an email validation request.
	 */
	@RequestMapping(path = EMAIL_VALIDATION_REQUEST_URL, method=RequestMethod.GET) 
	public @ResponseBody ResponseEntity<String> emailValidationRequest(String email, HttpServletRequest request) throws IOException {
		User u = userRepository.findByEmail(email);
		if (u == null) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		if (u.isEmailVerified()) {
			return new ResponseEntity<String>("Email is already validated", HttpStatus.BAD_REQUEST);
		}
		
        String responseUrl = createResponseURL(u, request, EMAIL_VALIDATION_RESPONSE_URL);
		return sendTemplatedEmail(u, responseUrl, emailValidationRequestTemplate, "Kite9 - Email Validation Request");
	}
	
	/**
	 * Handles the response to the email request, when the user clicks the link in their inbox.
	 */
	@RequestMapping(path = EMAIL_VALIDATION_RESPONSE_URL, method=RequestMethod.GET) 
	public @ResponseBody ResponseEntity<String> emailValidationResponse(@RequestParam("code") String code, @RequestParam("email") String email) throws IOException {
		User u = userRepository.findByEmail(email);
		WebSecurityConfig.checkUser(u);
		
		String expectedCode = generateValidationCode(u, EMAIL_VALIDATION_RESPONSE_URL);
		if (!expectedCode.equals(code)) {
			return new ResponseEntity<String>("Code supplied to validate email address doesn't match expected", HttpStatus.BAD_REQUEST);
		}
		
		u.setEmailVerified(true);
		u.setSalt(User.createNewSalt());
		userRepository.save(u);
		return ResponseEntity.ok("Email validated");
	}
	
	/**
	 * Sends an email to the user containing the password reset request link.
	 */
	@RequestMapping(path = PASSWORD_RESET_REQUEST_URL, method=RequestMethod.GET) 
	public @ResponseBody ResponseEntity<String> passwordResetRequest(String email, HttpServletRequest request) throws IOException {
		User u = userRepository.findByEmail(email);
		WebSecurityConfig.checkUser(u);		
	    String responseUrl = createResponseURL(u, request, PASSWORD_RESET_FORM_URL);
	    return sendTemplatedEmail(u, responseUrl, passwordResetRequesTemplate, "Kite9 - Password Reset Request");
	}
	
	
	/**
	 * Handles the post of the password reset form.
	 */
	@RequestMapping(path = PASSWORD_RESET_RESPONSE_URL, method= {RequestMethod.GET, RequestMethod.POST}) 
	public @ResponseBody() ResponseEntity<String> passwordResetResponse(@RequestParam("code") String code, @RequestParam("email") String email, @RequestParam("password") String newPassword) throws IOException {
		User u = userRepository.findByEmail(email);
		WebSecurityConfig.checkUser(u);
		
		String expectedCode = generateValidationCode(u, PASSWORD_RESET_FORM_URL);
		if (!expectedCode.equals(code)) {
			return new ResponseEntity<String>("Code supplied to validate email address doesn't match expected", HttpStatus.BAD_REQUEST);
		}
		
		String passwordHash = Hash.generatePasswordHash(newPassword);

		u.setPassword(passwordHash);
		u.setSalt(User.createNewSalt());
		userRepository.save(u);
		return ResponseEntity.ok("Password updated");
	}
	
	/**
	 * Sends a generic email to the user, containing a URL to click on.
	 */
	private ResponseEntity<String> sendTemplatedEmail(User u, String responseUrl, String template, String subject) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(RecipientType.TO, new InternetAddress(u.getEmail()));
                mimeMessage.setFrom(new InternetAddress("support@kite9.com"));
                mimeMessage.setSubject(subject);
				mimeMessage.setText(template.replace("{username}", u.getUsername()) + responseUrl);
            }
        };
        
        mailSender.send(preparator);
        
        LOG.info("Emailed "+u.getEmail()+" with url: "+responseUrl);
	
		return ResponseEntity.ok("Please check your email for a message from Kite9 Support.");
	}
}
