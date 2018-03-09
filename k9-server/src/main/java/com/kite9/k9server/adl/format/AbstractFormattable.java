package com.kite9.k9server.adl.format;

import java.io.StringWriter;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.kite9.framework.common.Kite9ProcessingException;

import com.kite9.k9server.adl.holder.ADL;

/**
 * Contains enough code for the formattable to create the output representation
 * from the input.
 * 
 * @author robmoffat
 *
 */
public abstract class AbstractFormattable implements Formattable {

	@Override
	public String getOutput() {
		return render(getInput());
	}
	
	public static String render(ADL data) {
		try {
			Transcoder transcoder = data.getTranscoder();
			TranscoderInput in = new TranscoderInput(data.getAsDocument());
			StringWriter sw = new StringWriter();
			TranscoderOutput out = new TranscoderOutput(sw);
			transcoder.transcode(in, out);
			return sw.toString();
		} catch (TranscoderException e) {
			throw new Kite9ProcessingException(e);
		}	
		
	}
	
}
