package test.networking;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.PacketHistoryEntry;

public class PacketHistoryEntryTest {
	private PacketHistoryEntry phe;
	private byte[] data;

	@Before
	public void setUp() throws Exception {
		data = new byte[] { 1, 3, 5, 7, 9 };
		phe = new PacketHistoryEntry((short) 1553, data, data.length);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(phe.getCreationTimeStamp() <= System.currentTimeMillis());
		assertEquals((short) 1553, phe.getSequence());
		assertArrayEquals(new byte[] { 1, 3, 5, 7, 9 }, phe.getPacketData());
		assertEquals(data.length, phe.getPacketLength());
		assertEquals(0, phe.getRetransmissionCount());
		assertEquals(0, phe.getRetransmissionCountAndIncrement());
		assertEquals(1, phe.getRetransmissionCount());
		phe.incrementRetransmissionCount();
		assertEquals(2, phe.getRetransmissionCount());
		assertFalse(phe.isAcked());
		phe.setAcked(true);
		assertTrue(phe.isAcked());

		phe.reset((short) 126, new byte[] { 1, 3 }, 2);

		assertTrue(phe.getCreationTimeStamp() <= System.currentTimeMillis());
		assertEquals((short) 126, phe.getSequence());
		assertEquals(1, phe.getPacketData()[0]);
		assertEquals(3, phe.getPacketData()[1]);
		assertEquals(2, phe.getPacketLength());
		assertEquals(0, phe.getRetransmissionCount());
		assertEquals(0, phe.getRetransmissionCountAndIncrement());
		assertEquals(1, phe.getRetransmissionCount());
		phe.incrementRetransmissionCount();
		assertEquals(2, phe.getRetransmissionCount());
		assertFalse(phe.isAcked());
		phe.setAcked(true);
		assertTrue(phe.isAcked());
	}

}
