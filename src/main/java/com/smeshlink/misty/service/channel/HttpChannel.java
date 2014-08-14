/**
 * Copyright (c) 2011-2014 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service.channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.smeshlink.misty.service.ICredential;
import com.smeshlink.misty.service.IServiceRequest;
import com.smeshlink.misty.service.IServiceResponse;
import com.smeshlink.misty.service.MistyService;
import com.smeshlink.misty.service.ServiceException;
import com.smeshlink.misty.service.UserCredential;

/**
 * HTTP channel.
 * 
 * @author Longshine
 * 
 */
public class HttpChannel implements IServiceChannel {
	private String host;
	private HttpClient client = new HttpClient();
	
	public HttpChannel(String host) {
		this.host = host;
	}
	
	public void setRequestListener(IRequestListener listener) {
		// do nothing
	}
	
	public void setTimeout(int timeout) {
		// TODO
	}
	
	public int getTimeout() {
		// TODO
		return 0;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}
	
	public IServiceResponse execute(IServiceRequest request) {
		if (request.getFormat() == null)
			request.setFormat("json");
		
		HttpMethod method = buildMethod(request);
		
		try {
			return new HttpResponse(request.getResource(), client.executeMethod(method), method);
		} catch (IOException e) {
			throw ServiceException.error(e);
		}
	}
	
	private HttpMethod buildMethod(IServiceRequest request) {
		String method = request.getMethod();
		String entity = null;
		String contentType = MistyService.getContentType(request.getFormat());
		HttpMethod m = null;
		if ("GET".equals(method)) {
			m = new GetMethod();
		} else if ("POST".equals(method)) {
			PostMethod post = new PostMethod();
			try {
				post.setRequestEntity(new StringRequestEntity(entity, contentType, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				ServiceException.badRequest(e.getMessage());
			}
			m = post;
		} else if ("PUT".equals(method)) {
			PutMethod put = new PutMethod();
			try {
				put.setRequestEntity(new StringRequestEntity(entity, contentType, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				ServiceException.badRequest(e.getMessage());
			}
			m = put;
		} else if ("DELETE".equals(method)) {
			m = new DeleteMethod();
		} else {
			ServiceException.badRequest("Unknown request method " + method);
		}

		String path = request.getResource();
		if (request.getFormat() != null)
			path += "." + request.getFormat();
		try {
			m.setURI(new URI("http", host, path, null, null));
		} catch (URIException e) {
			ServiceException.badRequest("Invalid URI requested.");
		}
		
		Map headers = request.getHeaders();
		if (headers != null) {
			for (Iterator it = headers.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry entry = (Entry) it.next();
				if (entry.getKey() == null || entry.getValue() == null)
					continue;
				m.setRequestHeader(entry.getKey().toString(), entry.getValue().toString());
			}
		}
		m.setRequestHeader(MistyService.HEADER_USER_AGENT, MistyService.VERSION);
		m.setRequestHeader(MistyService.HEADER_ACCEPT, contentType);
		
		ICredential cred = request.getCredential();
		if (cred == null) {
			client.getParams().setAuthenticationPreemptive(false);
			client.getState().clearCredentials();
		} else if (cred instanceof UserCredential) {
			UserCredential uc = (UserCredential) cred;
			client.getParams().setAuthenticationPreemptive(true);
			client.getState().setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(uc.getUsername(), uc.getPassword()));
		} else {
			ICredential.Pair pair = cred.getCredential();
			m.setRequestHeader(pair.getKey(), pair.getValue());
		}
		
		Map params = request.getParameters();
		if (params != null) {
			for (Iterator it = params.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry entry = (Entry) it.next();
				if (entry.getKey() == null || entry.getValue() == null)
					continue;
				m.getParams().setParameter(entry.getKey().toString(), entry.getValue());
			}
		}
		
		return m;
	}
	
	class HttpResponse implements IServiceResponse {
		private final String resource;
		private final int statusCode;
		private HttpMethod method;
		
		public HttpResponse(String resource, int statusCode, HttpMethod method) {
			this.resource = resource;
			this.statusCode = statusCode;
			this.method = method;
		}

		public String getResource() {
			return resource;
		}

		public int getStatus() {
			return statusCode;
		}

		public Object getBody() {
			return null;
		}

		public Map getHeaders() {
			Map map = new HashMap();
			Header[] headers = method.getResponseHeaders();
			for (int i = 0; i < headers.length; i++) {
				map.put(headers[i].getName(), headers[i].getValue());
			}
			return map;
		}

		public String getToken() {
			return null;
		}

		public void setToken(String token) {
			// do nothing
		}

		public InputStream getResponseStream() throws IOException {
			return method.getResponseBodyAsStream();
		}

		public void dispose() {
			method.releaseConnection();
		}
	}
}
