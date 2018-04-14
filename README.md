# JSONPrimitive
Library for convert json to LinkedHashMap and back. With maximum simple code as it posible.


## Parse String with json
This method does't throw any exception just return null when somesing was wrong
```java
LinkedHashMap<String, Object> parsedData = JsonPrimitive.fromJson(someJsonString);
```

## Convert Object to json String in human-readable format
```java
String json = JsonPrimitive.toJson(parsedData,true);
```

## If you want more control
```java
LinkedHashMap<String, Object> dataMap = null;
ArrayList<Object> dataArray = null;

JsonReader jsonReader = new JsonReader();
JsonWriter jsonWriter = new JsonWriter(true);

try {
    //Parse String with json
    //This method return true if root object is LinkedHashMap and false if it is ArrayList
    if (jsonReader.parse(someJsonString)) {
        //get result
        dataMap = jsonReader.getResultMap();
    } else {
        //get result
        dataArray = jsonReader.getResultArray();
    }
} catch (ParseException e) {
    e.printStackTrace();
}

try {
    //Write object to json file
    jsonWriter.writeToFile(new File("someFile.json"), dataMap);
} catch (IOException e) {
    e.printStackTrace();
}
```

Also you can read and write to any Stream
jsonWriter.writeToStream(OutputStream, Object);
jsonReader.parseJson(InputStream)

## Work with your custom class objects
You have to implement interface FromJson and/or ToJson which will works with LinkedHashMap<String, Object> as transport
```java
public class Bicycle implements ToJson, FromJson {
	public int cadence;
	public int gear;
	public int speed;

	public Bicycle(int startCadence, int startSpeed, int startGear) {
		gear = startGear;
		cadence = startCadence;
		speed = startSpeed;
	}

	public void setCadence(int newValue) {
		cadence = newValue;
	}

	public void setGear(int newValue) {
		gear = newValue;
	}

	public void applyBrake(int decrement) {
		speed -= decrement;
	}

	public void speedUp(int increment) {
		speed += increment;
	}

	@Override
	public LinkedHashMap<String, Object> toJsonMap() {
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("cadence", cadence);
		data.put("gear", gear);
		data.put("speed", speed);
		return data;
	}

	//@Override
	public Object fromJsonMap(LinkedHashMap<String, Object> data) {
		return new Bicycle(((Long)data.get("cadence")).intValue(), ((Long)data.get("gear")).intValue(), ((Long)data.get("speed")).intValue());
	}
}
```

Just try it in "examples" branch.
