/**
 * Copyright (c) 2011-2014 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for IoT.
 */
package com.smeshlink.misty.service;

import com.smeshlink.misty.util.Base64;

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

class ApiKeyCredential implements ICredential {
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

class UserCredential implements ICredential {
	private final String username;
    private final String password;
	private final Pair pair;
	
	public UserCredential(final String username, final String password) {
		this.username = username;
		this.password = password;
		this.pair = new Pair() {
			public String getKey() {
				return "Authorization";
			}

			public String getValue() {
				return "BASIC " + Base64.encodeString(username + ":" + password);
			}
		};
    }

	public Pair getCredential() {
		return pair;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
