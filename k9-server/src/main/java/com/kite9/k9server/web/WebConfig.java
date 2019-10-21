package com.kite9.k9server.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kite9.k9server.adl.format.ADLMessageConverter;
import com.kite9.k9server.adl.format.FormatSupplier;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebConfig implements WebMvcConfigurer {

	private static final Log LOG = LogFactory.getLog(WebConfig.class);

	/**
	 * Handles conversion of HttpException into a message and a http status.
	 */
	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(new HandlerExceptionResolver() {

			@Override
			public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
				if (ex instanceof HttpException) {
					try {
						response.sendError(((HttpException) ex).getStatus().value(), ex.getMessage());
					} catch (IOException e) {
						LOG.error("Failed to process HttpException", e);
					}

					return new ModelAndView();
				}

				return null;
			}
		});
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(adlMessageConverter());
	}

	@Autowired
	FormatSupplier fs;
	
	@Bean
	public HttpMessageConverter<?> adlMessageConverter() {
		return new ADLMessageConverter(fs);
	}
	
	public class LoggingFilter extends CommonsRequestLoggingFilter {

		public LoggingFilter() {
			super();
			 this.setIncludeClientInfo(true);
			 this.setIncludeQueryString(true);
			 this.setIncludePayload(true);
			 this.setMaxPayloadLength(1000);		
		}
		
		@Override
		protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
			return request.getMethod() + " " + super.createMessage(request, prefix, suffix);
		}
		
	}
	
	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
	   return new LoggingFilter();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
	}
	
}
