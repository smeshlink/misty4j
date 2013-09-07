/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.formatter;

/**
 * @author smeshlink
 *
 */
public class FormatException extends RuntimeException {
	private static final long serialVersionUID = 627136717575801822L;

	public FormatException(String message) {
		super(message);
    }
	
	public FormatException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public FormatException(Throwable cause) {
        super(cause);
    }
}
