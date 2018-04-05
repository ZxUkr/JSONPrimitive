package ua.org.PlainBytes;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;

public interface fromJson {
	default Object fromJson(String data) {
		LinkedHashMap<String, Object> dataMap = null;
		try {
			JsonReader json = new JsonReader(data);
			dataMap = json.getResultMap();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ParseException e) {
			//e.printStackTrace();
		}

		return fromJson(dataMap);
	}

	Object fromJson(LinkedHashMap<String, Object> data);
}
