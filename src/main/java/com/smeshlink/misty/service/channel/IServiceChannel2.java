/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service.channel;

import java.util.List;

import com.smeshlink.misty.entity.Entry;
import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.User;
import com.smeshlink.misty.service.QueryOption;
import com.smeshlink.misty.service.ServiceException;

/**
 * @author Longshine
 * 
 */
public interface IServiceChannel2 {
	Feed findFeed(User creator, String path, QueryOption opt) throws ServiceException;
	Feed findFeed(String path, QueryOption opt) throws ServiceException;
	Feed findFeed(Feed parent, String name, QueryOption opt) throws ServiceException;
	List listFeed(Feed parent, QueryOption opt) throws ServiceException;
	
	Entry findEntry(Feed feed, Object key, QueryOption opt) throws ServiceException;

	boolean createFeed(Feed feed) throws ServiceException;
	boolean createFeed(Feed parent, Feed feed) throws ServiceException;
	boolean updateFeed(Feed feed) throws ServiceException;
	boolean deleteFeed(Feed feed) throws ServiceException;
}
