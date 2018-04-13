package ua.org.PlainBytes.JsonPrimitive;

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

		return fromJsonMap(dataMap);
	}

	Object fromJsonMap(LinkedHashMap<String, Object> data);
}
