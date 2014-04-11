/**
 * Copyright (c) 2011 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @author Longshine
 * 
 */
public class Base64 {
	private static final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
	public static final String version = "1.0.0";

	public static String encode(byte[] data) {
		// Initialise output
		String output = "";

		// Create data and output buffers
		int[] dataBuffer;
		int[] outputBuffer = new int[4];

		// while there are still bytes to be processed
		for (int pos = 0; pos < data.length;) {
			// Create new data buffer and populate next 3 bytes from data
			dataBuffer = new int[3];
			int i = 0;
			for (; i < 3 && pos < data.length; i++) {
				// dataBuffer[i] = data.readUnsignedByte();
				dataBuffer[i] = data[pos++] & 0xFF;
			}

			// Convert to data buffer Base64 character positions and
			// store in output buffer
			outputBuffer[0] = (dataBuffer[0] & 0xfc) >> 2;
			outputBuffer[1] = ((dataBuffer[0] & 0x03) << 4) | ((dataBuffer[1]) >> 4);
			outputBuffer[2] = ((dataBuffer[1] & 0x0f) << 2) | ((dataBuffer[2]) >> 6);
			outputBuffer[3] = dataBuffer[2] & 0x3f;

			// If data buffer was short (i.e not 3 characters) then set
			// end character indexes in data buffer to index of '=' symbol.
			// This is necessary because Base64 data is always a multiple of
			// 4 bytes and is basses with '=' symbols.
			for (int j = i; j < 3; j++) {
				outputBuffer[j + 1] = 64;
			}

			// Loop through output buffer and add Base64 characters to
			// encoded data string for each character.
			for (int k = 0; k < outputBuffer.length; k++) {
				output += BASE64_CHARS.charAt(outputBuffer[k]);
			}
		}

		// Return encoded data
		return output;
	}
	
	public static String encodeString(String s) {
		byte[] bytes = null;
		try {
			bytes = s.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
		return bytes == null ? null : encode(bytes);
	}

	public static String decodeString(String data) {
		byte[] bytes = decode(data);

		try {
			return new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static byte[] decode(String data) {
		// Initialise output ByteArray for decoded data
		ArrayList byteArray = new ArrayList();

		// Create data and output buffers
		int[] dataBuffer = new int[4];
		int[] outputBuffer = new int[3];

		// While there are data bytes left to be processed
		for (int i = 0; i < data.length(); i += 4) {
			// Populate data buffer with position of Base64 characters for next
			// 4 bytes from encoded data
			for (int j = 0; j < 4 && i + j < data.length(); j++) {
				dataBuffer[j] = BASE64_CHARS.indexOf(data.charAt(i + j));
			}

			// Decode data buffer back into bytes
			outputBuffer[0] = (dataBuffer[0] << 2) + ((dataBuffer[1] & 0x30) >> 4);
			outputBuffer[1] = ((dataBuffer[1] & 0x0f) << 4) + ((dataBuffer[2] & 0x3c) >> 2);
			outputBuffer[2] = ((dataBuffer[2] & 0x03) << 6) + dataBuffer[3];

			// Add all non-padded bytes in output buffer to decoded data
			for (int k = 0; k < outputBuffer.length; k++) {
				if (dataBuffer[k + 1] == 64)
					break;
				byteArray.add(Byte.valueOf(Integer.valueOf(outputBuffer[k]).byteValue()));
			}
		}

		Byte[] tmpBytes = (Byte[]) byteArray.toArray(new Byte[0]);
		byte[] bytes = new byte[tmpBytes.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = tmpBytes[i].byteValue();
		}

		// Return decoded data
		return bytes;
	}
}
