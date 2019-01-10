package com.kite9.k9server.adl.renderer;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kite9.k9server.adl.format.media.MediaTypes;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

/**
 * Handles rendering of Kite9 ADL content.  A bit like a simpler version of the CommandController, 
 * except that it just renders the ADL.
 * 
 * @author robmoffat
 *
 */
@Controller()
public class RenderingController {
	
	@RequestMapping(path="/api/renderer", consumes= {MediaTypes.ADL_SVG_VALUE, MediaTypes.SVG_VALUE})
	public @ResponseBody ADL echo(@RequestBody ADL input) {
		return input;
	}
	
	@RequestMapping(path="/api/renderer/test")
	public @ResponseBody ADL testCard() throws IOException {
		String xml = StreamUtils.copyToString(this.getClass().getResourceAsStream("/test-card.xml"), Charset.defaultCharset());
		return new ADLImpl(xml, "someurl");
	}
}
