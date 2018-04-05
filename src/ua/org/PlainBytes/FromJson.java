package ua.org.PlainBytes;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;

public interface FromJson {
	default Object fromJson(String data) {
		LinkedHashMap<String, Object> dataMap = null;
		try {
			JsonReader json = new JsonReader(data);
			dataMap = json.getResultMap();
		} catch (ParseException e) {
			//e.printStackTrace();
		}

		return fromJson(dataMap);
	}

	static Object fromJson(LinkedHashMap<String, Object> data) {
		return null;
	}
}
