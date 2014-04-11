/**
 * 
 */
package com.smeshlink.misty.command;

/**
 * @author smeshlink
 *
 */
public class CommandResponse {
	private int status;
	private Object body;
	private String cmdKey;
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setBody(Object body) {
		this.body = body;
	}

	public Object getBody() {
		return body;
	}

	public String getCmdKey() {
		return cmdKey;
	}

	public void setCmdKey(String cmdKey) {
		this.cmdKey = cmdKey;
	}
}
