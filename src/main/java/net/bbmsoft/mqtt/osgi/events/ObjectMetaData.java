package net.bbmsoft.mqtt.osgi.events;

import java.util.Map;

public interface ObjectMetaData {

	public Map<String, Class<?>> getValuetypes();
}
