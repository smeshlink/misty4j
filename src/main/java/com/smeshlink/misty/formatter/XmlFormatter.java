/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.formatter;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ws.commons.serialize.XMLWriter;
import org.apache.ws.commons.serialize.XMLWriterImpl;
import org.apache.ws.commons.util.Base64;
import org.apache.ws.commons.util.Base64.DecodingException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.smeshlink.misty.entity.Entry;
import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.Location;
import com.smeshlink.misty.entity.PagedList;
import com.smeshlink.misty.entity.Unit;
import com.smeshlink.misty.service.ServiceException;
import com.smeshlink.misty.util.DateTimeUtils;

/**
 * XML formatter.
 * 
 * @author smeshlink
 */
public class XmlFormatter implements IFeedFormatter {
	
	public void format(OutputStream stream, ServiceException e) throws FormatException {
		try {
			ContentHandler handler = getXmlWriter(stream);
			
			handler.startDocument();
			handler.startElement(EmptyUri, TAG_ERROR, TAG_ERROR, ZERO_ATTRIBUTES);
			
			writeElementString(handler, EmptyUri, TAG_ERROR_STATUS, TAG_ERROR_STATUS, ZERO_ATTRIBUTES, String.valueOf(e.getStatus()));
			if (e.getMessage() != null)
				writeElementString(handler, EmptyUri, TAG_ERROR_MESSAGE, TAG_ERROR_MESSAGE, ZERO_ATTRIBUTES, e.getMessage());
			
			handler.endElement(EmptyUri, TAG_ERROR, TAG_ERROR);
			handler.endDocument();
		} catch (UnsupportedEncodingException ex) {
			throw new FormatException(ex);
		} catch (SAXException ex) {
			throw new FormatException(ex);
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
			ContentHandler handler = getXmlWriter(writer);
			
			handler.startDocument();
			handler.startElement(EmptyUri, TAG_ROOT, TAG_ROOT, ZERO_ATTRIBUTES);
			
			write(handler, feed);
			
			handler.endElement(EmptyUri, TAG_ROOT, TAG_ROOT);
			handler.endDocument();
		} catch (SAXException e) {
			throw new FormatException(e);
		}
	}
	
	public void format(OutputStream stream, List feeds) throws FormatException {
		try {
			ContentHandler handler = getXmlWriter(stream);
			
			handler.startDocument();
			
			if (feeds instanceof PagedList) {
				PagedList pl = (PagedList) feeds;
				handler.startPrefixMapping("opensearch", OpenSearchNamespace);
				
				AttributesImpl rootAttrs = new AttributesImpl();
				rootAttrs.addAttribute(EmptyUri, "opensearch", "xmlns:opensearch", null, OpenSearchNamespace);
				handler.startElement(EmptyUri, TAG_ROOT, TAG_ROOT, rootAttrs);
				
				writeElementString(handler, OpenSearchNamespace, "totalResults",
						"opensearch:totalResults", ZERO_ATTRIBUTES, String.valueOf(pl.getTotal()));
				writeElementString(handler, OpenSearchNamespace, "startIndex",
						"opensearch:startIndex", ZERO_ATTRIBUTES, String.valueOf(pl.getOffset()));
				writeElementString(handler, OpenSearchNamespace, "itemsPerPage",
						"opensearch:itemsPerPage", ZERO_ATTRIBUTES, String.valueOf(pl.getLimit()));
				
				handler.endPrefixMapping("opensearch");
			} else {
				handler.startElement(EmptyUri, TAG_ROOT, TAG_ROOT, ZERO_ATTRIBUTES);
			}
			
			Iterator it = feeds.iterator();
			while (it.hasNext()) {
				write(handler, (Feed) it.next());
			}
			
			handler.endElement(EmptyUri, TAG_ROOT, TAG_ROOT);
			handler.endDocument();
		} catch (UnsupportedEncodingException e) {
			throw new FormatException(e);
		} catch (SAXException e) {
			throw new FormatException(e);
		}
	}
	
