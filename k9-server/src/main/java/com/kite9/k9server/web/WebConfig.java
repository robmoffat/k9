package com.kite9.k9server.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.kite9.k9server.repos.RestDataConfig;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

	private static final Log LOG = LogFactory.getLog(WebConfig.class);

	/**
	 * Handles conversion of HttpException into a message and a http status.
	 */
	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		super.configureHandlerExceptionResolvers(exceptionResolvers);
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
}
