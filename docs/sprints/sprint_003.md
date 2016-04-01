# Sprint 3: Grails 3

## Goals

- Upgrade the original Kite9 to Grails 3.
- We can keep our existing Kite9 infrastructure working by moving to Grails 3, which is based on Spring Boot.
- This probably means moving to gradle builds too, and therefore a gradle fabric8 plugin.
- I think ideally we should stick to the spring/jpa-based persistence because it's more future proof, so we need to move the rest of those entites and add tests for them.
- This means that Kite9 should be completely ported to AWS.
- Add a table for the diagram contents, unrendered and rendered
- Need to check email works

## Background

This sprint came about because of talks with Andrew Lockley.  He said we needed to get users in front of the system and the best way I can think to do this is to port the platform onto AWS fully. 

This means we're carrying a bit of baggage:  we have Grails baked into our application still, which will be mainly used for rendering views.  (Ideally, that's all it will be used for:  I think we should convert all the rest of the controllers to Java / Spring Boot.. in [Sprint 5](sprint_005.md) )