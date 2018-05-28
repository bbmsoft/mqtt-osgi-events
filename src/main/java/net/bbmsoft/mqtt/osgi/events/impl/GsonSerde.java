package net.bbmsoft.mqtt.osgi.events.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Component
public class GsonSerde implements SerDe {

	private Gson gson;
	private Type mapType;

	@Activate
	public void activate() {
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.mapType = new TypeToken<Map<String, ?>>() {
		}.getType();
	}

	@Override
	public String serialize(Map<String, ?> properties) {
		return gson.toJson(properties);
	}

	@Override
	public Map<String, ?> deserialize(String json) {
		return this.gson.fromJson(json, this.mapType);
	}

}
