package com.kite9.k9server.domain.user;

import org.springframework.data.rest.core.config.Projection;

import com.kite9.k9server.domain.BasicExcerptProjection;

@Projection(types={User.class}, name="default")
public interface DefaultExcerptProjection extends BasicExcerptProjection {

	
}
