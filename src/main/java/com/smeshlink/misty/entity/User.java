/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.entity;

/**
 * @author smeshlink
 * 
 */
public class User {
	private Long id = Long.valueOf(0);
	private String username;

	public void setId(long id) {
		this.id = Long.valueOf(id);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
