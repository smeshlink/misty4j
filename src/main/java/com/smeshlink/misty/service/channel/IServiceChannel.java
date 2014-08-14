/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service.channel;


import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.User;
import com.smeshlink.misty.service.IFeedService;

/**
 * @author Longshine
 * 
 */
public interface IServiceChannel {
	IFeedService feed();
	IFeedService feed(User user);
	IFeedService feed(Feed parent);
	void setTimeout(int timeout);
	int getTimeout();
	void addCommandListener(ICommandListener listener);
	void removeCommandListener(ICommandListener listener);
}
