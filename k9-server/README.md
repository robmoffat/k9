# Kite9 Server

Kite9 Server is a Docker-ized Spring Boot Executable Jar which handles the following concerns:

- Providing REST endpoints for creating Kite9 diagrams (in various formats)
- Providing User-level security and a Project abstraction.
- Providing a Command-based interface for modifying diagrams held internally in ADL format (i.e. Kite9 + SVG)
- Providing storage of stylesheets and related artifacts

Since the user creation / password setting workflow requires mail, it has a Docker Postfix component.  Data is held in
Mysql installed locally.  The configuration for this is in `src/docker/default.properties`.

In the past, I've also run on aws, hence `src/docker/aws-machine.properties` in there too.

## Integration Tests

These are run using a local Docker installation.  To start:

```
docker-machine start
eval $(docker-machine env default)
mvn integration-test
```

The fabric8 maven plugin will set up the required docker instances and run the tests.

