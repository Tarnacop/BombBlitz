package bomber.networking;

public class ProtocolConstant {
	// Protocol constants and documentation

	/*
	 * All input is evil. Do not assume every received packet has valid format.
	 * Always perform format and bounds checking
	 */

	/*
	 * All integers transmitted over the network should be in network byte order
	 * (big-endian byte order, which is also the byte order used by Java)
	 */

	/*
	 * All strings transmitted over the network should use UTF-8 encoding. There
	 * should be a byte indicating the number of bytes of the string before the
	 * actual bytes of the string, which are NOT null terminated
	 */

	/*
	 * The first bit (most significant bit) in the message type byte is reserved
	 * for determining whether the message requires reliability and contains a
	 * sequence number (which should be acknowledged immediately by the
	 * recipient) When the bit is 1, the sequence number is a short (16-bit
	 * integer) at offset 1 When the bit is 0, the sequence number is a short of
	 * constant 0 at offset 1, which does not require acknowledgement
	 */
	// Mask for testing whether the bit is set
	public static final byte MSG_B_HASSEQUENCE = (byte) 0x80;

	// Client to Server message types
	// Range from 0x00 to 0x3f (up to 64 types of message)

	// Network connection
	public static final byte MSG_C_NET_CONNECT = 0x00; // Bit set
	/*
	 * 1 byte message type + 2 byte sequence + 1 byte name string length + name
	 * string bytes
	 */

	public static final byte MSG_C_NET_DISCONNECT = 0x01; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_C_NET_PING = 0x02; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_C_NET_ACK = 0x03; // Bit not set
	// 1 byte message type + 2 byte sequence + 2 byte acknowledged sequence

	// In lobby
	public static final byte MSG_C_LOBBY_GETPLAYERLIST = 0x04;
	public static final byte MSG_C_LOBBY_GETROOMLIST = 0x05;
	public static final byte MSG_C_LOBBY_GETLEADERBOARD = 0x06;
	public static final byte MSG_C_LOBBY_CREATEROOM = 0x07;
	public static final byte MSG_C_LOBBY_JOINROOM = 0x08;
	public static final byte MSG_C_LOBBY_GETROOMINFO = 0x09;
	public static final byte MSG_C_LOBBY_SENDTEXT = 0x0a;

	// In room
	public static final byte MSG_C_ROOM_LEAVE = 0x0b;
	public static final byte MSG_C_ROOM_SETINFO = 0x0c;
	public static final byte MSG_C_ROOM_GETINFO = 0x0d;
	public static final byte MSG_C_ROOM_SENDTEXT = 0x0e;

	// In game
	public static final byte MSG_C_GAME_LEAVE = 0x0f;
	public static final byte MSG_C_GAME_SENDMOVE = 0x10;
	public static final byte MSG_C_GAME_SENDTEXT = 0x11;

	// Server to Client message types
	// Range from 0x40 to 0x7f (up to 64 types of message)

	// Network connection
	public static final byte MSG_S_NET_ACCEPT = 0x40; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_S_NET_REJECT = 0x41; // Bit not set
	// 1 byte message type + 2 byte sequence
	// TODO rejection message should contain a reason: server full, duplicate
	// name

	public static final byte MSG_S_NET_ALREADYCONNECTED = 0x42; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_S_NET_NOTCONNECTED = 0x43; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_S_NET_DISCONNECTED = 0x44; // Bit not set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_S_NET_PING = 0x45; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_S_NET_ACK = 0x46; // Bit not set
	// 1 byte message type + 2 byte sequence + 2 byte acknowledged sequence

	public static final byte MSG_S_LOBBY_PLAYERLIST = 0x47;
	/*
	 * 1 byte message type + 2 byte sequence + 4 byte total number of players +
	 * 4 byte start player id + 4 byte number of players in this packet + array
	 * of player IDs and name pairs(4 byte id + 1 byte name length + bytes of
	 * name string)
	 */
	// TODO currently start player id is hard-coded to 0 and number of players
	// in this packet to total number of players(we don't
	// expect to have the number of players one packet does not fit)

}
