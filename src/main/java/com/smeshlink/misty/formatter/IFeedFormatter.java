/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.formatter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import com.smeshlink.misty.entity.Entry;
import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.service.ServiceException;

/**
 * @author smeshlink
 *
 */
public interface IFeedFormatter {
	void format(OutputStream stream, List feeds) throws FormatException;
	void format(OutputStream stream, Feed feed) throws FormatException;
	void format(Writer stream, Feed feed) throws FormatException;
	void format(OutputStream stream, Entry value) throws FormatException;
	void format(OutputStream stream, ServiceException e) throws FormatException;
	Collection parseFeeds(InputStream stream);
	Collection parseFeeds(Object obj);
	Feed parseFeed(InputStream inputStream) throws FormatException;
	Feed parseFeed(Object obj);
	Entry parseValue(InputStream inputStream) throws FormatException;
}
