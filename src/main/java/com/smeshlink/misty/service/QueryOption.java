/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.smeshlink.misty.util.DateTimeUtils;

/**
 * @author smeshlink
 *
 */
public class QueryOption {
	public static final int CONTENT_SUMMARY	=	0x0;
	public static final int CONTENT_FULL	=	0x1;
	
	public static final int STATUS_ALL		=	0x0;
	public static final int STATUS_LIVE		=	0x1;
	public static final int STATUS_FROZEN	=	0x2;

	public static final int ORDER_NONE		=	0x0;
	public static final int ORDER_CREATED	=	0x1;
	public static final int ORDER_UPDATED	=	0x2;
	public static final int ORDER_ASC		=	0x4;
	public static final int ORDER_DESC		=	0x8;

	public static final int VIEW_GROUP	=	0x0;
	public static final int VIEW_FLAT		=	0x1;

	public static final int SAMPLE_NONE		=	0x0;
	public static final int SAMPLE_AVG		=	0x1;
	public static final int SAMPLE_MAX		=	0x2;
	public static final int SAMPLE_MIN		=	0x3;
	public static final int SAMPLE_RANDOM	=	0x4;
	
	public static final QueryOption DEFAULT = new QueryOption();
	
	private int content;
	private int status;
	private int order;
	private int view;
	private int sample;
	private int offset = 0;
	private int limit = -1;
	private Date startTime;
	private Date endTime;
	
	public QueryOption() {
	}
	
	public QueryOption(IServiceRequest request) {
		setContent(parseContent(request.getParameter("content")));
		setStatus(parseStatus(request.getParameter("status")));
		setOrder(parseOrder(request.getParameter("order")));
		setView(parseView(request.getParameter("view")));
		setSample(parseSample(request.getParameter("sample")));
		
		Integer limit = parseInt(request.getParameter("limit"));
		Integer offset = parseInt(request.getParameter("offset"));
		Integer p = parseInt(request.getParameter("p"));
		Integer n = parseInt(request.getParameter("n"));
		
		if (limit != null || offset != null) {
			if (limit != null)
				setLimit(limit.intValue());
			if (offset != null)
				setOffset(offset.intValue());
		} else if (p != null || n != null) {
			if (n != null)
				setLimit(n.intValue());
			if (p != null)
				setOffset((p.intValue() - 1) * getLimit());
		}
		
		Date startTime = QueryOption.parseDate(request.getParameter("start"));
		Date endTime = QueryOption.parseDate(request.getParameter("end"));
		Integer duration = QueryOption.parseInt(request.getParameter("duration"));
		
		if (startTime != null || endTime != null || duration != null) {
			// historical query
			if (duration == null)
				duration = Integer.valueOf(86400);
			if (startTime == null && endTime == null)
				endTime = new Date();
			if (startTime == null) {
				Calendar start = Calendar.getInstance();
				start.setTime(endTime);
				start.add(Calendar.SECOND, 0 - duration.intValue());
				startTime = start.getTime();
			} else if (endTime == null) {
				Calendar end = Calendar.getInstance();
				end.setTime(startTime);
				end.add(Calendar.SECOND, duration.intValue());
				endTime = end.getTime();
			}
			
			setStartTime(startTime);
			setEndTime(endTime);
		}
	}
	
	public static Integer parseInt(String value) {
		Integer result = null;
		if (value != null)
			try {
				result = Integer.valueOf(value);
			} catch (NumberFormatException e) { }
		return result;
	}
	
	public static Date parseDate(String value) {
		Date result = null;
		if (value != null)
			try {
				result = DateTimeUtils.fromDateTime8601(value);
			} catch (ParseException e) { }
		return result;
	}
	
	public static int parseContent(String value) {
		if (value == null)
			return CONTENT_FULL;
		else if ("summary".equalsIgnoreCase(value))
			return CONTENT_SUMMARY;
		else
			return CONTENT_FULL;
	}
	
	public static int parseStatus(String value) {
		if (value == null)
			return STATUS_ALL;
		else if ("live".equalsIgnoreCase(value))
			return STATUS_LIVE;
		else if ("frozen".equalsIgnoreCase(value))
			return STATUS_FROZEN;
		else
			return STATUS_ALL;
	}
	
	public static int parseView(String value) {
		if (value == null)
			return VIEW_GROUP;
		else if ("flat".equalsIgnoreCase(value))
			return VIEW_FLAT;
		else
			return VIEW_GROUP;
	}
	
	public static int parseSample(String value) {
		if (value == null)
			return SAMPLE_NONE;
		else if ("avg".equalsIgnoreCase(value))
			return SAMPLE_AVG;
		else if ("max".equalsIgnoreCase(value))
			return SAMPLE_MAX;
		else if ("min".equalsIgnoreCase(value))
			return SAMPLE_MIN;
		else if ("random".equalsIgnoreCase(value))
			return SAMPLE_RANDOM;
		else
			return SAMPLE_NONE;
	}
	
	public static int parseOrder(String value) {
		int order = ORDER_NONE;
		if (value != null) {
			String[] tmp = value.split(",");
			for (int i = 0; i < tmp.length; i++) {
				if ("created".equalsIgnoreCase(tmp[i]))
					order |= ORDER_CREATED;
				else if ("updated".equalsIgnoreCase(tmp[i]))
					order |= ORDER_UPDATED;
				else if ("asc".equalsIgnoreCase(tmp[i]))
					order |= ORDER_ASC;
				else if ("desc".equalsIgnoreCase(tmp[i]))
					order |= ORDER_DESC;
			}
		}
		return order;
	}
	
	public static int parseOrder(String[] values) {
		int order = ORDER_NONE;
		for (int i = 0; i < values.length; i++) {
			order |= parseOrder(values[i]);
		}
		return order;
	}

	public void setContent(int content) {
		this.content = content;
	}

	public int getContent() {
		return content;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public void setView(int view) {
		this.view = view;
	}

	public int getView() {
		return view;
	}

	public void setSample(int sample) {
		this.sample = sample;
	}

	public int getSample() {
		return sample;
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

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}
}
