package ua.org.PlainBytes;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class main {
	public static void main(String[] args) throws Throwable {
		String testJson = "\n" +
				"  {\n" +
				"    \"_id\": \"5973782bdb9a930533b05cb2\",\n" +
				"    \"isActive\": true,\n" +
				"    \"balance\": \"$1,446.35\",\n" +
				"    \"age\": 32,\n" +
				"    \"eyeColor\": \"green\",\n" +
				"    \"name\": \"Logan Keller\",\n" +
				"    \"gender\": \"male\",\n" +
				"    \"company\": \"ARTIQ\",\n" +
				"    \"email\": \"logankeller@artiq.com\",\n" +
				"    \"phone\": \"+1 (952) 533-2258\",\n" +
				"    \"friends\": [\n" +
				"      {\n" +
				"        \"id\": 0,\n" +
				"        \"name\": \"Colon Salazar\"\n" +
				"      },\n" +
				"      {\n" +
				"        \"id\": 1,\n" +
				"        \"name\": \"French Mcneil\"\n" +
				"      },\n" +
				"      {\n" +
				"        \"id\": 2,\n" +
				"        \"name\": \"Carol Martin\"\n" +
				"      }\n" +
				"    ],\n" +
				"    \"favoriteFruit\": \"banana\"\n" +
				"  }\n" +
				"\n";

		JSONPrimitive jsonPrimitive = new JSONPrimitive();
		LinkedHashMap<String, Object> dataMap = null;
		ArrayList<Object> dataArray = null;

		if (jsonPrimitive.parse(testJson)) {
			dataMap = jsonPrimitive.getResultMap();
		} else {
			dataArray = jsonPrimitive.getResultArray();
		}

		long startTime = System.nanoTime();

		if (jsonPrimitive.parse(new File("TestData/citylots.json"))) {
			dataMap = jsonPrimitive.getResultMap();
		} else {
			dataArray = jsonPrimitive.getResultArray();
		}

		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Executed time is: " + duration/1000000 +"ms.");
		dataMap.get("_id");
		dataArray.get(0);
	}
}
