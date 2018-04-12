package ua.org.PlainBytes;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class main {
	public static void main(String[] args) throws Throwable {
		String someJsonString = "\n" +
				"  {\n" +
				"    id: \"5973782bdb9a930533b05cb2\",\n" +
				"    isActive: true,\n" +
				"    \"balance\": \"$1,446.35\",\n" +
				"    age: 32,\n" +
				"    \"eyeColor\": \"green\",\n" +
				"    \"name\": \"\\uD834\\uDD1E Logan\\tKeller\",\n" +
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

		//Parse String with json
		//This method does't throw eny exception just return null
		LinkedHashMap<String, Object> parsedData = JsonPrimitive.fromJson(someJsonString);

		//Convert Object to json String in human-readable format
		String json = JsonPrimitive.toJson(parsedData,true);

		//=================================================================================================
		//If you want more control
		LinkedHashMap<String, Object> dataMap = null;
		ArrayList<Object> dataArray = null;

		JsonReader jsonReader = new JsonReader();
		JsonWriter jsonWriter = new JsonWriter(true);


		try {
			//Parse String with json
			//This method return true if root object is LinkedHashMap and false if it is ArrayList
			if (jsonReader.parse(someJsonString)) {
				//get result
				dataMap = jsonReader.getResultMap();
			} else {
				//get result
				dataArray = jsonReader.getResultArray();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			//Write object to json file
			jsonWriter.writeToFile(new File("someFile.json"), dataMap);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Also you can read and write to any Stream
		//jsonWriter.writeToStream(OutputStream, Object);
		//jsonReader.parseJson(InputStream)


		//===================================================================================================
		//If you want work with your custom class objects
		//You have to implement interface FromJson and/or ToJson which will works with LinkedHashMap<String, Object>
		dataMap.put("testObject", new Bicycle(1,2,3));


		String str = jsonWriter.writeToString(dataMap);
		if (jsonReader.parse(str)) {
			dataMap = jsonReader.getResultMap();
		}


		//===================================================================================================
		//Some performance test with 190Mb file
		long startTime = System.nanoTime();
		if (jsonReader.parse(new File("TestData/citylots.json"))) {
			dataMap = jsonReader.getResultMap();
		} else {
			dataArray = jsonReader.getResultArray();
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("FromJson time is: " + duration / 1000000 + "ms.");

		startTime = System.nanoTime();
		jsonWriter.writeToFile(new File("TestData/citylots_remake.json"), dataArray);
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		System.out.println("ToJson time is: " + duration / 1000000 + "ms.");
	}
}
