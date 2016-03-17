# 5th March 2016:  Sprint One #

The goals of this sprint are:

1.  Build a simple Spring Boot application for Kite9 - Redux. 
2.  Check this into Github.
3.  Produce some Github pages containing this blog.
4.  Add some JPA-managed entities, (the same ones as exist in the Grails Kite9)
5.  Add REST support.
6.  Write a test to ensure that:
  - We can persist some entities in the database.
  - We can retrieve the entity.
  - We can delete the entity.
  - Via REST api.
7.  The Test should be dual-purpose: it can be run as a Unit test on my developer machine or run against an integration build.
8.  Deploy Kite9 as a clustered app to some Cloud service.

## Creating a Spring Boot App ##

Spring Boot is a Java framework that does a lot of the groundwork of doing the best practices of
a Java application from the get-go.  It's strongly recommended.  Architecturally, the guiding 
principle of Kite9 Redux is the [12-Factor App](http://12factor.net).  This is well worth a read. 
Spring Boot really helps you to start off on the right track with these guidelines:
 - It builds a standalone JAR file, containing all the dependencies it needs.
 - It does some very intelligent things around configuration, allowing you to derive configuration from environment variables, amongst other things.\
 - It contains sensible default logging (using Apache Commons Logging), which logs to Stdout.
 
## Stuff To Include

[start.spring.io](http://start.spring.io) is a great place to begin building your project.   I created my 
initial project (including a maven `pom.xml`) using that, including the following dependencies:

 - `spring-boot-starter-actuator` : Lots of Spring components around monitoring, health-checking, JMX etc.
 - `spring-boot-starter-data-jpa` : Because, we don't want to write any SQL.. This manages data-sources and so on.
 - `spring-boot-starter-data-rest` and `spring-boot-starter-hateoas` : Provides a REST API for our Domain objects... demonstrated below.
 - `spring-boot-devtools` : Possibly lots of things, but mainly it reloads your application when it sees files change.  Useful for GUI development.
 - `spring-boot-starter-mail` :  We will eventually be emailing people for password resets, etc.
 - `spring-boot-starter-security` : We are going to secure the main web pages of the application.  May happen in this sprint but unlikely.
 - `spring-boot-starter-web` : Embeds the Tomcat servlet engine, so we can serve up web pages. 
 - `spring-boot-starter-websocket` : Websockets are used for server-push messaging to the web client.
 - `h2` : An in-memory database for testing with.
 - `spring-boot-starter-test` : Contains JUnit extensions so that we can build tests that set up the Spring Application Context.
 
## Generated Main Class

Spring Boot provides a single class as the entrance point of your application.  It looks like this:

```java
@SpringBootApplication
public class Kite9ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Kite9ServerApplication.class, args);
	}
}
``` 

We have here the usual java `main` method, which invokes `SpringApplication`.  This then notices the class
annotation of `SpringBootApplication`, and kicks off a class-path scan looking for application components.

A note about this quickly: if we label a class with `@Component`, then one of those will be instantiated in the application when it starts.
These are usually called 'beans' in the Java world, and you can also use `@Bean` if you prefer that synonym.

By the same token, classed annotated with `@Configuration` will be used to configure Spring.  

## Creating an Entity

So, the first entity will be `Project`.  It's going to look like this:

```java
@Entity
public class Project extends AbstractJPAEntity {
	
	String title;
	String description;
    String stub;
	String secret = createRandomString();
	
	...
```

Projects are primary-keyed with a `Long`.  Because lots of the entities in Kite9 have this, it's in a superclass
called `AbstractLongIdEntity`:

```java
@MappedSuperclass
public class AbstractLongIdEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;

	public Long getId() {
		return id;
	}
```
 
This also has `equals()` and `hashCode()` methods in there, which use the `id` column.  

## Repositories

In order to build our REST test quickly and easily, we're going to use the built-in repository 
support provided by Spring:

```java
@Component
public interface ProjectRepository extends CrudRepository<Project, Long> {

}
```

This is enough information for Spring Boot to create a repository to serve up Projects when we need them.
Also, because we have enabled REST, it's enough to provide a REST api.  In order to (later) simplify our
security concerns, I want all of the REST resources served from a uri starting with `api`.  This can be done 
with a Spring configuration bean:

```java
@Configuration
public class RestDataConfig extends RepositoryRestConfigurerAdapter {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    super.configureRepositoryRestConfiguration(config);
    config.setBasePath("/api");
  }
}
```

Spring Boot contains a lot of 'convention over configuration'.  By default, it looks for an `@Component` implementing
`RepositoryRestConfigurer`.  If it doesn't find one, it uses a basic `RepositoryRestConfigurerAdapter`.  But, since
we have provided one, it uses ours.

This is a really hard part of Spring Boot to get used to:  *a lot of components* get created for you, by default, and
by-and-large this happens without you seeing it.  A common mistake is to re-define beans that are already going to 
exist with your setup, and then you often end up with multiple beans conflicting with one another.

## Writing A Test ##

Here is the test of our newly-defined Project entity and repository. This test is going to:
 - Create a new project with a POST method.
 - Check that it's returned correctly.
 - Re-retrieve it using a GET request.
 - Then, delete it.

```java
@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Kite9ServerApplication.class)
@WebAppConfiguration
public class TestProjectsRoundTripRest {

	RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
	
	@Value("${application.url:http://localhost:8080}")
	private String urlBase;
	
	@Test
	public void testProjectIsReturned() {
		String url = urlBase + "/api/projects";
		Project pIn = new Project("Test Project", "Lorem Ipsum", "tp1");
		ResponseEntity<Project> pOut = restTemplate.postForEntity(url, pIn, Project.class);
		checkEquals(pIn, pOut.getBody());

		// retrieve it again
		Project pGet = restTemplate.getForObject(pOut.getHeaders().getLocation(), Project.class);
		checkEquals(pIn, pGet);

		// delete it
		restTemplate.delete(pOut.getHeaders().getLocation());
	}
```

Some observations:
 - `@RunWith(SpringJUnit4ClassRunner.class)` : Tells JUnit to use the spring runner.  Makes all the other annotations work.
 - `@IntegrationTest` : This tells spring to start up the entire application, not just beans we mention in the configuration.  
 - `@WebAppConfiguration` : Tells spring that the test will use tomcat, so start that.  I have no idea why @IntegrationTest doesn't cover this.
 - `@SpringApplicationConfiguration(classes = Kite9ServerApplication.class)` : Tells it the application we are testing.
 - `RestTemplate` : This is a spring class which handles marshalling and unmarshalling from JSON.  Really handy.
 - `@Value` : We are going to use this later to re-purpose the test to work against the live server.  But, this sets the expected URL for now.

## Security Settings ##

But, it doesn't work.  And the reason is that our REST endpoint is secured by the inclusion of the Spring Security module, and all it's
default beans.  To overcome this, we need to configure Spring to allow unauthenticated HTTP requests:

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().permitAll().and().csrf().disable(); 
	}
```

Rather like the REST configuration, here we are providing another `Configurer`, this time for web security.  Once this is done,
our test goes green.

A more in-depth tutorial covering pretty much the same functionality can be found [here](https://gerrydevstory.com/2015/01/15/restful-web-service-with-spring-data-rest-and-spring-security/).

## Deploying To The Cloud ##

So, we have:  Docker Compose, Docker Swarm, Open Shift, Kubernetes.

Ideally we want to deploy to some *reasonably priced* container server.   And, we want to do this with:
 - A scalable database, based on something that Hibernate supports
 - Some processing nodes
 - A load-balancer.
 
We want to do this *within maven*, so we don't really want to have to install loads of rubbish on our box.  








## Extending the Object Model ##

How about adding Documents to projects?  There is a many-to-one mapping between a document and it's project, which 
makes it more complex to post data in REST.

http://www.javabeat.net/spring-boot-testing/

```
2016-03-07 07:59:15.640  INFO 40275 --- [  restartedMain] c.kite9.k9server.Kite9ServerApplication  : The following profiles are active: populate
```
  
 