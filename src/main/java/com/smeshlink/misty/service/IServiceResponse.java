/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author smeshlink
 *
 */
public interface IServiceResponse {
	String getResource();
	/**
	 * Gets the status code.
	 * @return
	 */
	int getStatus();
	Object getBody();
	/**
	 * Gets all response headers.
	 * @return
	 */
	Map getHeaders();
	String getToken();
	void setToken(String token);
	/**
	 * Gets the response stream.
	 * @return
	 * @throws IOException 
	 */
	InputStream getResponseStream() throws IOException;
	void dispose();
}
