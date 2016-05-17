# 17th May 2016: Travis / Build Automation

- building of all projects (including Visualization)
- Continuous build of master, (releasing to Amazon automatically? )
- Sort out DNS
- mail gateway
- automated service testing (running docker tests)

## Step 1: Travis Configuration 

Ok, so first problem is that Travis costs $129 per month to run for a private project.  wtf.  So, I think realistically, I'm probably 
not going to build this using Travis.

Secondly, I'm not completely happy with allowing Travis to build this project either.   So.  What is the alternative?

- If we don't provide a jar (somehow) the server can't be built.  And, it can't be deployed.  So, I am thinking that checking into the
project.  The best way seems to be the one described [here](http://stackoverflow.com/questions/364114/can-i-add-jars-to-maven-2-build-classpath-without-installing-them),
which is to have a directory inside the project which acts as a maven repository.

### kite9-java Build

This is in the public directory, and should be easily buildable.   

