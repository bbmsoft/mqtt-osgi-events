package net.bbmsoft.mqtt.osgi.events.impl;

import static org.osgi.service.event.EventConstants.EVENT_TOPIC;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@Component(property = EVENT_TOPIC + "=*", configurationPid = "mqtt")
public class EventToMqttAdapterImpl implements EventHandler {

	private ExecutorService eventWorker;

	@Reference
	private MqttClientService mqttClient;

	@Reference
	private SerDe serde;

	private String id;

	private volatile List<String> topics;

	@Activate
	public void activate(Config config, BundleContext ctx) {
		this.id = ctx.getProperty(Constants.FRAMEWORK_UUID).toString();
//		this.eventWorker = Executors.newCachedThreadPool(r -> new Thread(r, "Event to MQTT Worker"));
		this.eventWorker = Executors.newSingleThreadExecutor();
		modified(config);
	}

	@Modified
	public void modified(Config config) {
		this.topics = Arrays.asList(config.osgi_event_topics());
	}

	@Deactivate
	public void deactivate() {
		this.eventWorker.shutdown();
	}

	@Override
	public void handleEvent(Event event) {
		this.eventWorker.execute(() -> doHandleEvent(event));
	}

	private void doHandleEvent(Event event) {

		if (!topicMatches(event)) {
			return;
		}

		Map<String, Object> properties = getProperties(event);

		if (this.id.equals(String.valueOf(properties.get("_sender")))) {
			// this event was generated from an MQTT message coming from this framework. No
			// need to send it again.
			return;
		} else {
			properties.put("_sender", this.id);
		}

		String message = this.serde.serialize(properties);

		try {
			this.mqttClient.publish(event.getTopic(), message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean topicMatches(Event event) {

		String eventTopic = event.getTopic();
		
		for (String topic : this.topics) {
			if (eventTopic.startsWith(topic)) {
				return true;
			}
		}

		return false;
		
	}

	private Map<String, Object> getProperties(Event event) {

		Map<String, Object> out = new HashMap<>();

		for (String prop : event.getPropertyNames()) {
			out.put(prop, event.getProperty(prop));
		}

		return out;
	}

}
