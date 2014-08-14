/**
 * Copyright (c) 2011-2014 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for IoT.
 */
package com.smeshlink.misty.service;

/**
 * Represents a service credential.
 * 
 * @author Long
 *
 */
public interface ICredential {
	/**
	 * Gets credential in a key-value pair.
	 */
	Pair getCredential();
	
	interface Pair {
		String getKey();
		String getValue();
	}
}
