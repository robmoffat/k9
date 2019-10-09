package com.kite9.k9server.domain.project;

import org.springframework.data.rest.core.config.Projection;

import com.kite9.k9server.domain.BasicExcerptProjection;

@Projection(types={Project.class}, name="default")
public interface ProjectExcerptProjection extends BasicExcerptProjection {

	
}
