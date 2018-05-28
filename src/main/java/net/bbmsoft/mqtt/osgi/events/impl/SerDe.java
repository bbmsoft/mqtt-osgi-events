package net.bbmsoft.mqtt.osgi.events.impl;

import java.util.Map;

public interface SerDe {

	public String serialize(Map<String, ?> properties);
	
	public Map<String, ?> deserialize(String json);
}
