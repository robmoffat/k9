package com.kite9.k9server.domain.permission;

import org.springframework.data.rest.core.config.Projection;

import com.kite9.k9server.domain.BasicExcerptProjection;

@Projection(types={Member.class}, name="default")
public interface DefaultExcerptProjection extends BasicExcerptProjection {

	
}
