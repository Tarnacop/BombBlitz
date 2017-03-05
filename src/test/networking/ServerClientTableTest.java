package test.networking;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.ServerClientInfo;
import bomber.networking.ServerClientTable;

public class ServerClientTableTest {
	private ServerClientTable table;

	@Before
	public void setUp() throws Exception {
		table = new ServerClientTable(32);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(0, table.size());
		assertFalse(table.contains(0));
		assertFalse(table.contains(new InetSocketAddress("1.1.1.1", 1111)));
		assertFalse(table.contains("P1"));

		ServerClientInfo c = new ServerClientInfo(new InetSocketAddress("1.1.1.1", 1111), "P1");
		table.put(c);

		assertEquals(1, table.size());
		assertTrue(table.contains(0));
		assertTrue(table.contains(new InetSocketAddress("1.1.1.1", 1111)));
		assertTrue(table.contains("P1"));

		assertFalse(table.contains(new InetSocketAddress("1.1.1.2", 1112)));
		assertFalse(table.contains("P2"));

		ServerClientInfo c2 = new ServerClientInfo(new InetSocketAddress("1.1.1.2", 1112), "P2");
		table.put(c2);

		assertEquals(2, table.size());
		assertTrue(table.contains(0));
		assertTrue(table.contains(new InetSocketAddress("1.1.1.1", 1111)));
		assertTrue(table.contains("P1"));
		assertTrue(table.contains(1));
		assertTrue(table.contains(new InetSocketAddress("1.1.1.2", 1112)));
		assertTrue(table.contains("P2"));

		table.remove(1);
		assertFalse(table.contains(new InetSocketAddress("1.1.1.2", 1112)));
		assertFalse(table.contains("P2"));

		table.put(c2);
		table.remove(new InetSocketAddress("1.1.1.2", 1112));
		assertFalse(table.contains(new InetSocketAddress("1.1.1.2", 1112)));
		assertFalse(table.contains("P2"));

		table.put(c2);
		table.remove("P2");
		assertFalse(table.contains(new InetSocketAddress("1.1.1.2", 1112)));
		assertFalse(table.contains("P2"));

		table.remove(0);
		assertFalse(table.contains(new InetSocketAddress("1.1.1.1", 1111)));
		assertFalse(table.contains("P1"));

		table.put(c);
		table.remove(new InetSocketAddress("1.1.1.1", 1111));
		assertFalse(table.contains(new InetSocketAddress("1.1.1.1", 1111)));
		assertFalse(table.contains("P1"));

		table.put(c);
		table.remove("P1");
		assertEquals(0, table.size());
		assertFalse(table.contains(new InetSocketAddress("1.1.1.1", 1111)));
		assertFalse(table.contains("P1"));
	}
}
