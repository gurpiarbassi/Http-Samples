package com.intelliworx.jee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.intelliworx.jee.SerializationHelper;

public class SerializationHelperTest {

	@Test
	public void canConvertMapToString(){
		final Map<String, String[]> map = new HashMap<>();
		map.put("a", new String[]{"1", "2"});
		map.put("b", new String[]{"3", "4"});

		final String actual = SerializationHelper.convertToString(map, ":");
		final String expected = "a = [1, 2]:b = [3, 4]";

		assertEquals(expected, actual);
	}

	@Test
	public void canConvertMapToStringWithNulls(){
		final Map<String, String[]> map = new HashMap<>();
		map.put("a", new String[]{"1", null});
		map.put("b", new String[]{"3", "4"});

		final String actual = SerializationHelper.convertToString(map, ":");
		final String expected = "a = [1, null]:b = [3, 4]";

		assertEquals(expected, actual);
	}


	@Test
	public void testGetChecksum() {
		final String str1 = "abc";
		final String str1CheckSum = SerializationHelper.getChecksum(str1);

		final String str2 = "def";
		final String str2CheckSum = SerializationHelper.getChecksum(str2);

		final String str3 = "abc";
		final String str3CheckSum = SerializationHelper.getChecksum(str3);

		assertNotEquals(str1CheckSum, str2CheckSum);
		assertEquals(str1CheckSum, str3CheckSum);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetChecksumForNull() {
		final String str = null;
		SerializationHelper.getChecksum(str);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetChecksumForEmpty() {
		final String str = "";
		SerializationHelper.getChecksum(str);
	}

}
