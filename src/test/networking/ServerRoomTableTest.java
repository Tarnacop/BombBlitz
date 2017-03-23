package test.networking;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.ServerRoom;
import bomber.networking.ServerRoomTable;

public class ServerRoomTableTest {
	private ServerRoomTable table;

	@Before
	public void setUp() throws Exception {
		table = new ServerRoomTable(33);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertFalse(table.contains(0));
		assertFalse(table.contains(null));

		assertEquals(0, table.entrySet2().size());

		assertEquals(null, table.get(0));

		ServerRoom r1 = new ServerRoom("R1", null, null, 0);

		table.put(r1);

		assertTrue(table.contains(0));
		assertTrue(table.contains(r1));

		assertEquals(1, table.entrySet2().size());

		assertEquals(r1, table.get(0));

		ServerRoom r2 = new ServerRoom("R2", null, null, 0);

		table.put(r2);

		assertTrue(table.contains(1));
		assertTrue(table.contains(r2));

		assertEquals(2, table.entrySet2().size());

		assertEquals(r2, table.get(1));

		table.remove(1);
		assertEquals(1, table.entrySet2().size());

		table.remove(0);
		assertEquals(0, table.entrySet2().size());

	}

}
