package com.kite9.k9server.security.user_repo;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.io.Resources;
import com.kite9.k9server.domain.User;
import com.kite9.k9server.security.Hash;
import com.kite9.k9server.security.auth.WebSecurityConfig;
import com.kite9.k9server.web.HttpException;
import com.kite9.k9server.web.NotificationResource;

/**
 * Email validation and password reset functionality.
 * 
 * @author robmoffat
 */
@Controller
@RequestMapping(path="/api/users")
public class UserController implements ResourceProcessor<PersistentEntityResource>{
	
	
	private final UserRepository userRepository;
	private final String emailValidationRequestTemplate;
	private final String passwordResetRequesTemplate;
	private final String passwordResetForm;
	
	private static final Log LOG = LogFactory.getLog(UserController.class);
	public static final String PASSWORD_RESET_REL = "password-reset";
	public static final String VALIDATE_REL = "validate";
	public static final String URL_PREFIX = "/{email}";
	public static final String EMAIL_VALIDATION_RESPONSE_URL = "/email-validation-response";
	public static final String EMAIL_VALIDATION_REQUEST_URL = "/email-validation-request";
	public static final String PASSWORD_RESET_RESPONSE_URL = "/password-reset-response";
	public static final String PASSWORD_RESET_FORM_URL = "/password-reset-form";
	public static final String PASSWORD_RESET_REQUEST_URL = "/password-reset-request";
	
	@Autowired
	public UserController(UserRepository ur) throws IOException {
		this.userRepository = ur;
		emailValidationRequestTemplate = Resources.toString(this.getClass().getResource("/email-validation-request.txt"), Charset.defaultCharset());
		passwordResetRequesTemplate = Resources.toString(this.getClass().getResource("/password-reset-request.txt"), Charset.defaultCharset());
		passwordResetForm = Resources.toString(this.getClass().getResource("/password-reset-form.txt"), Charset.defaultCharset());
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
	
	@Autowired
    private JavaMailSender mailSender;

	/**
	 * Sends an email validation request.
	 */
	@RequestMapping(path = URL_PREFIX+EMAIL_VALIDATION_REQUEST_URL, method=RequestMethod.GET) 
	public @ResponseBody NotificationResource emailValidationRequest(@PathVariable("email") String email) {
		User u = userRepository.findByEmail(email);
		if (u == null) {
			throw new HttpException(HttpStatus.BAD_REQUEST, "User not found");
		}
		if (u.isEmailVerified()) {
			throw new HttpException(HttpStatus.BAD_REQUEST, "Email is already validated");
		}

		String code = generateValidationCode(u, EMAIL_VALIDATION_RESPONSE_URL);
        String responseUrl = createUserControllerUrl(u.getEmail(), code, EMAIL_VALIDATION_RESPONSE_URL);
		return sendTemplatedEmail(u, responseUrl.toString(), emailValidationRequestTemplate, "Kite9 - Email Validation Request");
	}
	
	/**
	 * Handles the response to the email request, when the user clicks the link in their inbox.
	 */
	@RequestMapping(path = URL_PREFIX+EMAIL_VALIDATION_RESPONSE_URL, method=RequestMethod.GET) 
	public @ResponseBody NotificationResource emailValidationResponse(@RequestParam("code") String code, @PathVariable("email") String email) {
		User u = getUserAndValidateWithCode(code, email, EMAIL_VALIDATION_RESPONSE_URL);
		u.setEmailVerified(true);
		u.setSalt(User.createNewSalt());
		userRepository.saveInternal(u);
		return new NotificationResource("Email validated");
	}

	private User getUserAndValidateWithCode(String code, String email, String forPath) {
		User u = userRepository.findByEmail(email);
		WebSecurityConfig.checkUser(u, false);
		String expectedCode = generateValidationCode(u, forPath);
		if (!expectedCode.equals(code)) {
			throw new HttpException(HttpStatus.BAD_REQUEST, "Code supplied to validate email address doesn't match expected");
		}
		return u;
	}
	
	/**
	 * Sends an email to the user containing the password reset request link.
	 */
	@RequestMapping(path = URL_PREFIX+PASSWORD_RESET_REQUEST_URL, method=RequestMethod.GET) 
	public @ResponseBody NotificationResource passwordResetRequest(@PathVariable("email") String email) {
		User u = userRepository.findByEmail(email);
		WebSecurityConfig.checkUser(u, true);	
		String code = generateValidationCode(u, PASSWORD_RESET_RESPONSE_URL);
	    String responseUrl = createUserControllerUrl(u.getEmail(), code, PASSWORD_RESET_FORM_URL);
	    return sendTemplatedEmail(u, responseUrl, passwordResetRequesTemplate, "Kite9 - Password Reset Request");
	}

	/**
	 * This actually works a lot better than using the HATEOAS methodOn and Link objects.
	 */
	public static String createUserControllerUrl(String email, String code, String path) {
		String url = ControllerLinkBuilder.linkTo(UserController.class).toString();
		return url + "/"+email+path+(code != null ? "?code="+code : "");
	}

	/**
	 * Handles displaying the password reset form when the user clicks the reset request link.
	 * @throws IOException 
	 */
	@RequestMapping(path = URL_PREFIX+PASSWORD_RESET_FORM_URL, method= {RequestMethod.GET}) 
	public void passwordResetForm(@RequestParam("code") String code, @PathVariable("email") String email, HttpServletResponse response) throws IOException {
		String url = createUserControllerUrl(email, null, PASSWORD_RESET_RESPONSE_URL);
		String html = passwordResetForm.replace("{code}", code).replace("{url}", url).replace("{email}", email);
		response.setContentType(MediaType.TEXT_HTML_VALUE);
		response.getWriter().write(html);
		response.setStatus(HttpStatus.OK.value());
	}
	
	/**
	 * Handles the post of the password reset form.
	 */
	@RequestMapping(path = URL_PREFIX+PASSWORD_RESET_RESPONSE_URL, method= {RequestMethod.GET, RequestMethod.POST}) 
	public @ResponseBody NotificationResource passwordResetResponse(@RequestParam("code") String code, @PathVariable("email") String email, @RequestParam("password") String newPassword) {
		try {
			User u = getUserAndValidateWithCode(code, email, PASSWORD_RESET_RESPONSE_URL);
			String passwordHash = Hash.generatePasswordHash(newPassword);
			u.setPassword(passwordHash);
			u.setSalt(User.createNewSalt());
			userRepository.saveInternal(u);
			return new NotificationResource("Password updated");
		} catch (Exception e) {
			throw new HttpException("Failed to update password: "+e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Sends a generic email to the user, containing a URL to click on.
	 */
	private NotificationResource sendTemplatedEmail(User u, String responseUrl, String template, String subject) {
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
	
		return new NotificationResource("Please check your email for a message from Kite9 Support.");
	}
	
	@Autowired
	EntityLinks entityLinks;

	@Override
	public PersistentEntityResource process(PersistentEntityResource resource) {
		if (resource.getContent() instanceof User) {
			User user = (User) resource.getContent();
			String email = user.getEmail();
			if (!user.isEmailVerified()) {
				Link l = new Link(createUserControllerUrl(email, null, EMAIL_VALIDATION_REQUEST_URL), VALIDATE_REL);
				resource.add(l);
			}
			
			Link l = new Link(createUserControllerUrl(email, null, PASSWORD_RESET_REQUEST_URL), PASSWORD_RESET_REL);
			resource.add(l);
		}
		
		return resource;
	}
}