	public void format(OutputStream stream, Entry value) throws FormatException {
		try {
			ContentHandler handler = getXmlWriter(stream);
			
			handler.startDocument();
			handler.startElement(EmptyUri, TAG_ROOT, TAG_ROOT, ZERO_ATTRIBUTES);
			
			handler.startElement(EmptyUri, TAG_FEED, TAG_FEED, ZERO_ATTRIBUTES);
			handler.startElement(EmptyUri, TAG_DATA, TAG_DATA, ZERO_ATTRIBUTES);
			write(handler, value);
			handler.endElement(EmptyUri, TAG_DATA, TAG_DATA);
			handler.endElement(EmptyUri, TAG_FEED, TAG_FEED);
			
			handler.endElement(EmptyUri, TAG_ROOT, TAG_ROOT);
			handler.endDocument();
		} catch (UnsupportedEncodingException e) {
			throw new FormatException(e);
		} catch (SAXException e) {
			throw new FormatException(e);
		}
	}
	
	public Collection parseFeeds(InputStream stream) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection parseFeeds(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Feed parseFeed(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Feed parseFeed(InputStream inputStream) throws FormatException {
		try {
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(inputStream);
			
			Feed feed = null;
			Element root = doc.getRootElement();
			Element feedElem = root.element(TAG_FEED);
			if (feedElem != null) {
				feed = parseFeedNode(feedElem);
			}
			
			return feed;
		} catch (DocumentException e) {
			throw new FormatException(e);
		} catch (NumberFormatException e) {
			throw new FormatException(e);
		}
	}
	
	public Entry parseValue(InputStream inputStream) throws FormatException {
		try {
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(inputStream);
			
			Feed feed = null;
			Element root = doc.getRootElement();
			Element feedElem = root.element(TAG_FEED);
			if (feedElem != null) {
				feed = parseFeedNode(feedElem);
			}
			
			Iterator it = feed.getEntries().iterator();
			
			return it.hasNext() ? (Entry) it.next() : null;
		} catch (DocumentException e) {
			throw new FormatException(e);
		} catch (NumberFormatException e) {
			throw new FormatException(e);
		}
	}
	
	private static Feed parseFeedNode(Element feedNode) throws FormatException, NumberFormatException {
		Feed feed = new Feed();
		for (Iterator it = feedNode.elementIterator(); it.hasNext(); ) {
			Element elem = (Element) it.next();
			String elemName = elem.getName();
			if (TAG_NAME.equals(elemName)) {
				feed.setName(elem.getText());
			} else if (TAG_TITLE.equals(elemName)) {
				feed.setTitle(elem.getText());
			} else if (TAG_DESCRIPTION.equals(elemName)) {
				feed.setDescription(elem.getText());
			} else if (TAG_WEBSITE.equals(elemName)) {
				feed.setWebsite(elem.getText());
			} else if (TAG_EMAIL.equals(elemName)) {
				feed.setEmail(elem.getText());
			} else if (TAG_CURRENT.equals(elemName)) {
				feed.setCurrentValue(parseValueObject(getChildNode(elem)));
			} else if (TAG_CREATED.equals(elemName)) {
				try {
					feed.setCreated(DateTimeUtils.fromDateTime8601(elem.getText()));
				} catch (ParseException e) {
					throw new FormatException("Malformed date string " + elem.getText(), e);
				}
			} else if (TAG_UPDATED.equals(elemName)) {
				try {
					feed.setUpdated(DateTimeUtils.fromDateTime8601(elem.getText()));
				} catch (ParseException e) {
					throw new FormatException("Malformed date string " + elem.getText(), e);
				}
			} else if (TAG_PRIVATE.equals(elemName)) {
				feed.setAccess(Feed.ACCESS_PRIVATE);
			} else if (TAG_KEYTYPE.equals(elemName)) {
				feed.setKeyType(Feed.parseKeyType(elem.getText()));
			} else if (TAG_KEYTYPE.equals(elemName)) {
				feed.setValueType(Feed.parseValueType(elem.getText()));
			} else if (TAG_STATUS.equals(elemName)) {
				feed.setStatus(Feed.parseStatus(elem.getText()));
			} else if (TAG_TAG.equals(elemName)) {
				feed.addTag(elem.getText());
			} else if (TAG_CHILDREN.equals(elemName)){
				for (Iterator itChild = elem.elementIterator(TAG_FEED); itChild.hasNext(); ) {
					Feed child = parseFeedNode((Element) itChild.next());
					if (child != null)
						feed.addChild(child);
				}
			} else if (TAG_DATA.equals(elemName)) {
				for (Iterator itEntry = elem.elementIterator(TAG_ENTRY); itEntry.hasNext(); ) {
					Entry entry = parseValueNode((Element) itEntry.next());
					if (entry != null)
						feed.addEntry(entry);
				}
			} else if (TAG_UNIT.equals(elemName)) {
				Unit unit = new Unit();
				unit.setLabel(elem.getText());
				unit.setSymbol(elem.attributeValue(TAG_UNIT_SYMBOL));
				unit.setType(elem.attributeValue(TAG_UNIT_TYPE));
				feed.setUnit(unit);
			} else if (TAG_LOCATION.equals(elemName)) {
				Location loc = new Location();
				loc.setDomain(Location.parseDomain(elem.attributeValue(TAG_DOMAIN)));
				loc.setExposure(Location.parseExposure(elem.attributeValue(TAG_EXPOSURE)));
				loc.setDisposition(Location.parseDisposition(elem.attributeValue(TAG_DISPOSITION)));
				for (Iterator itChild = elem.elementIterator(); itChild.hasNext(); ) {
					Element child = (Element) itChild.next();
					if (TAG_LOCATION_NAME.equals(child.getName()))
						loc.setName(child.getText());
					else if (TAG_ELEVATION.equals(child.getName()))
						loc.setElevation(Double.valueOf(child.getText()));
					else if (TAG_LONGITUDE.equals(child.getName()))
						loc.setLongitude(Double.valueOf(child.getText()));
					else if (TAG_LATITUDE.equals(child.getName()))
						loc.setLatitude(Double.valueOf(child.getText()));
					else if (TAG_SPEED.equals(child.getName()))
						loc.setSpeed(Double.valueOf(child.getText()));
					else if (TAG_BEARING.equals(child.getName()))
						loc.setBearing(Double.valueOf(child.getText()));
				}
				feed.setLocation(loc);
			}
		}
		return feed;
	}
	
	private static Entry parseValueNode(Element valueNode) throws FormatException, NumberFormatException {
		Object key = valueNode.attributeValue(TAG_AT);
		if (key == null)
			key = valueNode.attributeValue(TAG_KEY);
		else
			try {
				key = DateTimeUtils.fromDateTime8601((String) key);
			} catch (ParseException e) {
				throw new FormatException(e);
			}
		
		if (key == null)
			return null;
		else {
			Entry value = new Entry();
			value.setKey(key);
			
			Iterator it = valueNode.elementIterator();
			if (it.hasNext()) {
				value.setValue(parseValueObject((Element) it.next()));
			} else if (valueNode.isTextOnly() && valueNode.nodeCount() > 0) {
				value.setValue(parseValueObject(valueNode.node(0)));
			}
			
			return value;
		}
	}
	
	private static Object parseValueObject(Node node) throws FormatException, NumberFormatException {
		if (node == null)
			return null;

		Object val = null;
		
		String nodeName = node.getName();
		if (nodeName == null) {
			// text or CDATA node, take as number
			val = Double.valueOf(node.getText());
		} else if (TAG_VALUE.equals(nodeName) || TAG_ARRAY.equals(nodeName)) {
			val = parseValueObject(getChildNode(node));
		} else if (TAG_NUMBER.equals(nodeName)) {
			val = Double.valueOf(node.getText());
		} else if (TAG_INT.equals(nodeName)) {
			val = Integer.valueOf(node.getText());
		} else if (TAG_STRING.equals(nodeName)) {
			val = node.getText();
		} else if (TAG_BASE64.equals(nodeName)) {
			try {
				val = Base64.decode(node.getText());
			} catch (DecodingException e) {
				throw new FormatException(e);
			}
		} else if (TAG_STRUCT.equals(nodeName)) {
			Map map = new LinkedHashMap();
			Iterator itChild = ((Element) node).elementIterator(TAG_STRUCT_MEMBER);
			while (itChild.hasNext()) {
				Element child = (Element) itChild.next();
				String mName = null;
				Object mValue = null;
				Iterator itSub = child.elementIterator();
				while (itSub.hasNext()) {
					Element sub = (Element) itSub.next();
					if (TAG_STRUCT_NAME.equals(sub.getName()))
						mName = sub.getText();
					else if (TAG_VALUE.equals(sub.getName()))
						mValue = parseValueObject(sub);
				}
				if (mName != null)
					map.put(mName, mValue);
			}
			val = map;
		} else if (TAG_ARRAY_DATA.equals(nodeName)) {
			List list = new ArrayList();
			Iterator itChild = ((Element) node).elementIterator();
			while (itChild.hasNext()) {
				Object tmp = parseValueObject((Element) itChild.next());
				if (tmp != null)
					list.add(tmp);
			}
			val = list;
		}
		
		return val;
	}
	
	private static Node getChildNode(Node node) {
		if (node.hasContent())
			return ((Branch) node).node(0);
		else
			return null;
	}

	private static void write(ContentHandler handler, Feed feed) throws SAXException {
		handler.startElement(EmptyUri, TAG_FEED, TAG_FEED, ZERO_ATTRIBUTES);

		writeElementString(handler, TAG_NAME, feed.getName());
		writeElementString(handler, TAG_TITLE, feed.getTitle());
		writeElementString(handler, TAG_DESCRIPTION, feed.getDescription());
		writeElementString(handler, TAG_WEBSITE, feed.getWebsite());
		writeElementString(handler, TAG_EMAIL, feed.getEmail());
		writeElementString(handler, TAG_STATUS, feed.getStatusString());
		if (feed.getCreated() != null)
			writeElementString(handler, TAG_CREATED, DateTimeUtils.toDateTime8601(feed.getCreated()));
		if (feed.getUpdated() != null)
			writeElementString(handler, TAG_UPDATED, DateTimeUtils.toDateTime8601(feed.getUpdated()));
		
		if (feed.getKeyType() != Feed.KEY_NONE)
			writeElementString(handler, TAG_KEYTYPE, feed.getKeyTypeString());
		if (feed.getValueType() != Feed.KEY_NONE)
			writeElementString(handler, TAG_VALUETYPE, feed.getValueTypeString());
		
		if (feed.getAccess() == Feed.ACCESS_PRIVATE)
			writeElementString(handler, TAG_PRIVATE, "true");
		
		if (feed.getTags() != null) {
			for (Iterator itTag = feed.getTags().iterator(); itTag.hasNext(); ) {
				writeElementString(handler, TAG_TAG, (String) itTag.next());
			}
		}
		
		if (feed.getLocation() != null) {
			Location loc = feed.getLocation();
			AttributesImpl locAttrs = new AttributesImpl();
			locAttrs.addAttribute(EmptyUri, TAG_DOMAIN, TAG_DOMAIN, null, loc.getDomainString());
			locAttrs.addAttribute(EmptyUri, TAG_DISPOSITION, TAG_DISPOSITION, null, loc.getDispositionString());
			locAttrs.addAttribute(EmptyUri, TAG_EXPOSURE, TAG_EXPOSURE, null, loc.getExposureString());
			handler.startElement(EmptyUri, TAG_LOCATION, TAG_LOCATION, locAttrs);
			writeElementString(handler, TAG_LOCATION_NAME, loc.getName());
			writeElementString(handler, TAG_LATITUDE, loc.getLatitude());
			writeElementString(handler, TAG_LONGITUDE, loc.getLongitude());
			writeElementString(handler, TAG_ELEVATION, loc.getElevation());
			writeElementString(handler, TAG_SPEED, loc.getSpeed());
			writeElementString(handler, TAG_BEARING, loc.getBearing());
			handler.endElement(EmptyUri, TAG_LOCATION, TAG_LOCATION);
		}
		
		if (feed.getUnit() != null) {
			AttributesImpl unitAttrs = new AttributesImpl();
			unitAttrs.addAttribute(EmptyUri, TAG_UNIT_TYPE, TAG_UNIT_TYPE, null, feed.getUnit().getType());
			unitAttrs.addAttribute(EmptyUri, TAG_UNIT_SYMBOL, TAG_UNIT_SYMBOL, null, feed.getUnit().getSymbol());
			writeElementString(handler, EmptyUri, TAG_UNIT, TAG_UNIT, unitAttrs, feed.getUnit().getLabel());
		}
		
		if (feed.getCurrentValue() != null) {
			handler.startElement(EmptyUri, TAG_CURRENT, TAG_CURRENT, ZERO_ATTRIBUTES);
			writeValue(handler, feed.getCurrentValue());
			handler.endElement(EmptyUri, TAG_CURRENT, TAG_CURRENT);
		}
		
		if (feed.getEntries() != null) {
			Iterator itValue = feed.getEntries().iterator();
			if (itValue.hasNext()) {
				handler.startElement(EmptyUri, TAG_DATA, TAG_DATA, ZERO_ATTRIBUTES);
				do {
					write(handler, (Entry) itValue.next());
				} while (itValue.hasNext());
				handler.endElement(EmptyUri, TAG_DATA, TAG_DATA);
			}
		}
		
		if (feed.getChildren() != null) {
			Iterator itChild = feed.getChildren().iterator();
			if (itChild.hasNext()) {
				handler.startElement(EmptyUri, TAG_CHILDREN, TAG_CHILDREN, ZERO_ATTRIBUTES);
				do {
					write(handler, (Feed) itChild.next());
				} while (itChild.hasNext());
				handler.endElement(EmptyUri, TAG_CHILDREN, TAG_CHILDREN);
			}
		}
		
		handler.endElement(EmptyUri, TAG_FEED, TAG_FEED);
	}

	private static void write(ContentHandler handler, Entry value) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		Object key = value.getKey();
		if (key instanceof Date)
			attrs.addAttribute(EmptyUri, TAG_AT, TAG_AT, null, DateTimeUtils.toDateTime8601((Date) key));
		else
			attrs.addAttribute(EmptyUri, TAG_KEY, TAG_KEY, null, key.toString());
		handler.startElement(EmptyUri, TAG_ENTRY, TAG_ENTRY, attrs);
		if (value.getValue() != null)
			writeValue(handler, value.getValue());
		handler.endElement(EmptyUri, TAG_ENTRY, TAG_ENTRY);
	}

	private static void writeValue(ContentHandler handler, Object obj) throws SAXException {
		handler.startElement(EmptyUri, TAG_VALUE, TAG_VALUE, ZERO_ATTRIBUTES);
		if (obj instanceof Integer) {
			writeElementString(handler, TAG_INT, obj.toString());
		} else if (obj instanceof Number) {
			writeElementString(handler, TAG_NUMBER, obj.toString());
		} else if (obj instanceof String) {
			writeElementString(handler, TAG_STRING, obj.toString());
		} else if (obj instanceof Boolean) {
			writeElementString(handler, TAG_BOOLEAN, obj.toString());
		} else if (obj instanceof byte[]) {
			writeElementString(handler, TAG_BASE64, Base64.encode((byte[]) obj));
		} else if (obj instanceof Object[]) {
			handler.startElement(EmptyUri, TAG_ARRAY, TAG_ARRAY, ZERO_ATTRIBUTES);
			handler.startElement(EmptyUri, TAG_ARRAY_DATA, TAG_ARRAY_DATA, ZERO_ATTRIBUTES);
			Object[] data = (Object[]) obj;
			for (int i = 0;  i < data.length;  i++) {
				writeValue(handler, data[i]);
			}
			handler.endElement(EmptyUri, TAG_ARRAY_DATA, TAG_ARRAY_DATA);
			handler.endElement(EmptyUri, TAG_ARRAY, TAG_ARRAY);
		} else if (obj instanceof Collection) {
			handler.startElement(EmptyUri, TAG_ARRAY, TAG_ARRAY, ZERO_ATTRIBUTES);
			handler.startElement(EmptyUri, TAG_ARRAY_DATA, TAG_ARRAY_DATA, ZERO_ATTRIBUTES);
			Collection data = (Collection) obj;
			Iterator it = data.iterator();
			while (it.hasNext()) {
				writeValue(handler, it.next());
			}
			handler.endElement(EmptyUri, TAG_ARRAY_DATA, TAG_ARRAY_DATA);
			handler.endElement(EmptyUri, TAG_ARRAY, TAG_ARRAY);
		} else if (obj instanceof Map) {
			handler.startElement(EmptyUri, TAG_STRUCT, TAG_STRUCT, ZERO_ATTRIBUTES);
			Map map = (Map) obj;
			for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry entry = (Map.Entry) iter.next();
				handler.startElement(EmptyUri, TAG_STRUCT_MEMBER, TAG_STRUCT_MEMBER, ZERO_ATTRIBUTES);
				writeElementString(handler, TAG_STRUCT_NAME, entry.getKey().toString());
				writeValue(handler, entry.getValue());
				handler.endElement(EmptyUri, TAG_STRUCT_MEMBER, TAG_STRUCT_MEMBER);
			}
			handler.endElement(EmptyUri, TAG_STRUCT, TAG_STRUCT);
		} else {
			throw new SAXException("Unsupported Java type: " + obj.getClass().getName());
		}
		handler.endElement(EmptyUri, TAG_VALUE, TAG_VALUE);
	}
	
