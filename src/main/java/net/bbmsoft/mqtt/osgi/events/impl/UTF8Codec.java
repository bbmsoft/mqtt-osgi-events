package net.bbmsoft.mqtt.osgi.events.impl;

import java.nio.charset.Charset;

import org.osgi.service.component.annotations.Component;

@Component
public class UTF8Codec implements StringCodec {

	@Override
	public byte[] encode(String string) {
		return string.getBytes(Charset.forName("UTF-8"));
	}

	@Override
	public String decode(byte[] bytes) {
		return new String(bytes, Charset.forName("UTF-8")).intern();
	}

	
}
