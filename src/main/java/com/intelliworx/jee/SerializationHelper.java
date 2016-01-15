package com.intelliworx.jee;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

import java.util.Arrays;
import java.util.Map;

public class SerializationHelper {

	public static String convertToString(final Map<String, String[]> map, final String delimiter) {
		return map.entrySet().stream().map(entry -> entry.getKey() + " = " + Arrays.toString(entry.getValue())).collect(joining(delimiter));
		// Joining collector will use StringBuilder under the hood.
	}

	public static String getChecksum(final String string) {
		if(string == null || string.equals("")){
			throw new IllegalArgumentException("Value must not be null");
		}
		return md5Hex(string);
	}
}
