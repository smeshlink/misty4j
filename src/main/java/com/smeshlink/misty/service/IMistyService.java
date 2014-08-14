/**
 * Copyright (c) 2011-2014 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for IoT.
 */
package com.smeshlink.misty.service;

import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.User;
import com.smeshlink.misty.service.channel.IServiceChannel;

/**
 * @author Long
 *
 */
public interface IMistyService {
	/**
	 * Gets the {@link ICredential} to use.
	 */
	ICredential getCredential();
	/**
	 * Sets the {@link ICredential} to use.
	 */
	void setCredential(ICredential credential);
	/**
	 * Gets the {@link IServiceChannel} to use.
	 */
	IServiceChannel getChannel();
	/**
	 * Sets the {@link IServiceChannel} to use.
	 */
	void setChannel(IServiceChannel channel);
	/**
	 * Executes a request and returns its response.
	 * @param request the {@link IServiceRequest} to send
	 * @return an {@link IServiceResponse}, or null if timeout
	 */
	IServiceResponse execute(IServiceRequest request);
	IFeedService feed();
	IFeedService feed(User owner);
	IFeedService feed(Feed parent);
}
