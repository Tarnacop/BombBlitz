package test.networking;

import static org.junit.Assert.*;

import org.junit.Test;

import bomber.networking.BitArray;

public class BitArrayTest {

	@Test
	public void testLong() {
		long ba64 = 0;
		for (int i = 0; i < 64; i++) {
			ba64 = BitArray.setBit(ba64, i, true);
			assertTrue(BitArray.getBit(ba64, i));
			ba64 = BitArray.setBit(ba64, i, false);
			assertFalse(BitArray.getBit(ba64, i));
		}
	}

	@Test
	public void testInt() {
		int ba32 = 0;
		for (int i = 0; i < 32; i++) {
			ba32 = BitArray.setBit(ba32, i, true);
			assertTrue(BitArray.getBit(ba32, i));
			ba32 = BitArray.setBit(ba32, i, false);
			assertFalse(BitArray.getBit(ba32, i));
		}
	}

	@Test
	public void testShort() {
		short ba16 = 0;
		for (int i = 0; i < 16; i++) {
			ba16 = BitArray.setBit(ba16, i, true);
			assertTrue(BitArray.getBit(ba16, i));
			ba16 = BitArray.setBit(ba16, i, false);
			assertFalse(BitArray.getBit(ba16, i));
		}
	}

	@Test
	public void testByte() {
		byte ba8 = 0;
		for (int i = 0; i < 8; i++) {
			ba8 = BitArray.setBit(ba8, i, true);
			assertTrue(BitArray.getBit(ba8, i));
			ba8 = BitArray.setBit(ba8, i, false);
			assertFalse(BitArray.getBit(ba8, i));
		}
	}

}
