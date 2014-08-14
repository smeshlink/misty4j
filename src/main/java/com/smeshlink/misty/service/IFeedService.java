/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

import java.util.Collection;

import com.smeshlink.misty.entity.Feed;

/**
 * @author Longshine
 * 
 */
public interface IFeedService {
	Feed find(String path, QueryOption opt) throws ServiceException;
	Collection list(QueryOption opt) throws ServiceException;
	boolean create(Feed feed) throws ServiceException;
	boolean update(Feed feed) throws ServiceException;
	boolean delete(String path) throws ServiceException;
}
