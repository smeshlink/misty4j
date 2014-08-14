/**
 * Copyright (c) 2011-2014 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for IoT.
 */
package com.smeshlink.misty.service.channel;

import java.util.EventListener;

import com.smeshlink.misty.service.IServiceRequest;
import com.smeshlink.misty.service.IServiceResponse;

/**
 * @author smeshlink
 *
 */
public interface IRequestListener extends EventListener {
	IServiceResponse process(IServiceRequest request);
}
