/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.entity;

import java.util.Date;

/**
 * Represents a data entry with a certain key-value pair.
 * 
 * @author smeshlink
 *
 */
public class Entry {
	private Long id;
	private Long feedId;
	private Object key;
	private Object value;
	
	/**
	 * Sets the id of this entry.
	 */
	public void setId(long id) {
		this.id = Long.valueOf(id);
	}

	/**
	 * Sets the id of this entry.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the id of this entry.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the feed id of this entry.
	 */
	public void setFeedId(long feedId) {
		this.feedId = Long.valueOf(feedId);
	}

	/**
	 * Sets the feed id of this entry.
	 */
	public void setFeedId(Long feedId) {
		this.feedId = feedId;
	}

	/**
	 * Gets the feed id of this entry.
	 */
	public Long getFeedId() {
		return feedId;
	}

	/**
	 * Sets the key of this entry.
	 */
	public void setKey(Object key) {
		this.key = key;
	}

	/**
	 * Gets the key of this entry.
	 */
	public Object getKey() {
		return this.key;
	}

	/**
	 * Sets the value of this entry.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Gets the value of this entry.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the type of key of this entry.
	 */
	public int getKeyType() {
		if (key == null)
			return Feed.KEY_NONE;
		else if (Date.class.isInstance(key))
			return Feed.KEY_DATE;
		else
			return Feed.KEY_STRING;
	}

	/**
	 * Gets the type of value of this entry.
	 */
	public int getValueType() {
		if (value == null)
			return Feed.VALUE_NONE;
		Class c = value.getClass();
		if (c == Integer.class || c == Short.class)
			return Feed.VALUE_INTEGER;
		else if (Number.class.isAssignableFrom(c))
			return Feed.VALUE_NUMBER;
		else if (c == String.class)
			return Feed.VALUE_STRING;
		else if (c == byte[].class)
			return Feed.VALUE_BYTES;
		else
			return Feed.VALUE_NONE;
	}
}
