/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.entity;

import java.util.ArrayList;

/**
 * @author smeshlink
 * 
 */
public class PagedList extends ArrayList {
	private static final long serialVersionUID = -4351577652796431044L;

	private int total;
	private int offset;
	private int limit;

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}
}
