/**
 * Copyright (c) 2011-2014 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for IoT.
 */
package com.smeshlink.misty.service;

import java.util.Collection;

import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.User;
import com.smeshlink.misty.formatter.IFeedFormatter;
import com.smeshlink.misty.formatter.JSONFormatter;
import com.smeshlink.misty.formatter.XmlFormatter;
import com.smeshlink.misty.service.channel.HttpChannel;
import com.smeshlink.misty.service.channel.IServiceChannel;
import com.smeshlink.misty.service.channel.TcpChannel;

/**
 * @author Long
 *
 */
public class MistyService implements IMistyService {
	public static final String DEFAULT_API_HOST = "api.misty.smeshlink.com";
	public static final String VERSION = "Misty-Java-Lib/1.0";
	public static final String HEADER_ACCEPT = "Accept";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_USER_AGENT = "User-Agent";
	
	private IServiceChannel channel;
	private ICredential credential;
	
	public MistyService() {
		this(DEFAULT_API_HOST, true);
	}

	public MistyService(String host) {
		this(host, true);
	}

	public MistyService(boolean useHttp) {
		this(DEFAULT_API_HOST, useHttp);
	}

	public MistyService(String host, boolean useHttp) {
		this(useHttp ? (IServiceChannel) new HttpChannel(host)
				: new TcpChannel(host));
	}

	public MistyService(IServiceChannel channel) {
		this.channel = channel;
	}

	public IServiceChannel getChannel() {
		return channel;
	}

	public void setChannel(IServiceChannel channel) {
		this.channel = channel;
	}

	public ICredential getCredential() {
		return credential;
	}

	public void setCredential(ICredential credential) {
		this.credential = credential;
	}
	
	/**
	 * Sets an API key as the credential.
	 * @param apiKey
	 */
    public void setApiKey(String apiKey) {
        credential = new ApiKeyCredential(apiKey);
    }

    /**
     * Sets a user as the credential.
     * @param username
     * @param password
     */
    public void setUser(String username, String password) {
        credential = new UserCredential(username, password);
    }

	public IServiceResponse execute(IServiceRequest request) {
		return channel.execute(request);
	}

	public IFeedService feed() {
		return new FeedServiceImpl(null);
	}

	public IFeedService feed(User owner) {
		return new FeedServiceImpl(owner.getUsername());
	}

	public IFeedService feed(Feed parent) {
		return new FeedServiceImpl(parent == null ? null : ("/feeds/" + parent.getPath()));
	}
	
	class FeedServiceImpl implements IFeedService {
		private final String context;
		private ICredential credential;
		
		public FeedServiceImpl(String context) {
			this.credential = MistyService.this.getCredential();
			
			if (context == null || context.length() == 0)
				context = "/feeds";
            else if (context.charAt(0) != '/')
            	context = "/" + context;
			
			this.context = context;
		}
		
		public Collection list() throws ServiceException {
			return list(QueryOption.DEFAULT);
		}

		public Collection list(QueryOption opt) throws ServiceException {
			ServiceRequestImpl request = newRequest("GET");
			request.setResource(context);
			toParameters(request, opt);
			
			IServiceResponse response = null;
			try {
				response = execute(request);
				
				if (response == null) {
					throw ServiceException.timeout(null);
				} else if (response.getStatus() != 200) {
					throw new ServiceException(response.getStatus());
				} else {
					IFeedFormatter formatter = getFormatter(request.getFormat());
					if (response.getBody() == null)
						return formatter.parseFeeds(response.getResponseStream());
					else
						return formatter.parseFeeds(response.getBody());
				}
			} catch (ServiceException e) {
				throw e;
			} catch (Exception e) {
				throw ServiceException.error(e);
			} finally {
				if (response != null)
					response.dispose();
			}
		}

		public Feed find(String path) throws ServiceException {
			return find(path, QueryOption.DEFAULT);
		}
		
		public Feed find(String path, QueryOption opt) throws ServiceException {
			ServiceRequestImpl request = newRequest("GET");
			request.setResource(context + "/" + path);
			toParameters(request, opt);
			
			IServiceResponse response = null;
			try {
				response = execute(request);
				
				if (response == null) {
					throw ServiceException.timeout(null);
				} else if (response.getStatus() != 200) {
					throw new ServiceException(response.getStatus());
				} else {
					IFeedFormatter formatter = getFormatter(request.getFormat());
					if (response.getBody() == null)
						return formatter.parseFeed(response.getResponseStream());
					else
						return formatter.parseFeed(response.getBody());
				}
			} catch (ServiceException e) {
				throw e;
			} catch (Exception e) {
				throw ServiceException.error(e);
			} finally {
				if (response != null)
					response.dispose();
			}
		}
		
		public boolean create(Feed feed) throws ServiceException {
			return false;
		}

		public boolean update(Feed feed) throws ServiceException {
			return false;
		}

		public boolean delete(String path) throws ServiceException {
			return false;
		}
		
		private ServiceRequestImpl newRequest(String method) {
            ServiceRequestImpl request = new ServiceRequestImpl();
            request.setMethod(method);
            request.setCredential(credential);
            return request;
        }
	}
	
	private static IFeedFormatter getFormatter(String format) {
		if (format == null || "json".equalsIgnoreCase(format))
			return new JSONFormatter();
		else if ("xml".equalsIgnoreCase(format))
			return new XmlFormatter();
		else
			return null;
	}
	
	private static void toParameters(ServiceRequestImpl request, QueryOption opt) {
        if (opt.getLimit() > -1)
            request.getParameters().put("limit", new Integer(opt.getLimit()));
        if (opt.getOffset() > 0)
            request.getParameters().put("offset", new Integer(opt.getOffset()));
        if (opt.getStartTime() != null)
            request.getParameters().put("start", opt.getStartTime());
        if (opt.getEndTime() != null)
            request.getParameters().put("end", opt.getEndTime());
        if (opt.getOrder() != null)
            request.getParameters().put("order", opt.getOrder());
        if (opt.isDesc())
            request.getParameters().put("desc", new Boolean(opt.isDesc()));

        if (opt.getContent() != QueryOption.WHATEVER)
            request.getParameters().put("content", opt.getContentString());
        if (opt.getSample()  != QueryOption.WHATEVER)
            request.getParameters().put("sample", opt.getSampleString());
        if (opt.getView()  != QueryOption.WHATEVER)
            request.getParameters().put("view", opt.getViewString());

        if (opt.getStatus() != QueryOption.WHATEVER)
            request.getParameters().put("status", opt.getStatusString());
    }
	
	public static String getContentType(String format) {
        if ("json".equalsIgnoreCase(format))
            return "application/json";
        else if ("xml".equalsIgnoreCase(format))
            return "application/xml";
        else if ("csv".equalsIgnoreCase(format))
            return "text/csv";
        else
            return null;
    }
}
