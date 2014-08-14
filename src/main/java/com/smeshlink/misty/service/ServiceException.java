/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

/**
 * @author smeshlink
 *
 */
public class ServiceException extends RuntimeException {
	public static final int FORBIDDEN = 403;
	public static final int BAD_REQUEST = 400;
	public static final int INTERNAL_SERVER_ERROR = 500;
	private static final long serialVersionUID = 8417722391193724582L;
	
	private int status;
	
	public ServiceException(int status) {
		super();
		this.status = status;
	}
	
	public ServiceException(int status, String message) {
		super(message);
		this.status = status;
	}
	
	public ServiceException(int status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
	
	public String getMessage() {
		if (super.getMessage() == null) {
			Throwable t = getCause();
			return t == null ? null : t.getMessage();
		} else {
			return super.getMessage();
		}
	}
	
	public static void forbidden() throws ServiceException {
		throw new ServiceException(FORBIDDEN);
	}
	
	public static ServiceException error(Throwable cause) throws ServiceException {
		return new ServiceException(INTERNAL_SERVER_ERROR, null, cause);
	}
	
	public static void badRequest(String message) throws ServiceException {
		throw new ServiceException(BAD_REQUEST, message);
	}
	
	public static ServiceException timeout(String message) throws ServiceException {
		return new ServiceException(504, message);
	}
}
