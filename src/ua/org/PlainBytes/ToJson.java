package ua.org.PlainBytes;

import java.util.LinkedHashMap;

public interface ToJson {
	LinkedHashMap<String, Object> toJsonMap();

	default String toJson() {
		LinkedHashMap<String, Object> data = toJsonMap();
		if (this instanceof FromJson) {
			data.put("_java_class", this.getClass().getName());
		}
		JsonWriter json = new JsonWriter(data);

		return json.writeToString();
	}
}
