package com.kite9.k9server.domain.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface ProjectRepository extends CrudRepository<Project, Long>, ProjectRepositoryCustom {

}
