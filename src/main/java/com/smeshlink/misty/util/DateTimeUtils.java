/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author smeshlink
 *
 */
public class DateTimeUtils {
	
	public static Timestamp toTimestamp(Date date) {
		return Timestamp.class.isInstance(date) ? (Timestamp) date
				: new Timestamp(((Date) date).getTime());
	}
	
	public static double toUnixTimestamp(Date date) {
		return date.getTime() / 1000D;
	}

	public static Date fromUnixTimestamp(double timestamp) {
		return new Date((long) (timestamp * 1000));
	}

	public static Date fromDateTime8601(String input)
			throws java.text.ParseException {
		// NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
		// things a bit. Before we go on we have to repair this.
		SimpleDateFormat df = null;
		int dot = input.lastIndexOf('.');
		boolean withZone = true;
		boolean withMs = dot > 0;

		// this is zero time so we need to add that TZ indicator for
		if (input.endsWith("Z")) {
			if (withMs && (input.length() - dot) > 4)
				// trims milliseconds to 3 digits
				input = input.substring(0, dot + 4);
			else
				input = input.substring(0, input.length() - 1);
			input += "GMT-00:00";
		} else {
			int inset = 6;
			char c = input.charAt(input.length() - inset);
			if (c == '+' || c == '-') {
				String s0 = input.substring(0, input.length() - inset);
				String s1 = input.substring(input.length() - inset, input.length());

				if (withMs && (s0.length() - dot) > 4)
					// trims milliseconds to 3 digits
					s0 = s0.substring(0, dot + 4);

				input = s0 + "GMT" + s1;
			} else {
				if (withMs && (input.length() - dot) > 4)
					// trims milliseconds to 3 digits
					input = input.substring(0, dot + 4);
				withZone = false;
			}
		}

		if (withZone) {
			if (withMs)
				df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
			else
				df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		} else {
			if (withMs)
				df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			else
				df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}

		return df.parse(input);
	}

	public static String toDateTime8601(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");

		TimeZone tz = TimeZone.getTimeZone("UTC");
		df.setTimeZone(tz);

		String result = df.format(date);
		result = result.replaceAll("UTC", "Z");

		return result;
	}
}
