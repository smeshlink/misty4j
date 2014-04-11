/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.command;

import java.util.HashMap;
import java.util.Map;

/**
 * @author smeshlink
 *
 */
public class CommandRequest {
	private String name;
	private Map parameters;
	private String cmdKey;
	
	public CommandRequest() {
		parameters = new HashMap();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Map getParameters() {
		return parameters;
	}

	public String getCmdKey() {
		return cmdKey;
	}

	public void setCmdKey(String cmdKey) {
		this.cmdKey = cmdKey;
	}
}
