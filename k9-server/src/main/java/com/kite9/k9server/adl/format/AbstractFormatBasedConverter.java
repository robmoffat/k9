package com.kite9.k9server.adl.format;

import java.nio.charset.Charset;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.AbstractHttpMessageConverter;

public abstract class AbstractFormatBasedConverter<X> extends AbstractHttpMessageConverter<X> implements InitializingBean {

	public static final Charset DEFAULT = Charset.forName("UTF-8");
	
	@Autowired
	protected FormatSupplier formatSupplier;

	public AbstractFormatBasedConverter() {
		super();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setSupportedMediaTypes(formatSupplier.getMediaTypes());
	}
	
	

}