	protected XMLWriter newXmlWriter() {
		return new XMLWriterImpl();
	}
	
	private static void writeElementString(ContentHandler handler, String uri,
			String localName, String qName, Attributes atts, String value)
			throws SAXException {
		if (value != null)
			writeElementString(handler, uri, localName, qName, atts, value.toCharArray());
	}
	
	private static void writeElementString(ContentHandler handler, String uri,
			String localName, String qName, Attributes atts, char[] value)
			throws SAXException {
		handler.startElement(uri, localName, qName, atts);
		handler.characters(value, 0, value.length);
		handler.endElement(uri, localName, qName);
	}
	
	private static void writeElementString(ContentHandler handler, String localName, String value) throws SAXException {
		if (value != null)
			writeElementString(handler, localName, value.toCharArray());
	}
	
	private static void writeElementString(ContentHandler handler, String localName, char[] value) throws SAXException {
		handler.startElement(EmptyUri, localName, localName, ZERO_ATTRIBUTES);
		handler.characters(value, 0, value.length);
		handler.endElement(EmptyUri, localName, localName);
	}
	
	private static void writeElementString(ContentHandler handler, String localName, Object value) throws SAXException {
		if (value != null)
			writeElementString(handler, localName, value.toString().toCharArray());
	}
	
	private ContentHandler getXmlWriter(OutputStream stream)
			throws UnsupportedEncodingException {
		return getXmlWriter(new BufferedWriter(new OutputStreamWriter(stream, "UTF-8")));
	}
	
