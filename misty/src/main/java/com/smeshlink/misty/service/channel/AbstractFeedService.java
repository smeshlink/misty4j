/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service.channel;

public abstract class AbstractFeedService implements IFeedService {
	private String context;
	
	public AbstractFeedService(String context) {
		if (context == null || context.length() == 0)
			this.context = "/feeds";
		else if (context.charAt(0) == '/')
			this.context = context;
		else
			this.context = "/" + context;
	}
	
	public String getContext() {
		return context;
	}
}
