package ua.org.PlainBytes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class JsonReader {
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
	protected InputStream is = null;
	protected Boolean isRootObject = null;
	protected Object result = null;

	public JsonReader() {

	}

	public JsonReader(File jsonFile) throws FileNotFoundException, IOException, ParseException {
		is = new BufferedInputStream(new FileInputStream(jsonFile));
		result = parseJson(is);
		is.close();
	}

	public JsonReader(String rawJson) throws IOException, ParseException {
		is = new ByteArrayInputStream(rawJson.getBytes(StandardCharsets.UTF_8));
		result = parseJson(is);
	}

	public Boolean isRootObject() {
		return isRootObject;
	}

	public Object getResult() {
		return result;
	}

	public LinkedHashMap<String, Object> getResultMap() {
		if (result instanceof LinkedHashMap) return (LinkedHashMap<String, Object>) result;
		return null;
	}

	public ArrayList<Object> getResultArray() {
		if (result instanceof ArrayList) return (ArrayList<Object>) result;
		return null;
	}

	public boolean parse(String rawJson) throws IOException, ParseException {
		is = new ByteArrayInputStream(rawJson.getBytes(StandardCharsets.UTF_8));
		isRootObject = null;
		result = parseJson(is);
		return isRootObject;
	}

	public boolean parse(File jsonFile) throws IOException, ParseException {
		is = new BufferedInputStream(new FileInputStream(jsonFile));
		isRootObject = null;
		result = parseJson(is);
		return isRootObject;
	}

	public Object parseJson(InputStream is) throws IOException, ParseException {
		pos = 0L;
		int oneChar;
		while ((oneChar = is.read()) != -1) {
			pos++;
			char realChar = (char) oneChar;
			switch (isOpen(realChar)) {
				case OBJECT:
					LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
					parseObject(is, dataMap);
					isRootObject = true;
					return dataMap;
				case ARRAY:
					ArrayList<Object> dataArray = new ArrayList<Object>();
					parseArray(is, dataArray);
					isRootObject = false;
					return dataArray;
				case STRING1:
					throw new ParseException("Unexpected symbol \" at this position. At position " + (pos - 1), (int) (pos - 1));
				case STRING2:
					throw new ParseException("Unexpected symbol ' at this position. At position " + (pos - 1), (int) (pos - 1));
				case COLON:
					throw new ParseException("Unexpected COLON at this position. At position " + (pos - 1), (int) (pos - 1));
				case COMMA:
					throw new ParseException("Unexpected COMMA at this position. At position " + (pos - 1), (int) (pos - 1));
			}
		}

		return null;
	}

	protected void parseObject(InputStream is, LinkedHashMap<String, Object> data) throws IOException, ParseException {
		int oneChar;
		String name = null;
		StringBuilder value = new StringBuilder();
		TOKEN current = TOKEN.STRING;
		while ((oneChar = is.read()) != -1) {
			pos++;
			char realChar = (char) oneChar;
			switch (isOpen(realChar)) {
				case OBJECT:
					if (current != TOKEN.VALUE) {
						throw new ParseException("Unexpected OBJECT in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
					}
					LinkedHashMap<String, Object> subData = new LinkedHashMap<String, Object>();
					data.put(name, subData);
					parseObject(is, subData);
					value.setLength(0);
					current = TOKEN.COMMA;
					break;
				case ARRAY:
					if (current != TOKEN.VALUE) {
						throw new ParseException("Unexpected ARRAY in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
					}
					ArrayList<Object> subArray = new ArrayList<Object>();
					data.put(name, subArray);
					parseArray(is, subArray);
					value.setLength(0);
					current = TOKEN.COMMA;
					break;
				case STRING1:
					if (current == TOKEN.STRING) {
						name = parseString(is, STRING1);
						current = TOKEN.COLON;
					} else if (current == TOKEN.VALUE) {
						data.put(name, parseString(is, STRING1));
						current = TOKEN.COMMA;
					}
					break;
				case STRING2:
					if (current == TOKEN.STRING) {
						name = parseString(is, STRING2);
						current = TOKEN.COLON;
					} else if (current == TOKEN.VALUE) {
						data.put(name, parseString(is, STRING2));
						current = TOKEN.COMMA;
					}
					break;
				case COLON:
					if (current == TOKEN.COLON) {
						current = TOKEN.VALUE;
					} else {
						throw new ParseException("Unexpected COLON in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
					}
					continue;
				case COMMA:
					if (current == TOKEN.VALUE) {
						if (value.toString().trim().length() == 0) {
							throw new ParseException("Unexpected COMMA in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
						}
						data.put(name, parsePrimitiveValue(value.toString()));
						value.setLength(0);
					} else if (current != TOKEN.COMMA) {
						throw new ParseException("Unexpected COMMA in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
					}
					current = TOKEN.STRING;
					continue;
				default:
					if (current == TOKEN.VALUE) {
						value.append(realChar);
					}
			}
			switch (isClose(realChar)) {
				case OBJECT:
					return;
				case ARRAY:
					throw new ParseException("Unexpected ARRAY end in OBJECT area. At position " + (pos - 1), (int) (pos - 1));
			}
		}
	}

	protected void parseArray(InputStream is, ArrayList<Object> data) throws IOException, ParseException {
		int oneChar;
		StringBuilder value = new StringBuilder();
		TOKEN current = TOKEN.VALUE;
		while ((oneChar = is.read()) != -1) {
			pos++;
			char realChar = (char) oneChar;
			switch (isOpen(realChar)) {
				case OBJECT:
					if (current != TOKEN.VALUE) {
						throw new ParseException("Unexpected OBJECT in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
					}
					LinkedHashMap<String, Object> subData = new LinkedHashMap<String, Object>();
					data.add(subData);
					parseObject(is, subData);
					value.setLength(0);
					current = TOKEN.COMMA;
					break;
				case ARRAY:
					if (current != TOKEN.VALUE) {
						throw new ParseException("Unexpected ARRAY in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
					}
					ArrayList<Object> subArray = new ArrayList<Object>();
					data.add(subArray);
					parseArray(is, subArray);
					value.setLength(0);
					current = TOKEN.COMMA;
					break;
				case STRING1:
					if (current != TOKEN.VALUE) {
						throw new ParseException("Unexpected symbol \" in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
					}
					data.add(parseString(is, STRING1));
					current = TOKEN.COMMA;
					continue;
				case STRING2:
					if (current != TOKEN.VALUE) {
						throw new ParseException("Unexpected symbol \' in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
					}
					data.add(parseString(is, STRING2));
					current = TOKEN.COMMA;
					continue;
				case COLON:
					throw new ParseException("Unexpected COLON in ARRAY area. At position " + (pos - 1), (int) (pos - 1));
				case COMMA:
					if (current == TOKEN.VALUE) {
						if (value.toString().trim().length() == 0) {
							throw new ParseException("Unexpected COMMA in place of a " + current.toString() + ". At position " + (pos - 1), (int) (pos - 1));
						}
						data.add(parsePrimitiveValue(value.toString()));
						value.setLength(0);
					}
					current = TOKEN.VALUE;
					continue;
				default:
					if (current == TOKEN.VALUE) {
						value.append(realChar);
					}
			}
			switch (isClose(realChar)) {
				case OBJECT:
					throw new ParseException("Unexpected OBJECT end in ARRAY area. At position " + (pos - 1), (int) (pos - 1));
				case ARRAY:
					return;
			}
		}
	}

	protected Object parsePrimitiveValue(String rawValue) throws ParseException {
		if (rawValue == null) return null;
		rawValue = rawValue.trim();

		if (rawValue.isEmpty() || rawValue.equalsIgnoreCase("null"))
			return null;
		if (rawValue.equalsIgnoreCase("true"))
			return Boolean.TRUE;
		if (rawValue.equalsIgnoreCase("false"))
			return Boolean.FALSE;
		try {
			return NumberFormat.getInstance().parse(rawValue);
		} catch (ParseException e) {
			throw new ParseException("Unknown type of VALUE before position " + (pos - 1), (int) (pos - 1));
		}
	}

	protected String parseString(InputStream is, int token) throws IOException {
		int oneChar, prevChar = -1;
		StringBuilder str = new StringBuilder();
		while ((oneChar = is.read()) != -1) {
			pos++;
			char realChar = (char) oneChar;
			if (prevChar != '\\') {
				if (realChar == bracesClose[token]) return str.toString();
				else if (realChar != '\\') str.append(realChar);
				prevChar = oneChar;
			} else {
				switch (realChar) {
					case '"':
					case '\\':
					case '/':
						str.append(realChar);
						break;
					case 'b':
						str.append("\b");
						break;
					case 'f':
						str.append("\f");
						break;
					case 'n':
						str.append("\n");
						break;
					case 'r':
						str.append("\r");
						break;
					case 't':
						str.append("\t");
						break;
					case 'u':
						str.append(parse4hexDigits(is));
						break;
					default:
						str.append("\\").append(realChar);
						break;
				}
				prevChar = -1;
			}
		}

		return str.toString();
	}

	protected char parse4hexDigits(InputStream is) throws IOException {
		String hex = "" + (char) is.read() + (char) is.read() + (char) is.read() + (char) is.read();
		pos += 4;
		int value = Integer.parseInt(hex, 16);
		return (char) value;
	}

	protected static int isOpen(char symbol) {
		for (int i = 0; i < bracesOpen.length; i++) {
			if (bracesOpen[i] == symbol) return i;
		}
		return -1;
	}

	protected static int isClose(char symbol) {
		for (int i = 0; i < bracesClose.length; i++) {
			if (bracesClose[i] == symbol) return i;
		}
		return -1;
	}
}
