package com.kite9.k9server.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface RevisionRepository extends CrudRepository<Revision, Long> {

}
