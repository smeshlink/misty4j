/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a feed.
 * 
 * @author smeshlink
 * 
 */
public class Feed {
	public static final int KEY_NONE = 0;
	public static final int KEY_DATE = 1;
	public static final int KEY_STRING = 2;
	public static final int VALUE_NONE = 0;
	public static final int VALUE_INTEGER = 1;
	public static final int VALUE_NUMBER = 2;
	public static final int VALUE_STRING = 3;
	public static final int VALUE_BYTES = 4;
	
	public static final int STATUS_LIVE = 0;
	public static final int STATUS_FROZEN = 1;

	public static final int ACCESS_PUBLIC = 0;
	public static final int ACCESS_PRIVATE = 1;
	
	private Long id;
	private Long parentId;
	private Feed parent;
	private String name;
	private String title;
	private String description;
	private int keyType;
	private int valueType;
	private Date created;
	private Date updated;
	private int status;
	private int access;
	private Location location;
	private Unit unit;
	private String website;
	private String email;
	private List tags;
	private List children;
	private Map entriesMap;
	private Object currentValue;
	
	public static String getStatusString(int status) {
		if (status == STATUS_FROZEN)
			return "frozen";
		else
			return "live";
	}
	
	public static int parseStatus(String status) {
		if ("frozen".equalsIgnoreCase(status))
			return STATUS_FROZEN;
		else
			return STATUS_LIVE;
	}
	
	public static String getKeyTypeString(int keyType) {
		if (keyType == KEY_DATE)
			return "date";
		else if (keyType == KEY_STRING)
			return "string";
		else
			return "none";
	}
	
	public static int parseKeyType(String keyType) {
		if ("date".equalsIgnoreCase(keyType))
			return KEY_DATE;
		else if ("string".equalsIgnoreCase(keyType))
			return KEY_STRING;
		else
			return KEY_NONE;
	}
	
	public static String getValueTypeString(int valueType) {
		if (valueType == VALUE_INTEGER)
			return "integer";
		else if (valueType == VALUE_NUMBER)
			return "number";
		else if (valueType == VALUE_STRING)
			return "string";
		else if (valueType == VALUE_BYTES)
			return "bytes";
		else
			return "none";
	}
	
	public static int parseValueType(String valueType) {
		if ("integer".equalsIgnoreCase(valueType))
			return VALUE_INTEGER;
		else if ("number".equalsIgnoreCase(valueType))
			return VALUE_NUMBER;
		else if ("string".equalsIgnoreCase(valueType))
			return VALUE_STRING;
		else if ("bytes".equalsIgnoreCase(valueType))
			return VALUE_BYTES;
		else
			return VALUE_NONE;
	}
	
	public void setId(long id) {
		this.id = Long.valueOf(id);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setParentId(long parentId) {
		this.parentId = Long.valueOf(parentId);
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getUpdated() {
		return updated;
	}
	
	public void addChild(Feed feed) {
		if (children == null)
			children = new ArrayList();
		feed.setParent(this);
		children.add(feed);
	}

	public void addChildren(Collection children) {
		for (Iterator it = children.iterator(); it.hasNext(); ) {
			((Feed) it.next()).setParent(this);
		}
		getChildren().addAll(children);
	}

	public Collection getChildren() {
		if (children == null)
			children = new ArrayList();
		return children;
	}
	
	public void addEntries(Collection values) {
		Iterator it = values.iterator();
		while (it.hasNext())
			addEntry((Entry) it.next());
	}
	
	public void addEntry(Entry value) {
		if (entriesMap == null)
			entriesMap = new LinkedHashMap();
		entriesMap.put(value.getKey(), value);
	}

	public Collection getEntries() {
		return entriesMap == null ? null : entriesMap.values();
	}
	
	public void setEntriesMap(Map map) {
		entriesMap = map;
	}
	
	public Map getEntriesMap() {
		return entriesMap;
	}
	
	public void addTag(String tag) {
		if (this.tags == null)
			this.tags = new ArrayList();
		this.tags.add(tag);
	}
	
	public void addTags(Collection tags) {
		if (this.tags == null)
			this.tags = new ArrayList();
		this.tags.addAll(tags);
	}
	
	public Collection getTags() {
		return tags;
	}

	public void setCurrentValue(Object currentValue) {
		this.currentValue = currentValue;
	}

	public Object getCurrentValue() {
		return currentValue;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}

	public int getKeyType() {
		return keyType;
	}
	
	public String getKeyTypeString() {
		return getKeyTypeString(keyType);
	}

	public void setValueType(int valueType) {
		this.valueType = valueType;
	}

	public int getValueType() {
		return valueType;
	}
	
	public String getValueTypeString() {
		return getValueTypeString(valueType);
	}

	public void setParent(Feed parent) {
		this.parent = parent;
		setParentId(parent == null ? null : parent.getId());
	}

	public Feed getParent() {
		return parent;
	}

	public String getPath() {
		return parent == null ? name : parent.getPath() + "/" + name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
	
	public String getStatusString() {
		return getStatusString(status);
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public int getAccess() {
		return access;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
