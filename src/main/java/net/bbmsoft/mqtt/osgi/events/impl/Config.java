package net.bbmsoft.mqtt.osgi.events.impl;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
@interface Config {

	String mqtt_broker_address();

	String[] mqtt_topics();
	
	String[] osgi_event_topics();
}