package net.bbmsoft.mqtt.osgi.events.impl;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(service = MqttClientService.class, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = "mqtt")
public class MqttClientService {

	private MqttClient mqtt;

	@Reference
	private MqttCallback mqttCallback;

	@Reference
	private StringCodec stringCodec;

	private String id;

	@Activate
	public synchronized void activate(Config config, BundleContext ctx) throws MqttException {
		this.id = ctx.getProperty(Constants.FRAMEWORK_UUID).toString();
		modified(config);
	}

	@Modified
	public synchronized void modified(Config config) throws MqttException {

		deactivate();

		this.mqtt = new MqttClient(config.mqtt_broker_address(), this.id);
		this.mqtt.setCallback(this.mqttCallback);
		this.mqtt.connect();

		for (String topic : config.mqtt_topics()) {
			this.mqtt.subscribe(topic.replaceAll("\\*", "#"));
		}
	}

	@Deactivate
	public synchronized void deactivate() throws MqttException {

		if (this.mqtt != null) {
			try {
				this.mqtt.disconnect();
				this.mqtt.close();
			} finally {
				this.mqtt = null;
			}
		}
	}

	public void subscribe(String topic) throws IOException {

		try {
			this.mqtt.subscribe(topic.replaceAll("\\*", "#"));
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	public void unsubscribe(String topic) throws IOException {

		try {
			this.mqtt.unsubscribe(topic.replaceAll("\\*", "#"));
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	public void publish(String topic, String message) throws IOException {

		byte[] bytes = this.stringCodec.encode(message);
		MqttMessage mqttMessage = new MqttMessage(bytes);

		try {
			this.mqtt.publish(topic, mqttMessage);
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}
}
