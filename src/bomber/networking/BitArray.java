package bomber.networking;

/**
 * Class to change the bits in an integer
 * 
 * @author Qiyang Li
 */
public class BitArray {
	/**
	 * Get a bit from a long
	 * 
	 * @param input
	 *            the long
	 * @param index
	 *            the index of the bit
	 * @return the bit
	 */
	public static boolean getBit(long input, int index) {
		return ((input >> (long) index) & 1l) == 1l;
	}

	/**
	 * Set a bit to a long
	 * 
	 * @param input
	 *            the long
	 * @param index
	 *            the index of the bit
	 * @param value
	 *            the bit
	 * @return the updated long
	 */
	public static long setBit(long input, int index, boolean value) {
		if (value) {
			return input | (1l << (long) index);
		} else {
			return input & ~(1l << (long) index);
		}
	}

	/**
	 * Get a bit from an int
	 * 
	 * @param input
	 *            the int
	 * @param index
	 *            the index of the bit
	 * @return the bit
	 */
	public static boolean getBit(int input, int index) {
		return ((input >> index) & 1) == 1;
	}

	/**
	 * Set a bit to an int
	 * 
	 * @param input
	 *            the int
	 * @param index
	 *            the index of the bit
	 * @param value
	 *            the bit
	 * @return the updated int
	 */
	public static int setBit(int input, int index, boolean value) {
		if (value) {
			return input | (1 << index);
		} else {
			return input & ~(1 << index);
		}
	}

	/**
	 * Get a bit from from a short
	 * 
	 * @param input
	 *            the short
	 * @param index
	 *            the index of the bit
	 * @return the updated short
	 */
	public static boolean getBit(short input, int index) {
		return ((input >> index) & 1) == 1;
	}

	/**
	 * Set a bit to a short
	 * 
	 * @param input
	 *            the short
	 * @param index
	 *            the index of the bit
	 * @param value
	 *            the bit
	 * @return the updated short
	 */
	public static short setBit(short input, int index, boolean value) {
		if (value) {
			return (short) (input | (1 << index));
		} else {
			return (short) (input & ~(1 << index));
		}
	}

	/**
	 * Get a bit from a byte
	 * 
	 * @param input
	 *            the byte
	 * @param index
	 *            the index of the bit
	 * @return the updated byte
	 */
	public static boolean getBit(byte input, int index) {
		return ((input >> index) & 1) == 1;
	}

	/**
	 * Set a bit to a byte
	 * 
	 * @param input
	 *            the byte
	 * @param index
	 *            the index of the bit
	 * @param value
	 *            the bit
	 * @return the updated byte
	 */
	public static byte setBit(byte input, int index, boolean value) {
		if (value) {
			return (byte) (input | (1 << index));
		} else {
			return (byte) (input & ~(1 << index));
		}
	}

}
