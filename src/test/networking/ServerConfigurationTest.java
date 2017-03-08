package test.networking;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.ServerConfiguration;

public class ServerConfigurationTest {
	private ServerConfiguration config;

	@Before
	public void setUp() throws Exception {
		config = new ServerConfiguration();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(25, config.getClientTimeOut());
		config.setClientTimeOut(30);
		assertEquals(30, config.getClientTimeOut());

		assertEquals(10, config.getKeepAliveInterval());

		assertEquals(500, config.getRetransmitInterval());

		assertEquals(30, config.getTickRate());
		config.setTickRate(33);
		assertEquals(33, config.getTickRate());

		assertEquals(10, config.getMaxRetransmitCount());
		config.setMaxRetransmitCount(30);
		assertEquals(30, config.getMaxRetransmitCount());

		assertEquals(32, config.getMaxPlayer());

		config.setServerName("Test Name");
		assertEquals("Test Name", config.getServerName());
	}

}
