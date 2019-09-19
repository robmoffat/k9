package com.kite9.k9server.rest;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kite9.k9server.adl.holder.ADL;
import com.kite9.k9server.adl.holder.ADLImpl;
import com.kite9.k9server.domain.Entity;

@Service
public class HateoasDOMBuilder implements InitializingBean {
	
	public static final HttpHeaders EMPTY_HEADERS = new HttpHeaders();

	@Value("${kite9.rest.template:classpath:/static/public/context/admin/resource.xml}")
	private String templateResource;
	
	private String template;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		template = IOUtils.toString(resourceLoader.getResource(templateResource).getInputStream(), "UTF-8");
	}
	
	
	public ADL createDocument(ResourceSupport rs) throws Exception {
		URI u = new URI(rs.getId().getHref());
		ADL container = new ADLImpl(template, u, EMPTY_HEADERS);
		
		NodeList nl = container.getAsDocument().getRootElement().getElementsByTagName("diagram");
		
		if (nl.getLength() != 1) {
			throw new IllegalArgumentException("Couldn't find single diagram element in template");
		}
		
		Element top = (Element) nl.item(0);
		Element main = createElementFrom(rs, true, container.getAsDocument());	
		top.appendChild(main);
		return container;
	}

	private Element createElementFrom(Object rs, boolean topLevel, Document d) {
		if (rs instanceof Entity) {
			return createEntityElement((Entity) rs, d);
		} else if (rs instanceof Resource) {
			return createElementFrom(((Resource<?>) rs).getContent(), topLevel, d); 
		} else if (rs instanceof Resources) {
			return null;
		} else { 
			return null;
		}
	}


	protected Element createEntityElement(Entity e, Document d) {
		Element entity = d.createElement("entity");
		String className = e.getClass().getCanonicalName();
		className = className.substring(className.lastIndexOf(".")+1).toLowerCase();
		entity.setAttribute("class", className);
		
		Element title = d.createElement("title");
		title.setTextContent(e.getTitle());
		entity.appendChild(title);
		
		Element icon = d.createElement("icon");
		icon.setAttribute("image-src", e.getLocalImagePath());
		entity.appendChild(icon);
		
		Element description = d.createElement("description");
		description.setTextContent(e.getDescription());
		entity.appendChild(description);
		
		if (e.getLastUpdated() != null) {
			Element date = d.createElement("date");
			date.setTextContent(e.getLastUpdated().toString());
			entity.appendChild(date);
		}
		
		return entity;
	}
}
