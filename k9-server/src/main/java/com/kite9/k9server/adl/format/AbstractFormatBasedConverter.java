package com.kite9.k9server.adl.format;

import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.kite9.framework.common.Kite9ProcessingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import com.kite9.k9server.adl.holder.ADL;

public abstract class AbstractFormatBasedConverter<X> extends AbstractHttpMessageConverter<X> implements InitializingBean, Converter {

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

	/**
	 * Plug in any caching here.
	 */
	public String getSVGRepresentation(ADL data) {
		try {
			Transcoder transcoder = data.getTranscoder();
			TranscoderInput in = new TranscoderInput(data.getAsDocument());
			in.setURI(data.getUri());
			StringWriter sw = new StringWriter();
			TranscoderOutput out = new TranscoderOutput(sw);
			transcoder.transcode(in, out);
			return sw.toString();
		} catch (TranscoderException e) {
			throw new Kite9ProcessingException(e);
		}
	}

}