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
 * Represents a request to a service.
 * 
 * @author smeshlink
 *
 */
public interface IServiceRequest {
	/**
	 * Returns the name of the method with which this
	 * request was made, for example, GET, POST, or PUT. 
	 * @return a <code>String</code> specifying the name of the method
	 */
	String getMethod();
	/**
	 * Returns the fully qualified name of the client
	 * or the last proxy that sent the request.
	 * 
	 * @return a <code>String</code> containing the fully qualified name of the client
	 */
	String getHost();
	/**
	 * Returns the login of the user making this request, if the
     * user has been authenticated, or <code>null</code> if the user 
     * has not been authenticated.
     * 
	 * @return a <code>String</code> specifying the login user
	 */
	String getUser();
	/**
	 * Returns the value of the specified request header
	 * as a <code>String</code>. If the request did not include a header
     * of the specified name, this method returns <code>null</code>.
     * If there are multiple headers with the same name, this method
     * returns the first head in the request.
     * The header name is case insensitive. You can use
     * this method with any request header.
     * 
	 * @param name a <code>String</code> specifying the header name
	 * @return a <code>String</code> containing the value of the requested header
	 */
	String getHeader(String name);
	Map getHeaders();
	/**
	 * Returns the value of a request parameter as a <code>String</code>,
     * or <code>null</code> if the parameter does not exist.
     * 
     * <p>You should only use this method when you are sure the
     * parameter has only one value. If the parameter might have
     * more than one value, use {@link #getParameters}.
     *
     * <p>If you use this method with a multivalued
     * parameter, the value returned is equal to the first value
     * in the array returned by <code>getParameters</code>.
     * 
	 * @param name a <code>String</code> specifying the name of the parameter
	 * @return a <code>String</code> representing the single value of the parameter
	 * 
	 * @see	#getParameters
	 */
	String getParameter(String name);
	/**
	 * Returns an array of <code>String</code> objects containing 
	 *  all of the values the given request parameter has, or 
     * <code>null</code> if the parameter does not exist.
     * 
     * <p>If the parameter has a single value, the array has a length
     * of 1.
     * 
	 * @param name a <code>String</code> specifying the name of the parameter
	 * @return an array of <code>String</code> objects containing the parameter's values
	 * 
	 * @see	#getParameter
	 */
	String[] getParameters(String name);
	InputStream getInputStream() throws IOException;
	String getToken();
	Object getBody();
	/**
	 * Gets the fully qualified name of the resource being requested.
	 * 
	 * @return the fully qualified name of the resource being requested
	 */
	String getResource();
	/**
	 * Gets the name of format used in this request.
	 * 
	 * @return the name of format
	 */
	String getFormat();
	void setFormat(String format);
}