	private Writer getWriter(OutputStream stream)
			throws UnsupportedEncodingException {
		return new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
	}
	
	private ContentHandler getXmlWriter(Writer writer) {
		XMLWriter xw = newXmlWriter();
		xw.setDeclarating(true);
		xw.setEncoding("UTF-8");
		xw.setIndenting(false);
		xw.setFlushing(true);
		xw.setWriter(writer);
		return xw;
	}
	
	static final Attributes ZERO_ATTRIBUTES = new AttributesImpl();
	static final String OpenSearchNamespace = "http://a9.com/-/spec/opensearch/1.1/";
	static final String EmptyUri = "";
	static final String TAG_ROOT = "xfml";
	static final String TAG_FEED = "feed";
	static final String TAG_NAME = "name";
	static final String TAG_TITLE = "title";
	static final String TAG_DESCRIPTION = "description";
	static final String TAG_WEBSITE = "website";
	static final String TAG_EMAIL = "email";
	static final String TAG_CREATED = "created";
	static final String TAG_UPDATED = "updated";
	static final String TAG_CURRENT = "current";
	static final String TAG_KEYTYPE = "keyType";
	static final String TAG_VALUETYPE = "valueType";
	static final String TAG_STATUS = "status";
	static final String TAG_TAG = "tag";
    static final String TAG_LOCATION = "location";
    static final String TAG_LOCATION_NAME = "name";
    static final String TAG_DOMAIN = "domain";
    static final String TAG_EXPOSURE = "exposure";
    static final String TAG_DISPOSITION = "disposition";
    static final String TAG_ELEVATION = "ele";
    static final String TAG_LONGITUDE = "lng";
    static final String TAG_LATITUDE = "lat";
    static final String TAG_SPEED = "speed";
    static final String TAG_BEARING = "bearing";
	static final String TAG_UNIT = "unit";
	static final String TAG_UNIT_TYPE = "type";
	static final String TAG_UNIT_SYMBOL = "symbol";
    static final String TAG_PRIVATE = "private";
	static final String TAG_CHILDREN = "children";
	static final String TAG_DATA = "data";
	static final String TAG_ENTRY = "entry";
	static final String TAG_KEY = "key";
	static final String TAG_AT = "at";
	static final String TAG_VALUE = "value";
	static final String TAG_NUMBER = "number";
	static final String TAG_INT = "integer";
	static final String TAG_STRING = "string";
	static final String TAG_BOOLEAN = "boolean";
	static final String TAG_BASE64 = "base64";
	static final String TAG_ARRAY = "array";
	static final String TAG_ARRAY_DATA = "data";
	static final String TAG_STRUCT = "struct";
	static final String TAG_STRUCT_MEMBER = "member";
	static final String TAG_STRUCT_NAME = "name";
	static final String TAG_ERROR = "error";
	static final String TAG_ERROR_STATUS = "status";
	static final String TAG_ERROR_MESSAGE = "message";
}
