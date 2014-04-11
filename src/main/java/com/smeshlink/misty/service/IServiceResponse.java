/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

import java.util.Map;

/**
 * @author smeshlink
 *
 */
public interface IServiceResponse {
	String getResource();
	int getStatus();
	Object getBody();
	Map getHeaders();
	String getToken();
	void setToken(String token);
}
