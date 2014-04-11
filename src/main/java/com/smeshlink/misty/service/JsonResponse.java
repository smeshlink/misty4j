/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import com.smeshlink.misty.formatter.JSONFormatter;

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
		if (body == null) {
			Object obj = json.opt("body");
			if (obj != null)
				body = (new JSONFormatter()).parseObject(obj);
		}
		return body;
	}

	public int getStatus() {
		return status;
	}

	public void setHeader(String name, String value) {
		// not support
	}
	
	public String getToken() {
		return json.optString("token", null);
	}

	public void setToken(String token) {
		json.put("token", token);
	}
}
