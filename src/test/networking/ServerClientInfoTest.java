package test.networking;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.ServerClientInfo;

public class ServerClientInfoTest {
	private ServerClientInfo sci;

	@Before
	public void setUp() throws Exception {
		sci = new ServerClientInfo(new InetSocketAddress("1.1.1.1", 1111), "client111");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(new InetSocketAddress("1.1.1.1", 1111), sci.getSocketAddress());

		assertEquals("client111", sci.getName());

		assertTrue(sci.getTimeStamp() <= Instant.now().getEpochSecond());

		sci.setID(11);
		assertEquals(11, sci.getID());

		sci.setInLobby(true);
		assertTrue(sci.isInLobby());

		sci.setInLobby(false);
		assertFalse(sci.isInLobby());

		sci.setInRoom(true);
		assertTrue(sci.isInRoom());

		sci.setInRoom(false);
		assertFalse(sci.isInRoom());

		sci.setReadyToPlay(true);
		assertTrue(sci.isReadyToPlay());

		sci.setReadyToPlay(false);
		assertFalse(sci.isReadyToPlay());

		assertEquals(0, sci.getNextPacketSequenceAndIncrement());
		assertEquals(1, sci.getNextPacketSequenceAndIncrement());

		assertFalse(sci.isSequenceDuplicate((short) 0));
		assertTrue(sci.isSequenceDuplicate((short) 0));
		assertTrue(sci.isSequenceDuplicate((short) 0));

		sci.updateTimeStamp();
		assertTrue(sci.getTimeStamp() <= Instant.now().getEpochSecond());

	}

}
