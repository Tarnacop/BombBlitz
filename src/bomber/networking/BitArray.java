package bomber.networking;

public class BitArray {
	public static boolean getBit(long input, int index) {
		return ((input >> (long) index) & 1l) == 1l;
	}

	public static long setBit(long input, int index, boolean value) {
		if (value) {
			return input | (1l << (long) index);
		} else {
			return input & ~(1l << (long) index);
		}
	}

	public static boolean getBit(int input, int index) {
		return ((input >> index) & 1) == 1;
	}

	public static int setBit(int input, int index, boolean value) {
		if (value) {
			return input | (1 << index);
		} else {
			return input & ~(1 << index);
		}
	}

	public static boolean getBit(short input, int index) {
		return ((input >> index) & 1) == 1;
	}

	public static short setBit(short input, int index, boolean value) {
		if (value) {
			return (short) (input | (1 << index));
		} else {
			return (short) (input & ~(1 << index));
		}
	}

	public static boolean getBit(byte input, int index) {
		return ((input >> index) & 1) == 1;
	}

	public static byte setBit(byte input, int index, boolean value) {
		if (value) {
			return (byte) (input | (1 << index));
		} else {
			return (byte) (input & ~(1 << index));
		}
	}

	public static void main(String[] args) {
		long num = 0;
		num = setBit(num, 5, true);

		num = setBit(num, 63, true);

		for (int i = 0; i < 64; i++) {
			System.out.println(getBit(num, i));
		}
	}
}
