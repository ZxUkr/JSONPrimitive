package ua.org.PlainBytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class JsonPrimitive {
	public static String toJson(Object object) {
		return toJson(object, false);
	}

	public static String toJson(Object object, boolean formattedOutput) {
		JsonWriter jw = new JsonWriter(formattedOutput);
		ByteArrayOutputStream aos = new ByteArrayOutputStream();
		try {
			jw.writeToStream(aos, object);
			return aos.toString("UTF-8");
		} catch (IOException e) {
			//e.printStackTrace();
		}

		return null;
	}

	public static LinkedHashMap<String, Object> fromJson(String rawJson) {
		JsonReader jr = new JsonReader();
		try {
			if (jr.parse(rawJson)) return jr.getResultMap();
		} catch (ParseException e) {
			//e.printStackTrace();
		}
		return null;
	}
}
