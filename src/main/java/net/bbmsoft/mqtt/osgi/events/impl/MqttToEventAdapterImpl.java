package net.bbmsoft.mqtt.osgi.events.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

@Component
public class MqttToEventAdapterImpl implements MqttCallback {

	@Reference
	private EventAdmin eventAdmin;

	@Reference
	private SerDe serde;

	@Reference
	private StringCodec codec;

	private String id;

	@Activate
	public void activate(BundleContext ctx) {
		this.id = ctx.getProperty(Constants.FRAMEWORK_UUID).toString();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {

		byte[] payload = message.getPayload();

		String json = this.codec.decode(payload);

		Map<String, Object> properties = new HashMap<>(this.serde.deserialize(json));

		if (this.id.equals(String.valueOf(properties.get("_sender")))) {
			// this mqtt message was generated from an OSGi event coming from this
			// framework. No need to post it again.
			return;
		} else {
			properties.put("_sender", this.id);
		}

		Event event = new Event(topic, (Dictionary<String, ?>) new Hashtable<>(properties));

		this.eventAdmin.postEvent(event);

	}

	@Override
	public void connectionLost(Throwable cause) {

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}

}
