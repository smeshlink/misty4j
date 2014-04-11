/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a request to a service.
 * 
 * @author smeshlink
 *
 */
public class ServiceRequestImpl implements IServiceRequest {
	private String method;
	private String user;
	private String host;
	private Map headers = new HashMap();
	private Map parameters = new HashMap();
	private Object body;
	private String resource;
	private String format;
	private String token;

	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}

	public String getHeader(String name) {
		Object obj = headers.get(name.toLowerCase());
		if (obj == null) {
			return null;
		} else if (obj instanceof List) {
			List list = (List) obj;
			return list.size() > 0 ? (String) list.get(0) : null;
		} else {
			return obj.toString();
		}
	}
	
	public Map getHeaders() {
		return headers;
	}
	
	public void addHeader(String name, String value) {
		for (Iterator it = headers.entrySet().iterator(); it.hasNext(); ) {
			Entry entry = (Entry) it.next();
			if (name.equalsIgnoreCase((String) entry.getKey())) {
				Object obj = entry.getValue();
				List list;
				if (entry.getValue() instanceof String) {
					list = new ArrayList();
					entry.setValue(list);
					list.add(obj);
				} else {
					list = (List) obj;
				}
				list.add(value);
				return;
			}
		}

		headers.put(name, value);
	}

	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}

	public String getParameter(String name) {
		Object obj = parameters.get(name);
		if (obj == null) {
			return null;
		} else if (obj instanceof List) {
			List list = (List) obj;
			return list.size() > 0 ? (String) list.get(0) : null;
		} else {
			return obj.toString();
		}
	}

	public String[] getParameters(String name) {
		Object obj = parameters.get(name);
		if (obj == null) {
			return null;
		} else if (obj instanceof List) {
			List list = (List) obj;
			return (String[]) list.toArray(new String[0]);
		} else {
			return new String[] { obj.toString() };
		}
	}

	public String getResource() {
		return resource;
	}
	
	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	public InputStream getInputStream() throws IOException {
		throw new RuntimeException();
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
