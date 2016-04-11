# 3rd April 2016: Sprint 4: Security #

## Goals of the Sprint: 

- User entity, creatable, queryable via rest, tested.
- Sign Up Screen:  *Name*, *Email Address*, *Password*.  This would send an email out to *confirm* the email address.  
- A special URL would confirm the email, being a hash of some secret salt and their details.
- Edit screen:  user is allowed to go onto their page and change the email, but that invalidates it again (meaning we don't send to it).
- Log-in Screen (steal these from the existing grails app for now)
- Limiting the projects you can look up, based on who you are.
- Need to check email works
- Secure the application, with at least one secure page.


## Step 1: User Entity.

This is already in the system, from sprint 1, but not tested (so, assume broken).   I am going to change it around a bit though:

```java
@Entity
public class User extends AbstractLongIdEntity {

	
	/**
	 * Users can call themselves anything.  We'll use email address to log in.
	 */
	private String username;

	
	private String password;
	
	/**
	 * Users have to provide a unique email address.  But, we will validate that it belongs to them 
	 * as well.
	 */
	@Column(unique=true, length=70, nullable=false)
	private String email;

	/**
	 * This will be used as an API key, when calling the REST services.
	 */
	@Column(length=32, nullable=false)
	private String api;
	
	private boolean accountExpired = false;
	private boolean accountLocked = false;
	private boolean passwordExpired = false;
	private boolean emailable = true;
	private boolean emailVerified=false;
```

We're going to need a slightly different REST api,  We could go crazy, and implement an `@Controller` from scratch, but Spring also has `@RestController`, which might be a better fit, or this [answer](http://stackoverflow.com/questions/22824840/when-to-use-restcontroller-vs-repositoryrestresource) implies 

So, if I query the endpoint /users, I get back:

```
{
  "_embedded" : {
    "users" : [ {
      "username" : "test1",
      "password" : "blah",
      "email" : "test1@kite9.com",
      "api" : "19griouprbiam29uiq30h81m",
      "accountExpired" : false,
      "accountLocked" : false,
      "passwordExpired" : false,
      "emailable" : true,
      "emailVerified" : false,
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/api/users/1"
        },
        "user" : {
          "href" : "http://localhost:8080/api/users/1"
        }
      }
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/api/users"
    },
    "profile" : {
      "href" : "http://localhost:8080/api/profile/users"
    }
  }
}
```

Really, we *never* want people to query users via the REST URL in this way.  What are the operations we actually do want?

- Unsecured: 
 - **Create User**:  it should be possible to post in a new user, with an email, username and password.
 - Probably some static pages, error pages etc.

- Password Secured (i.e. Form-Based):
 - **Get API Key**:  returns the API key, when you give the email address and password (i.e. log in).   Can only log in if account not locked, expired, and password not expired.

- Api Key Secured:
 - **Update User**:  change the username, email, password etc, using the api key as verification it's you.
 - **Validate Email-Send**: Sends you an email with a link to validate your email address.
 - **Validate Email**: you provide some proof-of-address.
 - **Password Reset Request**: you provide an email, and it sends a password reset email, and sets "passwordExpired" to true.  It also changes the password to something random.   
 - **Update Password**: you provide a new password, and a proof-of-address.
 - and pretty much the entire rest of the application. 
 
## Proof-Of-Address

This is emailed to you.  It consists of a hash of username, password, email address and api key.  

## Step 2: Implementing A Controller

Because I don't want the basic methods of the `User` repository (GET, POST, DELETE etc) exposed, I need to stop them being exported automatically by Spring.

This can be done like so:

```
@Component
@RepositoryRestResource(exported=false)
public interface UserRepository extends CrudRepository<User, Long> {

}
```

Now, if I were simply *adding* methods to the `UserRepository`, I could write a controller with the annotation `@RespositoryRestController`, and this would make it a simple job to add some extra methods.
However, because I've got the `exported=false` part on the above repository, I'm *not allowed* to use one of these:  it simply fails to run my methods, and I get errors.  So, the way around this problem
is to implement it with `@BasePathAwareController`, which at least understands that it is a REST controller:

```
@BasePathAwareController
public class UserController {

	private final UserRepository userRepository;
	
	@Autowired
	public UserController(UserRepository ur) {
		this.userRepository = ur;
	}
	
	@RequestMapping(path = "/users/create", method=RequestMethod.GET) 
    public @ResponseBody ResponseEntity<User> createUser(
    		@RequestParam(name="username") String username, 
    		@RequestParam(name="password") String password, 
    		@RequestParam(name="email") String email) {
		User u = new User(username, password, email);
		userRepository.save(u);
		return ResponseEntity.ok(u);
	}
}
```

So, all I am doing here is simply exposing an API interface that allows you to create a user by specifying username, password and email address.  

If the user enters an email address that already exists in the system, then a huge exception ensues and is thrown back to the client.  This isn't great, so I added this check:

```
	User existing = userRepository.findByEmail(email);
	if (existing != null) {
		return new ResponseEntity<User>(HttpStatus.CONFLICT);
	}
```

There is always the possibility that someone creates *a lot* of users.  We'll cross this bridge when we come to it.

You get back something like:

```
{
  "id" : 1,
  "username" : "rob",
  "password" : "bob",
  "email" : "ttt",
  "api" : "4ecp09bvdqege20d4qg4j9i7j7",
  "accountExpired" : false,
  "accountLocked" : false,
  "passwordExpired" : false,
  "emailable" : true,
  "emailVerified" : false
}
```

Which is *still* not great:  we definitely don't want email address or password *ever* shown.  Let's deal with this now.

```java
	@JsonIgnore
	private String password;
```

and the same on email, gives:

```
{
  "id" : 1,
  "username" : "rob",
  "api" : "qekffvemipgt46dui2mn1m2fdj",
  "accountExpired" : false,
  "accountLocked" : false,
  "passwordExpired" : false,
  "emailable" : true,
  "emailVerified" : false
}
```

### Adding A Test

Following the pattern of the existing REST tests, I added this:

```
	@Test
	public void testCreateUser() {
		Map<String, String> vars = new HashMap<>();
		String username = "Joe Bloggs";
		String password = "Elephant";
		String email = "joe@example.com";
		
		String url = urlBase + "/api/users/create?username="+username+"&password="+password+"&email="+email;
		
		ResponseEntity<User> uOut = restTemplate.getForEntity(url, User.class, vars);
		
		User u = uOut.getBody();
		Assert.assertEquals(username, u.getUsername());
		Assert.assertNotNull(u.getApi());
	}
```	

### Encrypting The Password

It's a bad idea to store passwords as plaintext in the database, so I am going to hash them and store that.  

My code to do this looks like this:

```
	public static String generatePasswordHash(String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(password);
	}
```

This uses the standard Spring BCrypt encoder, which is designed for passwords.

## Coding **Validate Email-Send**

This is where it gets interesting.  We only want users who are logged in to be able to call this service.  So, that means that we need to pass the authentication information 
through.  Since REST is supposed  to be stateless, to do this, we are going to pass the API key through as a header parameter (Authorization).  This seems to me to be a 
good approach to the [problems described here](http://stackoverflow.com/questions/319530/restful-authentication).

The one drawback of this approach is that someone could re-use your cookie elsewhere if they wanted to.  This isn't really regarded as a problem generally for web services, 
and I don't think it really should be here, either.  We can always add a "Log Out" feature which invalidates their API key, if needed.

### How Does It Work?  Using cURL

Trying this:

```
curl -v -H  "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==" http://localhost:8080/api/projects
```

Means that I get an object back in Spring like this:

```
authentication	UsernamePasswordAuthenticationToken  (id=9357)	
	authenticated	false	
	authorities		Collections$EmptyList<E>  (id=9359)	
	credentials		"open sesame" (id=9360)	
	details			WebAuthenticationDetails  (id=9361)	
	principal		"Aladdin" (id=9362)	
```

So, that's BASIC auth going on, and it's using a class called `BasicAuthenticationFilter`.  I am now going to create my own.

### Kite9ApiBasedAuthenticationFilter

This is a filter I have designed, to cope with situations where the `Authorization` http header field is set with a value starting with 'KITE9' (and then the API key... for now.  Possibly we will
encode this in some way later.)

This has some code like this: 

```java
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException { (1)
		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith(KITE9_AUTH_PREFIX)) {
			String apiKey = header.substring(KITE9_AUTH_PREFIX.length());  (2)
	
			try {
	
				if (authenticationIsRequired(apiKey)) {
					ApiKeyAuthentication authRequest = new ApiKeyAuthentication(apiKey);
					authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
					Authentication authResult = authenticationManager.authenticate(authRequest);
					SecurityContextHolder.getContext().setAuthentication(authResult);
				}
	
			} catch (AuthenticationException failed) {
				SecurityContextHolder.clearContext();
				authenticationEntryPoint.commence(request, response, failed);
				return;
			}
		}

		filterChain.doFilter(request, response);  (3)
	}
```

1. Because it extends `OncePerRequestFilter`, this is the only method that needs to be implemented.  
2. It looks for the apiKey, and if one exists, it creates an `ApiKeyAuthentiation' object.  This is just a simple implementation of Spring's `Authentication` interface, and that's used by an `AuthenticationProvider`.
3. This continues on the request chain, for the main processing of the servlets.

### Configuring URL Pipeline

To configure this in the servlet filter pipeline, I need to change my `WebSecurityConfig` configuration.

I need to be able to:

 - Have the new authentication filter
 - Allow access to 'public' urls, without the filter, but 
 - only allow accesss to api methods behind the authenticated role.

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	protected void configure(HttpSecurity http) throws Exception {
		LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/login");   (1)
		Kite9ApiKeyBasedAuthenticationFilter kite9ApiFilter = 
				new Kite9ApiKeyBasedAuthenticationFilter(authenticationManager(), entryPoint);       
		
		http.addFilterAfter(kite9ApiFilter, BasicAuthenticationFilter.class);	//  API-key approach (2)
		http.csrf().disable();
		http.formLogin();  (3)
		http.authorizeRequests()   (4)
			.antMatchers("/api/public/**").permitAll()
			.antMatchers("/**").authenticated();
	}
```

1.  In the case of login failures, this is where the kite9ApiFilter will send you.
2.  Configuring the Kite9 login filter as part of the servlet chain.
3.  Also allowing form login - although this will need more configuration later.
4.  This says:  allow any public api URLs for anyone, anything else needs authentication.  Again, more work will be needed here.









