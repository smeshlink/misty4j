/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.entity;

/**
 * Unit of a feed.
 * 
 * @author smeshlink
 * 
 */
public class Unit {
	private String label;
	private String symbol;
	private String type;

	/**
	 * Get the unit, e.g. Celsius.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the unit, e.g. Celsius.
	 * @param label the label of the unit
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the symbol of the unit, e.g. C.
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the symbol of the unit, e.g. C.
	 * @param symbol the symbol of the unit
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Gets the type of the unit.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the unit.
	 */
	public void setType(String type) {
		this.type = type;
	}
}
