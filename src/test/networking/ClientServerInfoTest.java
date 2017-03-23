package test.networking;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.ClientServerInfo;

public class ClientServerInfoTest {
	private ClientServerInfo server;

	@Before
	public void setUp() throws Exception {
		server = new ClientServerInfo(new InetSocketAddress("1.2.3.4", 1234));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertNotNull(server.toString());

		server.updateTimeStamp();
		assertTrue(server.getTimeStamp() > 0);

		assertEquals(new InetSocketAddress("1.2.3.4", 1234), server.getSocketAddress());

		for (short i = 0; i < 100; i++) {
			assertFalse(server.isSequenceDuplicate(i));
		}

		for (short i = 0; i < 101; i++) {
			assertEquals(i, server.getNextPacketSequenceAndIncrement());
			server.insertPacket(i, new DatagramPacket(new byte[1], 1));
		}

		for (short i = 0; i < 100; i++) {
			assertTrue(server.isSequenceDuplicate(i));
		}

		assertEquals(100, server.getPacketHistoryList().size());

		assertFalse(server.isSequenceDuplicate((short) 100));
	}

}
