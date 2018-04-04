package ua.org.PlainBytes;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class main {
	public static void main(String[] args) throws Throwable {
		String testJson = "\n" +
				"  {\n" +
				"    \"_id\": \"5973782bdb9a930533b05cb2\",\n" +
				"    \"isActive\": true,\n" +
				"    \"balance\": \"$1,446.35\",\n" +
				"    \"age\": 32,\n" +
				"    \"eyeColor\": \"green\",\n" +
				"    \"name\": \"\\uD834\\uDD1ELogan Keller\",\n" +
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

		JsonReader jsonReader = new JsonReader();
		LinkedHashMap<String, Object> dataMap = null;
		ArrayList<Object> dataArray = null;

		if (jsonReader.parse(testJson)) {
			dataMap = jsonReader.getResultMap();
		} else {
			dataArray = jsonReader.getResultArray();
		}

		JsonWriter jsonWriter = new JsonWriter();
		String str = jsonWriter.writeToString(dataMap);

		//=======================================================
		long startTime = System.nanoTime();

		if (jsonReader.parse(new File("TestData/citylots.json"))) {
			dataMap = jsonReader.getResultMap();
		} else {
			dataArray = jsonReader.getResultArray();
		}

		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Executed time is: " + duration / 1000000 + "ms.");
		dataMap.get("_id");
		dataArray.get(0);
	}
}
