package com.kite9.k9server.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.batik.bridge.BridgeException;
import org.apache.batik.dom.util.SAXIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.kite9.framework.common.Kite9XMLProcessingException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import org.xml.sax.SAXParseException;

@Controller
public class Kite9ErrorController implements ErrorController {
	
	public static final String HIGHLIGHTER = 
			"<link rel=\"stylesheet\"\n" + 
			"      href=\"/public/external/highlight-9.6.2.min.css\">\n" + 
			"<script src=\"/public/external/highlight-9.6.2.min.js\"></script>";

	public static final String BOOTSTRAP = "<link href=\"/public/external/bootstrap-4.3.1.css\" "
			+ "rel=\"stylesheet\" "
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
		Throwable next = null;
		if (e != null) {
			sb.append("<div class=\"card\" style=\"margin-bottom: 30px;\"><div class=\"card-body\"><h4 class=\"card-title\">");
			sb.append("("+level+") ");
			sb.append(e.getClass().getName());
			sb.append("</h4>");
			sb.append("<h6 class=\"card-subtitle mb-2 text-muted\">");
			sb.append(e.getMessage());
			sb.append("<div class=\"card-text\" style=\"padding: 20px; \">");
			next = e.getCause();
		}
		if (e instanceof Kite9XMLProcessingException) {
			String css = ((Kite9XMLProcessingException) e).getCss();
			String ctx = ((Kite9XMLProcessingException) e).getContext();
			doXMLBasedException(sb, e, css, ctx);
			next = e.getCause();
		} else if (e instanceof BridgeException) {
			BridgeException be = (BridgeException) e;
			String ctx = Kite9XMLProcessingException.toString(be.getElement());
			String css = Kite9XMLProcessingException.debugCss(be.getElement());
			doXMLBasedException(sb, e, css, ctx);
			next = e.getCause();
		} else if (e instanceof SAXParseException) {
			SAXParseException pe = (SAXParseException) e;
			String publicId = pe.getPublicId();
			String systemId = pe.getSystemId();
			int lineNumber = pe.getLineNumber();
			int columnNumber = pe.getColumnNumber();
			String saxInfo = String.format("publicId: %s\nsystemId: %s\nlineNumber: %s\ncolumnNumber: %s", publicId, systemId, lineNumber, columnNumber);
			doCard(sb, saxInfo, "details", "plaintext");
			next = pe.getException();
		} else if (e instanceof SAXIOException) {
			SAXIOException pe = (SAXIOException) e;
			doStackTraceCard(sb, pe);
			next = pe.getSAXException();
		} else if (e instanceof TranscoderException) {
			doStackTraceCard(sb, e);
			next = ((TranscoderException) e).getException();
		} else if ((e!=null) && (e.getCause() == null)) {
			doStackTraceCard(sb, e);
			next = null;
		}
		
		if (e != null) {
			sb.append("</div></div></div>");
		}
		
		if (next != null) {
			processException(sb, next, level+1);
		}
	}

	protected void doXMLBasedException(StringBuilder sb, Throwable e, String css, String ctx) {
		if (!StringUtils.isEmpty(ctx)) {
			doCard(sb, ctx, "fragment", "xml");
		}
		
		if (!StringUtils.isEmpty(css)) {
			doCard(sb, css, "style", "css");
			
		}
		
		doStackTraceCard(sb, e);
	}

	protected void doStackTraceCard(StringBuilder sb, Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw); 
		doCard(sb, sw.toString(), "Stack Trace", "plaintext");
	}

	protected void doCard(StringBuilder sb, String ctx, String title, String format) {
		sb.append("<div class=\"card\" style=\"margin-bottom: 30px;\"><div class=\"card-body\">");
		sb.append("<h5 class=\"card-title\">"+title+"</h5>");
		sb.append("<div class=\"card-text\"><pre><code class=\""+format+"\">");
		sb.append(HtmlUtils.htmlEscape(ctx));
		sb.append("</code></pre></div></div></div>");
	}

	private void addHeader(StringBuilder sb, Integer statusCode, String uri) {
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
