# 23rd January 2018: Sprint 17: Rest-Based Rendering

- some new entities:  rendered data entity.  diagram xml entity.   (should we have a single entity for hashed content?  Might be a good idea)
- currently, this is groovy code.  Refactor so this is a first-class Java, Spring service.
- Use REST, use the user token to validate requests.
- If we’ve been refactoring carefully, this should also still work.
- Write some tests for this.
- Store results in the content table.
- hard-code the stylesheets for now.
