/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service.channel;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.User;
import com.smeshlink.misty.formatter.FormatException;
import com.smeshlink.misty.formatter.IFeedFormatter;
import com.smeshlink.misty.formatter.JSONFormatter;
import com.smeshlink.misty.formatter.XmlFormatter;
import com.smeshlink.misty.service.QueryOption;
import com.smeshlink.misty.service.ServiceException;

/**
 * @author Longshine
 * 
 */
public class HttpChannel implements IServiceChannel {
	public static final int MEDIA_TYPE_JSON = 0;
	public static final int MEDIA_TYPE_XML = 1;
	public static final int MEDIA_TYPE_CSV = 2;

	private static final String HEADER_KEY_API = "X-ApiKey";
	private static final String HEADER_ACCEPT = "Accept";
	private static final String HEADER_USER_AGENT = "User Agent";
	private static final String USER_AGENT = "Misty-Java-Lib/0.1.0";

	private String host = "api.misty.smeshlink.com";
	private String apiKey;
	private String username;
	private String password;
	private User user;
	private int mediaType;
	private HttpClient client = new HttpClient();
	
	public HttpChannel(String username, String password) {
		this.username = username;
		this.password = password;
		prepareUsernamePasswordCredentials();
	}
	
	public HttpChannel(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public IFeedService feed() {
		return new FeedService(null);
	}

	public IFeedService feed(User user) {
		return new FeedService(user.getUsername());
	}

	public IFeedService feed(Feed parent) {
		return new FeedService(parent == null ? null : ("/feeds/" + parent.getPath()));
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setUsername(String username) {
		this.username = username;
		prepareUsernamePasswordCredentials();
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
		prepareUsernamePasswordCredentials();
	}

	public String getPassword() {
		return password;
	}
	
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}
	
	private void prepareUsernamePasswordCredentials() {
		if (username != null && password != null) {
			user = new User();
			user.setUsername(username);
			client.getParams().setAuthenticationPreemptive(true);
			client.getState().setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(username, password));
		}
	}
	
	private String getContentType() {
		if (mediaType == MEDIA_TYPE_JSON)
			return "application/json";
		else if (mediaType == MEDIA_TYPE_XML)
			return "application/xml";
		else if (mediaType == MEDIA_TYPE_CSV)
			return "text/plain";
		else
			return null;
	}
	
	private HttpMethod buildMethod(String method, String path, String entity) {
		HttpMethod m = null;
		if ("GET".equals(method)) {
			m = new GetMethod();
		} else if ("POST".equals(method)) {
			PostMethod post = new PostMethod();
			try {
				post.setRequestEntity(new StringRequestEntity(entity, getContentType(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				ServiceException.badRequest(e.getMessage());
			}
			m = post;
		} else if ("PUT".equals(method)) {
			PutMethod put = new PutMethod();
			try {
				put.setRequestEntity(new StringRequestEntity(entity, getContentType(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				ServiceException.badRequest(e.getMessage());
			}
			m = put;
		} else if ("DELETE".equals(method)) {
			m = new DeleteMethod();
		} else {
			ServiceException.badRequest("Unknown request method " + method);
		}

		try {
			m.setURI(new URI("http", host, path, null, null));
		} catch (URIException e) {
			ServiceException.badRequest("Invalid URI requested.");
		}

		m.setRequestHeader(HEADER_ACCEPT, getContentType());
		m.setRequestHeader(HEADER_USER_AGENT, USER_AGENT);
		if (apiKey != null)
			m.setRequestHeader(HEADER_KEY_API, apiKey);
		
		return m;
	}
	
	private IFeedFormatter getFormatter() {
		if (mediaType == MEDIA_TYPE_JSON)
			return new JSONFormatter();
		else if (mediaType == MEDIA_TYPE_XML)
			return new XmlFormatter();
		else
			return null;
	}

	class FeedService extends AbstractFeedService {
		
		public FeedService(String context) {
			super(context);
		}

		public Feed find(String path, QueryOption opt) throws ServiceException {
			String url = getContext() + "/" + path;
			
			HttpMethod method = buildMethod("GET", url, null);
			
			try {
				int statusCode = client.executeMethod(method);

				if (statusCode == HttpStatus.SC_OK) {
					return getFormatter().parseFeed(method.getResponseBodyAsStream());
				}
			} catch (IOException e) {
				ServiceException.error(e);
			} catch (FormatException e) {
				ServiceException.error(e);
			} finally {
				method.releaseConnection();
			}
			
			return null;
		}

		public Collection list(QueryOption opt) throws ServiceException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean create(Feed feed) throws ServiceException {
			StringWriter writer = new StringWriter();
			getFormatter().format(writer, feed);
			
			HttpMethod method = buildMethod("POST", getContext(), writer.toString());
			
			try {
				int status = client.executeMethod(method);
				if (status == HttpStatus.SC_CREATED)
					return true;
			} catch (IOException e) {
				ServiceException.error(e);
			} catch (FormatException e) {
				ServiceException.error(e);
			} finally {
				method.releaseConnection();
			}
			
			return false;
		}

		public boolean update(Feed feed) throws ServiceException {
			String url = getContext() + "/" + feed.getName();
			
			StringWriter writer = new StringWriter();
			getFormatter().format(writer, feed);
			
			HttpMethod method = buildMethod("PUT", url, writer.toString());
			
			try {
				int status = client.executeMethod(method);
				if (status == HttpStatus.SC_NO_CONTENT)
					return true;
			} catch (IOException e) {
				ServiceException.error(e);
			} catch (FormatException e) {
				ServiceException.error(e);
			} finally {
				method.releaseConnection();
			}
			
			return false;
		}

		public boolean delete(String path) throws ServiceException {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
