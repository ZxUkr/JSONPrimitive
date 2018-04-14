package ua.org.PlainBytes.JsonPrimitive;

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
	int depth = 0;
	protected Boolean isRootObject = null;
	protected boolean formattedOutput = false;
	protected Object data = null;
	protected String newLineSeparator = "\r\n";

	public void setFormattedOutput(boolean formattedOutput) {
		this.formattedOutput = formattedOutput;
	}

	public JsonWriter() {

	}

	public JsonWriter(boolean formattedOutput) {
		this.formattedOutput = formattedOutput;
	}

	public JsonWriter(LinkedHashMap<String, Object> data, boolean formattedOutput) {
		//os = new BufferedOutputStream(new FileOutputStream(jsonFile));
		isRootObject = true;
		this.formattedOutput = formattedOutput;
		this.data = data;
	}

	public JsonWriter(ArrayList<Object> data, boolean formattedOutput) {
		isRootObject = false;
		this.formattedOutput = formattedOutput;
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

	public String writeToString(Object data) {
		if (!(data instanceof ArrayList)) isRootObject = true;
		else isRootObject = false;
		this.data = data;
		return writeToString();
	}

	public void writeToFile(File jsonFile) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(jsonFile));
		writeToStream(bos, data);
		bos.close();
	}

	public void writeToFile(File jsonFile, Object data) throws IOException {
		if (!(data instanceof ArrayList)) isRootObject = true;
		else isRootObject = false;
		this.data = data;
		writeToFile(jsonFile);
	}

	public void writeToStream(OutputStream os, Object data) throws IOException {
		pos = 0L;
		depth = 0;
		if (data instanceof LinkedHashMap) {
			isRootObject = true;
			writeObjectToStream(os, (LinkedHashMap<String, Object>) data);
		} else if (data instanceof ArrayList) {
			isRootObject = true;
			writeArrayToStream(os, (ArrayList<Object>) data);
		} else {
			writeValueToStream(os, data);
		}
	}

	protected void writeObjectToStream(OutputStream os, LinkedHashMap<String, Object> data) throws IOException {
		if (data == null) {
			os.write("null".getBytes(StandardCharsets.UTF_8));
			return;
		}
		os.write(bracesOpen[OBJECT]);
		boolean first = true;
		if (formattedOutput) {
			depth++;
			for (Map.Entry<String, Object> item : data.entrySet()) {
				if (!first) os.write(bracesOpen[COMMA]);
				os.write(newLineSeparator.getBytes(StandardCharsets.UTF_8));
				writeIndent(os);
				writeStringToStream(os, item.getKey());
				os.write(' ');
				os.write(bracesOpen[COLON]);
				os.write(' ');
				writeValueToStream(os, item.getValue());
				first = false;
			}
			depth--;
			if (data.size() > 0) {
				os.write(newLineSeparator.getBytes(StandardCharsets.UTF_8));
				writeIndent(os);
			}
		} else {
			for (Map.Entry<String, Object> item : data.entrySet()) {
				if (!first) os.write(bracesOpen[COMMA]);
				writeStringToStream(os, item.getKey());
				os.write(bracesOpen[COLON]);
				writeValueToStream(os, item.getValue());
				first = false;
			}
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
		if (formattedOutput) {
			depth++;
			for (Object value : data) {
				if (!first) {
					os.write(bracesOpen[COMMA]);
					os.write(' ');
				}
				//if (!(value instanceof String || value instanceof Number || value instanceof Boolean)) {
				//	os.write(newLineSeparator.getBytes(StandardCharsets.UTF_8));
				//	writeIndent(os);
				//} else if (!first) {
				//	os.write(' ');
				//}
				writeValueToStream(os, value);
				first = false;
			}
			depth--;
		} else {
			for (Object value : data) {
				if (!first) os.write(bracesOpen[COMMA]);
				writeValueToStream(os, value);
				first = false;
			}
		}
		os.write(bracesClose[ARRAY]);
	}

	protected void writeValueToStream(OutputStream os, Object value) throws IOException {
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
			os.write(jsonValue.getBytes(StandardCharsets.UTF_8));
		} else {
			//jsonValue = String.format("{\"_java_class\":\"%s\", \"value\":\"%s\"}", data.getClass().getName(), data.toString());
			//os.write(jsonValue.getBytes(StandardCharsets.UTF_8));
			writeStringToStream(os, data.toString());
		}
	}

	protected void writeIndent(OutputStream os) throws IOException {
		for (int i = 0; i < depth; i++) os.write('\t');
	}
}
