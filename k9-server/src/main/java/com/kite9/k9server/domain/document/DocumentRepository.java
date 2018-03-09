package com.kite9.k9server.domain.document;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface DocumentRepository extends CrudRepository<Document, Long> {

}
