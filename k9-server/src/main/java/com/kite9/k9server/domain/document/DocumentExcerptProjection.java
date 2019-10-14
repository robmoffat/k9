package com.kite9.k9server.domain.document;

import org.springframework.data.rest.core.config.Projection;
import org.w3c.dom.Document;

import com.kite9.k9server.domain.entity.BasicExcerptProjection;

@Projection(types={Document.class}, name="default")
public interface DocumentExcerptProjection extends BasicExcerptProjection {

	
}
