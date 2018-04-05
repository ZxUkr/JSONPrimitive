package ua.org.PlainBytes;

import java.util.LinkedHashMap;

public interface toJson {
	LinkedHashMap<String, Object> toJsonableMap();

	default String toJson() {
		LinkedHashMap<String, Object> data = toJsonableMap();
		JsonWriter json = new JsonWriter(data);

		return json.writeToString();
	}
}
