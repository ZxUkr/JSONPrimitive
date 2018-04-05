package ua.org.PlainBytes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonWriter {
	public enum TOKEN {
		OBJECT, ARRAY, STRING, VALUE, COLON, COMMA
	}

	protected static final int OBJECT = 0;
	protected static final int ARRAY = 1;
	protected static final int STRING1 = 2;
	protected static final int STRING2 = 3;
	protected static final int COLON = 4; //':';
	protected static final int COMMA = 5; //',';
	protected static final char[] bracesOpen = {'{', '[', '"', '\'', ':', ','};
	protected static final char[] bracesClose = {'}', ']', '"', '\''};

	protected long pos;
	protected Boolean isRootObject = null;
	protected Object data = null;

	public JsonWriter() {

	}

	public JsonWriter(LinkedHashMap<String, Object> data) {
		//os = new BufferedOutputStream(new FileOutputStream(jsonFile));
		isRootObject = true;
		this.data = data;
	}

	public JsonWriter(ArrayList<Object> data) {
		isRootObject = false;
		this.data = data;
	}

	public String writeToString() {
		ByteArrayOutputStream aos = new ByteArrayOutputStream();
		try {
			writeToStream(aos, data);
		} catch (IOException e) {
			//e.printStackTrace();
		}
		try {
			return aos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}

		return null;
	}

	public String writeToString(LinkedHashMap<String, Object> data) {
		isRootObject = true;
		this.data = data;
		return writeToString();
	}

	public String writeToString(ArrayList<Object> data) {
		isRootObject = false;
		this.data = data;
		return writeToString();
	}

	public void writeToFile(File jsonFile) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(jsonFile));
		writeToStream(bos, data);
		bos.close();
	}

	public void writeToFile(File jsonFile, LinkedHashMap<String, Object> data) throws IOException {
		isRootObject = true;
		this.data = data;
		writeToFile(jsonFile);
	}

	public void writeToFile(File jsonFile, ArrayList<Object> data) throws IOException {
		isRootObject = false;
		this.data = data;
		writeToFile(jsonFile);
	}

	public void writeToStream(OutputStream os, Object data) throws IOException {
		pos = 0L;
		if (data instanceof LinkedHashMap) {
			isRootObject = true;
			writeObjectToStream(os, (LinkedHashMap<String, Object>) data);
		} else if (data instanceof ArrayList) {
			isRootObject = true;
			writeArrayToStream(os, (ArrayList<Object>) data);
		}
	}

	protected void writeObjectToStream(OutputStream os, LinkedHashMap<String, Object> data) throws IOException {
		if (data == null) {
			os.write("null".getBytes(StandardCharsets.UTF_8));
			return;
		}
		os.write(bracesOpen[OBJECT]);
		boolean first = true;
		for (Map.Entry<String, Object> item : data.entrySet()) {
			if (!first) os.write(bracesOpen[COMMA]);
			os.write(bracesOpen[STRING1]);
			os.write(item.getKey().getBytes(StandardCharsets.UTF_8));
			os.write(bracesOpen[STRING1]);
			os.write(bracesOpen[COLON]);
			Object value = item.getValue();
			if (value == null) {
				os.write("null".getBytes(StandardCharsets.UTF_8));
			} else if (value instanceof String) {
				writeStringToStream(os, (String) value);
			} else if (value instanceof Number) {
				writeNumberToStream(os, (Number) value);
			} else if (value instanceof Boolean) {
				writeBooleanToStream(os, (Boolean) value);
			} else if (value instanceof LinkedHashMap) {
				writeObjectToStream(os, (LinkedHashMap<String, Object>) value);
			} else if (value instanceof ArrayList) {
				writeArrayToStream(os, (ArrayList<Object>) value);
			} else {
				writeCustomToStream(os, value);
			}
			first = false;
		}
		os.write(bracesClose[OBJECT]);
	}

	protected void writeArrayToStream(OutputStream os, ArrayList<Object> data) throws IOException {
		if (data == null) {
			os.write("null".getBytes(StandardCharsets.UTF_8));
			return;
		}
		os.write(bracesOpen[ARRAY]);
		boolean first = true;
		for (Object value : data) {
			if (!first) os.write(bracesOpen[COMMA]);
			if (value == null) {
				os.write("null".getBytes(StandardCharsets.UTF_8));
			} else if (value instanceof String) {
				writeStringToStream(os, (String) value);
			} else if (value instanceof Number) {
				writeNumberToStream(os, (Number) value);
			} else if (value instanceof Boolean) {
				writeBooleanToStream(os, (Boolean) value);
			} else if (value instanceof LinkedHashMap) {
				writeObjectToStream(os, (LinkedHashMap<String, Object>) value);
			} else if (value instanceof ArrayList) {
				writeArrayToStream(os, (ArrayList<Object>) value);
			} else {
				//todo custom writing
			}
			first = false;
		}
		os.write(bracesClose[ARRAY]);
	}

	protected void writeStringToStream(OutputStream os, String data) throws IOException {
		if (data == null) {
			os.write("null".getBytes(StandardCharsets.UTF_8));
			return;
		}
		StringBuilder buff = new StringBuilder(data.length());
		buff.append(bracesOpen[STRING1]);
		for (int i = 0; i < data.length(); i++) {
			char realChar = data.charAt(i);
			if (Character.isSurrogate(realChar)) {
				//int codePoint = data.codePointAt(i);
				buff.append(String.format("\\u%x\\u%x", (int) realChar, (int) data.charAt(i + 1)));
				i++;
			} else switch (realChar) {
				case '"':
				case '\\':
				case '/':
					buff.append('\\').append(realChar);
					break;
				case '\b':
					buff.append("\\b");
					break;
				case '\f':
					buff.append("\\f");
					break;
				case '\n':
					buff.append("\\n");
					break;
				case '\r':
					buff.append("\\r");
					break;
				case '\t':
					buff.append("\\t");
					break;
				default:
					buff.append(realChar);
					break;
			}
		}
		buff.append(bracesOpen[STRING1]);
		os.write(buff.toString().getBytes(StandardCharsets.UTF_8));
	}

	protected void writeNumberToStream(OutputStream os, Number data) throws IOException {
		if (data == null) {
			os.write("null".getBytes(StandardCharsets.UTF_8));
			return;
		}
		os.write(data.toString().getBytes(StandardCharsets.UTF_8));
	}

	protected void writeBooleanToStream(OutputStream os, Boolean data) throws IOException {
		if (data == null) {
			os.write("null".getBytes(StandardCharsets.UTF_8));
			return;
		}
		if (data) {
			os.write("true".getBytes(StandardCharsets.UTF_8));
		} else {
			os.write("false".getBytes(StandardCharsets.UTF_8));
		}
	}

	protected void write4hexDigitsToStream(OutputStream os, char data) throws IOException {
		os.write(String.format("\\u%x", (int) data).getBytes(StandardCharsets.UTF_8));
	}

	protected void writeCustomToStream(OutputStream os, Object data) throws IOException {
		if (data == null) {
			os.write("null".getBytes(StandardCharsets.UTF_8));
			return;
		}
		String jsonValue = null;
		if (data instanceof ToJson) {
			jsonValue = ((ToJson) data).toJson();
		} else {
			jsonValue = String.format("{\"_java_class\":\"%s\", \"value\":\"%s\"}", data.getClass().getName(), data.toString());
		}
		os.write(jsonValue.getBytes(StandardCharsets.UTF_8));
	}
}
