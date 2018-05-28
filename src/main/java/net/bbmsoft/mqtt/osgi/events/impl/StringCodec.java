package net.bbmsoft.mqtt.osgi.events.impl;

public interface StringCodec {

	public byte[] encode(String string);

	public String decode(byte[] bytes);
}
