/**
 * Copyright (c) 2011-2014 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

/**
 * Represents a JSON request to a service.
 * 
 * @author smeshlink
 *
 */
public class JsonResponse implements IServiceResponse {
	private JSONObject json;
	private JSONObject headers;
	private int status;
	private Object body;
	private String resource;
	
	public JsonResponse(JSONObject json) {
		this.json = json;
		status = json.optInt("status", 200);
		resource = json.optString("resource", null);
		headers = json.optJSONObject("headers");
		body = json.opt("body");
	}

	public String getHeader(String name) {
		return headers == null ? null : headers.optString(name, null);
	}
	
	public Map getHeaders() {
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

	public String getResource() {
		return resource;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	public int getStatus() {
		return status;
	}

	public void setHeader(String name, String value) {
		 if (headers == null) {
			 headers = new JSONObject();
			 json.put("headers", headers);
         }
		 headers.put(name, value);
	}
	
	public String getToken() {
		return json.optString("token", null);
	}

	public void setToken(String token) {
		json.put("token", token);
	}

	public InputStream getResponseStream() throws IOException {
		return null;
	}

	public void dispose() {
		// do nothing
	}
}
