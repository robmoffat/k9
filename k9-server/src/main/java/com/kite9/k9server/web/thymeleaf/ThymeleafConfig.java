package com.kite9.k9server.web.thymeleaf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import com.kite9.k9server.adl.format.FormatSupplier;
import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;

@Configuration
public class ThymeleafConfig {
	
	@Autowired
	FormatSupplier formatSupplier;
	
	private Charset UTF_8 = Charsets.UTF_8;

	@Bean
	public SpringResourceTemplateResolver xmlTemplateResolver(ApplicationContext appCtx) {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

		templateResolver.setApplicationContext(appCtx);
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".xml");
		templateResolver.setTemplateMode("XML");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(false);
		return templateResolver;
	}

	@Bean
	public Filter responseConversionFilter() {
;		return new OrderedFilter() {
			
					
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				String path = null;
				List<String> accepts = Collections.emptyList();
				ServletResponse originalResponse = response;
				HttpServletRequest httpRequest = null;
				if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
					httpRequest = (HttpServletRequest) request;
					path = httpRequest.getRequestURI();
					accepts = Arrays.asList(httpRequest.getHeader("accept").split(","));
					if (path.startsWith("/api") && (accepts.contains("text/html"))) {
						response = new ADLServletResponseWrapper((HttpServletResponse) response);
					}
				}

				chain.doFilter(request, response);
				
				if (originalResponse != response) {
					try {
						// at this point, we need to do some conversion
						byte[] contents = ((ADLServletResponseWrapper) response).contents();
						String strContent = new String(contents, UTF_8);
						URI uri = new URI(httpRequest.getRequestURL().toString());
						ADL adl = new ADLImpl(strContent, uri, null);
						
						System.out.println("out: "+strContent);
							
						formatSupplier.getFormatFor(MediaType.TEXT_HTML).handleWrite(adl, 
								originalResponse.getOutputStream(), false, 0, 0);
					} catch (Exception e) {
						throw new IOException("Couldn't perform conversion: ", e);
					}	
				}

			}

			@Override
			public int getOrder() {
				return 2;
			}
		};
	}

	static class ADLServletResponseWrapper extends HttpServletResponseWrapper {
		protected ByteArrayServletOutputStream basos;
		
		protected PrintWriter writer;
		
		protected ServletOutputStream sos;

		protected boolean getOutputStreamCalled;

		protected boolean getWriterCalled;

		public ADLServletResponseWrapper(HttpServletResponse response) {
			super(response);
			basos = new ByteArrayServletOutputStream();
		}

		public ServletOutputStream getOutputStream() throws IOException {
			if (getWriterCalled) {
				throw new IllegalStateException("getWriter already called");
			}

			getOutputStreamCalled = true;
			
			
			return basos;
		}

		public PrintWriter getWriter() throws IOException {
			if (writer != null) {
				return writer;
			}
			if (getOutputStreamCalled) {
				throw new IllegalStateException("getOutputStream already called");
			}
			getWriterCalled = true;
			writer = new PrintWriter(basos);
			return writer;
		}

		public byte[] contents() {
			return basos.toByteArray();
		}
	}
	
	
	 public static class ByteArrayServletOutputStream extends ServletOutputStream {
	     /**
	      * Our buffer to hold the stream.
	      */
	     protected final ByteArrayOutputStream buf;


	     /**
	      * Construct a new ServletOutputStream.
	      */
	     public ByteArrayServletOutputStream() {
	         buf = new ByteArrayOutputStream();
	     }


	     /**
	      * @return the byte array.
	      */
	     public byte[] toByteArray() {
	         return buf.toByteArray();
	     }


	     /**
	      * Write to our buffer.
	      *
	      * @param b The parameter to write
	      */
	     @Override
	     public void write(int b) {
	         buf.write(b);
	     }

	     /**
	      * TODO SERVLET 3.1
	      */
	     @Override
	     public boolean isReady() {
	         // TODO Auto-generated method stub
	         return false;
	     }


	     /**
	      * TODO SERVLET 3.1
	      */
	     @Override
	     public void setWriteListener(WriteListener listener) {
	         // TODO Auto-generated method stub

	     }


	 }
}
