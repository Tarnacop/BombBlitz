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
		config = new ServerConfiguration(25, -10, -500, 10, -32);
		config = new ServerConfiguration(25, 10, 500, 10, 32);
		config = new ServerConfiguration(-25, -10, -32);
		config = new ServerConfiguration(25, 10, 32);
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

		config.setTickRate(-1);
		assertEquals(20, config.getTickRate());
		config.setTickRate(33);
		assertEquals(33, config.getTickRate());

		config.setMaxRetransmitCount(-1);
		assertEquals(10, config.getMaxRetransmitCount());
		config.setMaxRetransmitCount(30);
		assertEquals(30, config.getMaxRetransmitCount());

		assertEquals(32, config.getMaxPlayer());

		config.setServerName(null);
		assertNotNull(config.getServerName());

		config.setServerName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", config.getServerName());

		config.setServerName("Test Name");
		assertEquals("Test Name", config.getServerName());

		assertEquals("Bomb Blitz", config.getGameName());

		assertTrue(config.getMaxNameLength() > 1);

		assertNotNull(config.getVersion());
	}

}
