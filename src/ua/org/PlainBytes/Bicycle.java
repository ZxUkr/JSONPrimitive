package ua.org.PlainBytes;

import java.util.LinkedHashMap;

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
	public static Object fromJson(LinkedHashMap<String, Object> data) {
		return new Bicycle(((Long)data.get("cadence")).intValue(), ((Long)data.get("gear")).intValue(), ((Long)data.get("speed")).intValue());
	}
}