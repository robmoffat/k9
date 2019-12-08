package com.kite9.k9server.resource;

import java.util.Date;
import java.util.Map;

import org.springframework.hateoas.ResourceSupport;

public class GenericResource extends ResourceSupport {

	public String type;
    public String title;
    public String description;
    public Map<String, Object> _embedded;
    public Date lastUpdated;
    public String icon;
    public String commands;
    public String localId;
}
