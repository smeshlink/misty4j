/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service.channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

import com.smeshlink.misty.entity.Entry;
import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.User;
import com.smeshlink.misty.formatter.FormatException;
import com.smeshlink.misty.formatter.IFeedFormatter;
import com.smeshlink.misty.formatter.XmlFormatter;
import com.smeshlink.misty.service.QueryOption;
import com.smeshlink.misty.service.ServiceException;

/**
 * @author smeshlink
 *
 */
public class HttpChannel2 implements IServiceChannel2 {
	private String host = "";
	private String username;
	private String password;
	private User user;
	IFeedFormatter formatter = new XmlFormatter();
	HttpClient client = new HttpClient();
	
	public HttpChannel2() {
		client.getParams().setAuthenticationPreemptive(true);
	}
	
	public HttpChannel2(String username, String password) {
		this();
		this.username = username;
		this.password = password;
		prepareUsernamePasswordCredentials();
	}

	public List listFeed(Feed parent, QueryOption opt) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Feed findFeed(User creator, String path, QueryOption opt) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public Feed findFeed(String path, QueryOption opt) throws ServiceException {
		return findFeed(user, path, opt);
	}
	
	public Feed findFeed(Feed parent, String name, QueryOption opt) throws ServiceException {
		String url = host + "feeds";
		if (parent != null)
			url += "/" + parent.getPath();
		url += "/" + name;
		
		GetMethod method = new GetMethod(url);
		
		try {
			int statusCode = client.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK) {
				return formatter.parseFeed(method.getResponseBodyAsStream());
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
	
	public Entry findEntry(Feed feed, Object key, QueryOption opt) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean createFeed(Feed feed) throws ServiceException {
		String url = host + "feeds";
		if (feed.getParent() != null)
			url += "/" + feed.getParent().getPath();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		formatter.format(out, feed);
		
		PostMethod post = new PostMethod(url);
		post.setRequestEntity(new ByteArrayRequestEntity(out.toByteArray()));
		
		try {
			int status = client.executeMethod(post);
			if (status == HttpStatus.SC_CREATED)
				return true;
		} catch (IOException e) {
			ServiceException.error(e);
		} catch (FormatException e) {
			ServiceException.error(e);
		} finally {
			post.releaseConnection();
		}
		
		return false;
	}
	
	public boolean createFeed(Feed parent, Feed feed) throws ServiceException {
		feed.setParent(parent);
		return createFeed(feed);
	}

	public boolean updateFeed(Feed feed) throws ServiceException {
		String url = host + "feeds" + "/" + feed.getPath();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		formatter.format(out, feed);
		
		PutMethod put = new PutMethod(url);
		put.setRequestEntity(new ByteArrayRequestEntity(out.toByteArray()));
		
		try {
			int status = client.executeMethod(put);
			if (status == HttpStatus.SC_NO_CONTENT)
				return true;
		} catch (IOException e) {
			ServiceException.error(e);
		} catch (FormatException e) {
			ServiceException.error(e);
		} finally {
			put.releaseConnection();
		}
		
		return false;
	}
	
	public boolean deleteFeed(Feed feed) throws ServiceException {
		// TODO Auto-generated method stub
		return false;
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
	
	private void prepareUsernamePasswordCredentials() {
		if (username != null && password != null) {
			user = new User();
			user.setUsername(username);
			client.getState().setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(username, password));
		}
	}
}
