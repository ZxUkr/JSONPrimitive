package ua.org.PlainBytes;

import java.util.LinkedHashMap;

public interface ToJson {
	LinkedHashMap<String, Object> toJsonMap();

	default String toJson() {
		LinkedHashMap<String, Object> data = toJsonMap();
		if (this instanceof FromJson) {
			data.put("_java_class", this.getClass().getName());
			//TODO maybe it's worth deleting this item after write for save Object as it was
		}
		JsonWriter json = new JsonWriter(data, false);

		return json.writeToString();
	}
}
