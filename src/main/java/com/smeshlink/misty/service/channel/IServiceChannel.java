/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service.channel;

import com.smeshlink.misty.service.IServiceRequest;
import com.smeshlink.misty.service.IServiceResponse;

/**
 * @author Longshine
 * 
 */
public interface IServiceChannel {
	void setTimeout(int timeout);
	int getTimeout();
	void setRequestListener(IRequestListener listener);
	
	/**
	 * Executes a request and returns its response.
	 * @param request
	 * @return
	 */
	IServiceResponse execute(IServiceRequest request);
}
