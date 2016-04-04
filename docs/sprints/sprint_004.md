# 3rd April 2016: Sprint 4: Security #

## Goals of the Sprint: 

- User entity, creatable, queryable via rest, tested.
- Sign Up Screen:  *Name*, *Email Address*, *Password*.  This would send an email out to *confirm* the email address.  
- A special URL would confirm the email, being a hash of some secret salt and their details.
- Edit screen:  user is allowed to go onto their page and change the email, but that invalidates it again (meaning we don't send to it).
- Log-in Screen (steal these from the existing grails app for now)
- Limiting the projects you can look up, based on who you are.
- Need to check email works

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

- **Create User**:  it should be possible to post in a new user, with an email, username and password.
- **Get API Key**:  returns the API key, when you give the email address and password (i.e. log in).   Can only log in if account not locked, expired, and password not expired.
- **Update User**:  change the username, email, password etc, using the api key as verification it's you.
- **Validate Email**: you provide some proof-of-address.
- **Password Reset Request**: you provide an email, and it sends a password reset email, and sets "passwordExpired" to true.  It also changes the password to something random.   
- **Update Password**: you provide a new password, and a proof-of-address.

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

It's a bad idea to store passwords as plaintext in the database, so I am going to hash them with SHA-1 and store that.  

My code to do this looks like this:

```
	 * Generates the SHA-1 hash of the document.
	 */
	public String generateHash(String document) {
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
```

Is SHA-1 the right algorithm?  Not sure.  Probably it should be something else, I will look at this.



