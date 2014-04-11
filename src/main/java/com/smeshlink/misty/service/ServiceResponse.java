/**
 * 
 */
package com.smeshlink.misty.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author smeshlink
 *
 */
public class ServiceResponse implements IServiceResponse {
	private int status;
	private Map headers = new HashMap();
	private String resource;
	private Object body;
	private String token;
	
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
	
	public void appendHeader(String name, String value) {
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
	
	public String getResource() {
		return resource;
	}
	
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	public void setBody(Object body) {
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
}
