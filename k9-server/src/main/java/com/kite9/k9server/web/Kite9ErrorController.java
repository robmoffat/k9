package com.kite9.k9server.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.kite9.framework.common.Kite9XMLProcessingException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

@Controller
public class Kite9ErrorController implements ErrorController {
	
	public static final String HIGHLIGHTER = 
			"<link rel=\"stylesheet\"\n" + 
			"      href=\"//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.15.10/styles/default.min.css\">\n" + 
			"<script src=\"//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.15.10/highlight.min.js\"></script>";

	public static final String BOOTSTRAP = "<link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css\" "
			+ "rel=\"stylesheet\" "
			+ "integrity=\"sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M\" "
			+ "crossorigin=\"anonymous\">";
	
	@RequestMapping("/error")
	@ResponseBody
	public String handleError(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String uri = (String) request.getAttribute("javax.servlet.error.request_uri");
		Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
		
		StringBuilder sb = new StringBuilder();
		addHeader(sb, statusCode, uri);
		processException(sb, exception, 1);
		addFooter(sb);
		return sb.toString();
	}

	private void addFooter(StringBuilder sb) {
		sb.append("<script>hljs.initHighlightingOnLoad();</script>");
		sb.append("</div></body></html>");
	}

	private void processException(StringBuilder sb, Throwable e, int level) {
		if (e != null) {
			sb.append("<div class=\"card\" style=\"margin-bottom: 30px;\"><div class=\"card-body\"><h4 class=\"card-title\">");
			sb.append("("+level+") ");
			sb.append(e.getClass().getName());
			sb.append("</h4>");
			sb.append("<h6 class=\"card-subtitle mb-2 text-muted\">");
			sb.append(e.getMessage());
			sb.append("<div class=\"card-text\" style=\"padding: 20px; \">");
		}
		if (e instanceof Kite9XMLProcessingException) {
			String css = ((Kite9XMLProcessingException) e).getCss();
			String ctx = ((Kite9XMLProcessingException) e).getContext();
			
			
			if (!StringUtils.isEmpty(ctx)) {
				doCard(sb, ctx, "fragment", "xml");
			}
			
			if (!StringUtils.isEmpty(css)) {
				doCard(sb, css, "style", "css");
				
			}
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw); 
			doCard(sb, sw.toString(), "Stack Trace", "plaintext");
		}
		
		if (e != null) {
			sb.append("</div></div></div>");
			processException(sb, e.getCause(), level+1);
		}
	}

	protected void doCard(StringBuilder sb, String ctx, String title, String format) {
		sb.append("<div class=\"card\"><div class=\"card-body\">");
		sb.append("<h5 class=\"card-title\">"+title+"</h5>");
		sb.append("<div class=\"card-text\"><pre><code class=\""+format+"\">.");
		sb.append(HtmlUtils.htmlEscape(ctx));
		sb.append("</code></pre></div></div></div>");
	}

	private void addHeader(StringBuilder sb, int statusCode, String uri) {
		sb.append("<html><head><title>"+statusCode+"</title>");
		sb.append(HIGHLIGHTER);
		sb.append(BOOTSTRAP);
		sb.append("</head><body><div class=\"container\">");
		sb.append("<h1>Kite9 Error <span class=\"badge badge-danger\">"+statusCode+"</span></h1>");
		sb.append("<div class=\"alert alert-info\">" + uri+ "</div>");
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}
