/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.formatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.smeshlink.misty.command.CommandRequest;
import com.smeshlink.misty.command.CommandResponse;
import com.smeshlink.misty.entity.Entry;
import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.Location;
import com.smeshlink.misty.entity.PagedList;
import com.smeshlink.misty.service.IServiceRequest;
import com.smeshlink.misty.service.IServiceResponse;
import com.smeshlink.misty.service.ServiceException;
import com.smeshlink.misty.util.Base64;
import com.smeshlink.misty.util.DateTimeUtils;

/**
 * JSON formatter.
 * 
 * @author smeshlink
 */
public class JSONFormatter implements IFeedFormatter {
	
	public void format(OutputStream stream, IServiceRequest request) throws FormatException {
		try {
			Writer w = getWriter(stream);
			JSONWriter writer = getJSONWriter(w);
			
			writer.object();
			
			writer.key("method").value(request.getMethod());
			if (request.getResource() != null)
				writer.key("resource").value(request.getResource());
			
			writer.key("headers");
			writer.object();
			Iterator it = request.getHeaders().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				writer.key(entry.getKey().toString()).value(entry.getValue());
			}
			writer.endObject();
			
			if (request.getToken() != null)
				writer.key("token").value(request.getToken());
			
			Object body = request.getBody();
			if (body != null) {
				writer.key("body");
				
				if (body instanceof Feed) {
					write(writer, (Feed) body);
				}
			}
			
			writer.endObject();
			
			w.flush();
		} catch (UnsupportedEncodingException ex) {
			throw new FormatException(ex);
		} catch (IOException ex) {
			throw new FormatException(ex);
		}
	}
	
	public void format(OutputStream stream, IServiceResponse response) throws FormatException {
		try {
			Writer w = getWriter(stream);
			JSONWriter writer = getJSONWriter(w);
			
			writer.object();

			writer.key("status").value(response.getStatus());
			if (response.getResource() != null)
				writer.key("resource").value(response.getResource());
			
			if (response.getHeaders().size() > 0) {
				writer.key("headers");
				writer.object();
				Iterator it = response.getHeaders().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					writer.key(entry.getKey().toString()).value(entry.getValue());
				}
				writer.endObject();
			}
			
			if (response.getToken() != null)
				writer.key("token").value(response.getToken());
			
			Object result = response.getBody();
			if (result != null) {
				writer.key("body");
				
				if (result instanceof Feed) {
					write(writer, (Feed) result);
				} else if (result instanceof CommandResponse) {
					write(writer, (CommandResponse) result);
				}
			}
			
			writer.endObject();
			
			w.flush();
		} catch (UnsupportedEncodingException ex) {
			throw new FormatException(ex);
		} catch (IOException ex) {
			throw new FormatException(ex);
		}
	}
	
	public void format(OutputStream stream, ServiceException e) throws FormatException {
		try {
			Writer w = getWriter(stream);
			JSONWriter writer = getJSONWriter(w);
			
			writer.object();
			writer.key("status").value(e.getStatus());
			if (e.getMessage() != null)
				writer.key("message").value(e.getMessage());
			writer.endObject();
			
			w.flush();
		} catch (UnsupportedEncodingException ex) {
			throw new FormatException(ex);
		} catch (IOException ex) {
			throw new FormatException(ex);
		}
	}

	public void format(OutputStream stream, List feeds) throws FormatException {
		try {
			Writer w = getWriter(stream);
			JSONWriter writer = getJSONWriter(w);
			
			writer.object();
			
			if (feeds instanceof PagedList) {
				PagedList pl = (PagedList) feeds;
				writer.key("totalResults").value(pl.getTotal());
				writer.key("startIndex").value(pl.getOffset());
				writer.key("itemsPerPage").value(pl.getLimit());
			}
			
			writer.key("results");
			writer.array();
			Iterator it = feeds.iterator();
			while (it.hasNext()) {
				write(writer, (Feed) it.next());
			}
			writer.endArray();
			
			writer.endObject();
			w.flush();
		} catch (UnsupportedEncodingException e) {
			throw new FormatException(e);
		} catch (IOException e) {
			throw new FormatException(e);
		}
	}

	public void format(OutputStream stream, Feed feed) throws FormatException {
		try {
			format(getWriter(stream), feed);
		} catch (UnsupportedEncodingException e) {
			throw new FormatException(e);
		}
	}
	
	public void format(Writer writer, Feed feed) throws FormatException {
		try {
			JSONWriter jw = getJSONWriter(writer);
			write(jw, feed);
			writer.flush();
		} catch (IOException e) {
			throw new FormatException(e);
		}
	}
	
	public void format(OutputStream stream, Entry value) throws FormatException {
		try {
			Writer w = getWriter(stream);
			JSONWriter writer = getJSONWriter(w);
			
			write(writer, value);
			w.flush();
		} catch (IOException e) {
			throw new FormatException(e);
		}
	}
	
	public void format(Writer writer, Object obj) {
		try {
			JSONWriter jw = getJSONWriter(writer);

			if (obj instanceof Feed) {
				write(jw, (Feed) obj);
			}
			
			writer.flush();
		} catch (IOException e) {
			throw new FormatException(e);
		}
	}
	
	public Object parseObject(Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) obj;
			ArrayList list = new ArrayList();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				list.add(parseFeed(jsonObj));
			}
		} else if (obj instanceof JSONObject) {
			JSONObject jsonObj = (JSONObject) obj;
			Feed feed = parseFeed(jsonObj);
			return feed;
		}
		
		return null;
	}
	
	public CommandRequest parseCommandRequest(JSONObject jsonObj) {
		CommandRequest cmd = new CommandRequest();
		cmd.setName(jsonObj.optString("name"));
		cmd.setCmdKey(jsonObj.optString("cmdkey"));
		JSONObject paramsObj = jsonObj.optJSONObject("params");
		if (paramsObj != null) {
			for (Iterator it = paramsObj.keys(); it.hasNext(); ) {
				String key = (String) it.next();
				cmd.getParameters().put(key, paramsObj.get(key));
			}
		}
		return cmd;
	}
	
	private static Feed parseFeed(JSONObject jsonObj) {
		Feed feed = new Feed();
		
		feed.setName(jsonObj.optString("name"));
		feed.setTitle(jsonObj.optString("title"));
		feed.setDescription(jsonObj.optString("description"));
		feed.setWebsite(jsonObj.optString("website"));
		feed.setEmail(jsonObj.optString("email"));
		
		String created = jsonObj.optString("created");
		if (created != null) {
			try {
				feed.setCreated(DateTimeUtils.fromDateTime8601(created));
			} catch (ParseException e) {
				throw new FormatException("Malformed date string " + created, e);
			}
		}
		
		String updated = jsonObj.optString("updated");
		if (updated != null) {
			try {
				feed.setUpdated(DateTimeUtils.fromDateTime8601(updated));
			} catch (ParseException e) {
				throw new FormatException("Malformed date string " + updated, e);
			}
		}
		
		String keyType = jsonObj.optString("keyType");
		if (keyType != null)
			feed.setKeyType(Feed.parseKeyType(keyType));
		String valueType = jsonObj.optString("valueType");
		if (valueType != null)
			feed.setValueType(Feed.parseValueType(valueType));
		
		JSONObject location = jsonObj.optJSONObject("location");
		if (location != null) {
			Location loc = new Location();
			feed.setLocation(loc);
			loc.setName(location.optString("name"));
			loc.setDomain(Location.parseDomain(location.optString("domain")));
			loc.setExposure(Location.parseExposure(location.optString("exposure")));
			loc.setDisposition(Location.parseDisposition(location.optString("disposition")));
			loc.setElevation(optDouble(location, "ele"));
			loc.setLongitude(optDouble(location, "lng"));
			loc.setLatitude(optDouble(location, "lat"));
			loc.setSpeed(optDouble(location, "speed"));
			loc.setBearing(optDouble(location, "bearing"));
		}
		
		JSONArray tags = jsonObj.optJSONArray("tags");
		if (tags != null) {
			for (int i = 0; i < tags.length(); i++) {
				feed.addTag(tags.getString(i));
			}
		}

		Object currentObj = jsonObj.opt("current");
		if (currentObj == null)
			currentObj = jsonObj.opt("value");
		if (currentObj != null)
			feed.setCurrentValue(parseValue(feed, currentObj));
		
		JSONArray data = jsonObj.optJSONArray("data");
		if (data != null) {
			for (int i = 0; i < data.length(); i++) {
				Entry entry = parseEntry(feed, data.getJSONObject(i));
				if (entry != null)
					feed.addEntry(entry);;
			}
		}
		
		JSONArray children = jsonObj.optJSONArray("children");
		if (children != null) {
			for (int i = 0; i < children.length(); i++) {
				Feed child = parseFeed(children.getJSONObject(i));
				feed.addChild(child);
			}
		}
		
		return feed;
	}
	
	private static Double optDouble(JSONObject json, String key) {
		return json.has(key) ? new Double(json.optDouble(key)) : null;
	}

	private static Entry parseEntry(Feed feed, JSONObject jsonObject) {
		int keyType = feed.getKeyType();
		if (keyType == Feed.KEY_NONE)
			// assume string
			keyType = Feed.KEY_STRING;
		String keyStr = jsonObject.optString("at");
		Object key;
		if (keyStr == null) {
			key = jsonObject.optString("key");
		} else {
			try {
				key = DateTimeUtils.fromDateTime8601(keyStr);
			} catch (ParseException e) {
				throw new FormatException(e);
			}
		}
		
		if (key == null) {
			return null;
		} else {
			Entry value = new Entry();
			value.setKey(key);
			Object valObj = jsonObject.opt("value");
			if (valObj != null)
				value.setValue(parseValue(feed, valObj));
			return value;
		}
	}

	private static Object parseValue(Feed feed, Object obj) {
		int valueType = feed.getValueType();
		if (valueType == Feed.VALUE_NONE)
			// assume number
			valueType = Feed.VALUE_NUMBER;
		
		switch (valueType) {
		case Feed.VALUE_INTEGER:
			return Integer.valueOf(obj.toString());
		case Feed.VALUE_NUMBER:
			return Double.valueOf(obj.toString());
		case Feed.VALUE_STRING:
			return obj.toString();
		case Feed.VALUE_BYTES:
			try {
				return Base64.decode(obj.toString());
			} catch (Exception e) {
				throw new FormatException(e);
			}
		}
		
		return null;
	}

	public Entry parseValue(InputStream inputStream) throws FormatException {
		return null;
	}
	
	private void write(JSONWriter writer, Feed feed) {
		writer.object();

		writeValue(writer, "name", feed.getName());
		writeValue(writer, "title", feed.getTitle());
		writeValue(writer, "description", feed.getDescription());
		writeValue(writer, "website", feed.getWebsite());
		writeValue(writer, "email", feed.getEmail());
		writeValue(writer, "status", feed.getStatusString());
		if (feed.getCreated() != null)
			writer.key("created").value(DateTimeUtils.toDateTime8601(feed.getCreated()));
		if (feed.getUpdated() != null)
			writer.key("updated").value(DateTimeUtils.toDateTime8601(feed.getUpdated()));
		
		if (feed.getKeyType() != Feed.KEY_NONE)
			writer.key("keyType").value(feed.getKeyTypeString());
		if (feed.getValueType() != Feed.KEY_NONE)
			writer.key("valueType").value(feed.getValueTypeString());
		
		if (feed.getAccess() == Feed.ACCESS_PRIVATE)
			writer.key("private").value("true");
		
		if (feed.getTags() != null) {
			writer.key("tags");
			writer.array();
			for (Iterator itTag = feed.getTags().iterator(); itTag.hasNext(); ) {
				writer.value(itTag.next());
			}
			writer.endArray();
		}
		
		if (feed.getLocation() != null) {
			Location loc = feed.getLocation();
			writer.key("location");
			writer.object();
			
			if (loc.getDomain() != null)
				writer.key("domain").value(loc.getDomainString());
			if (loc.getDisposition() != null)
				writer.key("disposition").value(loc.getDispositionString());
			if (loc.getExposure() != null)
				writer.key("exposure").value(loc.getExposureString());

			writeValue(writer, "name", loc.getName());
			writeValue(writer, "lat", loc.getLatitude());
			writeValue(writer, "lng", loc.getLongitude());
			writeValue(writer, "ele", loc.getElevation());
			writeValue(writer, "speed", loc.getSpeed());
			writeValue(writer, "bearing", loc.getBearing());
			
			writer.endObject();
		}
		
		if (feed.getUnit() != null) {
			writer.key("unit");
			writer.object();
            writeValue(writer, "label", feed.getUnit().getLabel());
            writeValue(writer, "symbol", feed.getUnit().getSymbol());
            writeValue(writer, "type", feed.getUnit().getType());
			writer.endObject();
		}
		
		if (feed.getCurrentValue() != null) {
			writer.key("current").value(feed.getCurrentValue());
		}
		
		if (feed.getEntries() != null) {
			Iterator itValue = feed.getEntries().iterator();
			if (itValue.hasNext()) {
				writer.key("data");
				writer.array();
				do {
					write(writer, (Entry) itValue.next());
				} while (itValue.hasNext());
				writer.endArray();
			}
		}
		
		if (feed.getChildren() != null) {
			Iterator itChild = feed.getChildren().iterator();
			if (itChild.hasNext()) {
				writer.key("children");
				writer.array();
				do {
					write(writer, (Feed) itChild.next());
				} while (itChild.hasNext());
				writer.endArray();
			}
		}
		
		writer.endObject();
	}

	private void write(JSONWriter writer, Entry value) {
		writer.object();
		Object key = value.getKey();
		if (key instanceof Date)
			writer.key("at").value(DateTimeUtils.toDateTime8601((Date) key));
		else
			writer.key("key").value(key.toString());
		writer.key("value").value(value.getValue());
		writer.endObject();
	}
	
	private void writeValue(JSONWriter writer, String name, Object value) {
		if (value != null)
			writer.key(name).value(value);
	}
	
	private Writer getWriter(OutputStream stream) throws UnsupportedEncodingException {
		return new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
	}
	
	private JSONWriter getJSONWriter(Writer writer) throws UnsupportedEncodingException {
		return new JSONWriter(writer);
	}
	
	private void write(JSONWriter writer, CommandResponse response) {
		writer.object();
		
		writeValue(writer, "status", String.valueOf(response.getStatus()));
		writeValue(writer, "cmdkey", response.getCmdKey());
		
		if (response.getBody() != null) {
			writer.key("body").value(response.getBody());
		}
		
		writer.endObject();
	}

	public Collection parseFeeds(InputStream stream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
				sb.append(line);
			
			return parseFeeds(new JSONObject(sb.toString()));
		} catch (IOException e) {
			throw new FormatException(e);
		}
	}

	public Collection parseFeeds(Object obj) {
		if (obj instanceof Collection) {
			return (Collection) obj;
		} else if (obj instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) obj;
			ArrayList list = new ArrayList();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				list.add(parseFeed(jsonObj));
			}
			return list;
		} else if (obj instanceof JSONObject) {
			JSONObject jsonObj = (JSONObject) obj;
			return parseFeeds(jsonObj);
		} else {
			return null;
		}
	}

	public Feed parseFeed(InputStream stream) throws FormatException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
				sb.append(line);
			
			return parseFeed(new JSONObject(sb.toString()));
		} catch (IOException e) {
			throw new FormatException(e);
		}
	}
	
	public Feed parseFeed(Object obj) {
		if (obj instanceof Feed)
			return (Feed) obj;
		else if (obj instanceof JSONObject)
			return parseFeed((JSONObject) obj);
		else
			return null;
	}
	
	private static Collection parseFeeds(JSONObject jObj) {
		PagedList list = new PagedList();
		list.setTotal(jObj.optInt("totalResults"));
		JSONArray array = jObj.optJSONArray("results");
		if (array != null) {
			for (int i = 0; i < array.length(); i++) { 
				list.add(parseFeed(array.getJSONObject(i)));
			}
		}
		return list;
	}
}
