/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.entity;

/**
 * Location of a feed.
 * 
 * @author smeshlink
 * 
 */
public class Location {
	private static final Integer ZERO = new Integer(0);
	private static final Integer ONE = new Integer(1);
	
	public static final Integer DISPOSITION_FIXED = ZERO;
	public static final Integer DISPOSITION_MOBILE = ONE;

	public static final Integer EXPOSURE_INDOOR = ZERO;
	public static final Integer EXPOSURE_OUTDOOR = ONE;

	public static final Integer DOMAIN_PHYSICAL = ZERO;
	public static final Integer DOMAIN_VIRTUAL = ONE;
	
	private String name;
	private Integer disposition;
	private Integer exposure;
	private Integer domain;
	private Double latitude;
	private Double longitude;
	private Double elevation;
	private Double speed;
	private Double bearing;
	
	public static String getDispositionString(Integer disposition) {
		return disposition == DISPOSITION_MOBILE ? "mobile" : "fixed";
	}
	
	public static Integer parseDisposition(String disposition) {
		return "mobile".equalsIgnoreCase(disposition) ? DISPOSITION_MOBILE : DISPOSITION_FIXED;
	}

	public static String getExposureString(Integer exposure) {
		return exposure == EXPOSURE_OUTDOOR ? "outdoor" : "indoor";
	}
	
	public static Integer parseExposure(String exposure) {
		return "outdoor".equalsIgnoreCase(exposure) ? EXPOSURE_OUTDOOR : EXPOSURE_INDOOR;
	}

	public static String getDomainString(Integer domain) {
		return domain == DOMAIN_VIRTUAL ? "virtual" : "physical";
	}
	
	public static Integer parseDomain(String domain) {
		return "virtual".equalsIgnoreCase(domain) ? DOMAIN_VIRTUAL : DOMAIN_PHYSICAL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDisposition() {
		return disposition;
	}
	
	public String getDispositionString() {
		return disposition == null ? null : getDispositionString(disposition);
	}

	public void setDisposition(Integer disposition) {
		this.disposition = disposition;
	}

	public Integer getExposure() {
		return exposure;
	}
	
	public String getExposureString() {
		return exposure == null ? null : getExposureString(exposure);
	}

	public void setExposure(Integer exposure) {
		this.exposure = exposure;
	}

	public Integer getDomain() {
		return domain;
	}
	
	public String getDomainString() {
		return domain == null ? null : getDomainString(domain);
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getBearing() {
		return bearing;
	}

	public void setBearing(Double bearing) {
		this.bearing = bearing;
	}
}
