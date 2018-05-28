package net.bbmsoft.mqtt.osgi.events.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UTF8StringCodecTest {

	@Test
	public void testUtf8StringDecode() {

		UTF8Codec codec = new UTF8Codec();
		String utf8String = "1234567890-=!\"£$%^&*()_+¹²³€½¾{[]}\\|,./<>?·öä#ü+Ü*ÖÄ'0ß'`";

		byte[] bytes = codec.encode(utf8String);

		// since UTF8Codec returns an interned String we do actually expect the same
		// String Object and not just an equal String here
		assertTrue(utf8String == codec.decode(bytes));
	}
}
