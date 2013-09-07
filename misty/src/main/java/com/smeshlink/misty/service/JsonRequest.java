/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.smeshlink.misty.formatter.JSONFormatter;

/**
 * Represents a JSON request to a service.
 * 
 * @author smeshlink
 *
 */
public class JsonRequest implements IServiceRequest {
	private JSONObject json;
	private Object body;
	private String format;
	private String host;
	
	public JsonRequest() {
		this(new JSONObject());
	}
	
	public JsonRequest(JSONObject json) {
		this.json = json;
	}

	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void addHeader(String name, String value) {
		JSONObject headers = json.optJSONObject("headers");
		if (headers == null) {
			headers = new JSONObject();
			json.put("headers", headers);
		}
		
		headers.accumulate(name, value);
	}

	public String getHeader(String name) {
		JSONObject headers = json.optJSONObject("headers");
		
		if (headers == null)
			return null;
		
		for (Iterator it = headers.keys(); it.hasNext(); ) {
			String key = (String) it.next();
			if (key.equalsIgnoreCase(name))
				return headers.getString(key);
		}
		
		return null;
	}
	
	public Map getHeaders() {
		JSONObject headers = json.optJSONObject("headers");
		
		Map map = new HashMap();
		if (headers != null) {
			Iterator it = headers.keys();
			while (it.hasNext()) {
				String name = (String) it.next();
				map.put(name, headers.optString(name, null));
			}
		}
		return map;
	}

	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public String getMethod() {
		return json.getString("method");
	}
	
	public void setMethod(String method) {
		json.put("method", method);
	}

	public String getParameter(String name) {
		JSONObject params = json.optJSONObject("params");
		
		Object obj = params == null ? null : params.opt(name);
		if (obj == null) {
			return null;
		} else if (obj instanceof JSONArray) {
			JSONArray array = (JSONArray) obj;
			return array.optString(0, null);
		} else {
			return obj.toString();
		}
	}

	public String[] getParameters(String name) {
		JSONObject params = json.optJSONObject("params");
		
		Object obj = params == null ? null : params.opt(name);
		if (obj == null) {
			return null;
		} else if (obj instanceof JSONArray) {
			JSONArray array = (JSONArray) obj;
			String[] ret = new String[array.length()];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = array.optString(i);
			}
			return ret;
		} else {
			return new String[] { obj.toString() };
		}
	}

	public String getReferrer() {
		return getHeader("Referer");
	}

	public String getResource() {
		return json.getString("resource");
	}
	
	public void setResource(String resource) {
		json.put("resource", resource);
	}

	public String getUser() {
		return null;
	}

	public void setBody(Object body) {
		this.body = body;
		if (body == null) {
			json.remove("body");
		} else {
			StringWriter sw = new StringWriter();
			(new JSONFormatter()).format(sw, body);
			json.put("body", sw.toString());
		}
	}

	public Object getBody() {
		if (body == null) {
			Object obj = json.opt("body");
			if (obj != null)
				body = (new JSONFormatter()).parseObject(obj);
		}
		return body;
	}

	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getToken() {
		return json.optString("token", null);
	}
	
	public String toString() {
		return json.toString();
	}
}
