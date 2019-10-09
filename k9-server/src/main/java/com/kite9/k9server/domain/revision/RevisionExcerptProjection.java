package com.kite9.k9server.domain.revision;

import org.springframework.data.rest.core.config.Projection;

import com.kite9.k9server.domain.BasicExcerptProjection;

@Projection(types={Revision.class}, name="default")
public interface RevisionExcerptProjection extends BasicExcerptProjection {

	
}
