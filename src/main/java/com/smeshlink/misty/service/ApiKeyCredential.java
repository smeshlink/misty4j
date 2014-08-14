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
public class ApiKeyCredential implements ICredential {
	private final Pair pair;
	
	public ApiKeyCredential(final String apiKey) {
		pair = new Pair() {
			public String getKey() {
				return "X-ApiKey";
			}

			public String getValue() {
				return apiKey;
			}
		};
    }
	
	public Pair getCredential() {
		return pair;
	}
	
	public String getApiKey() {
		return pair.getValue();
	}
}
