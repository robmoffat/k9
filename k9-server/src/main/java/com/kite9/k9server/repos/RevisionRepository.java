package com.kite9.k9server.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.kite9.k9server.domain.Revision;

@Component
public interface RevisionRepository extends CrudRepository<Revision, Long> {

}
