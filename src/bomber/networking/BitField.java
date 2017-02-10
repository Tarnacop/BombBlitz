package bomber.networking;

public class BitField {
	public static boolean getBit(int field, int index) {
		return ((field >> index) & 1) == 1;
	}

	public static int setBit(int field, int index, boolean value) {
		if (value) {
			return field | (1 << index);
		} else {
			return field & ~(1 << index);
		}
	}
}
