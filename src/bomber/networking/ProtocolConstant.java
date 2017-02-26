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
	 * sequence number. (which should be acknowledged immediately by the
	 * recipient) When the bit is 1, the sequence number is a short (16-bit
	 * integer) at offset 1. When the bit is 0, the sequence number is a short
	 * of constant 0 at offset 1 (which can be safely ignored), which does not
	 * require acknowledgement
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
	public static final byte MSG_C_LOBBY_GETPLAYERLIST = 0x04; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_C_LOBBY_GETROOMLIST = 0x05; // Bit set
	// 1 byte message type + 2 byte sequence

	// public static final byte MSG_C_LOBBY_GETLEADERBOARD = 0x06;

	public static final byte MSG_C_LOBBY_CREATEROOM = 0x07; // Bit set
	/*
	 * 1 byte message type + 2 byte sequence + 1 byte room name length + room
	 * name string + 1 byte max player limit + 4 byte map ID
	 */

	public static final byte MSG_C_LOBBY_JOINROOM = 0x08; // Bit set
	// 1 byte message type + 2 byte sequence + 4 byte room ID

	// public static final byte MSG_C_LOBBY_SENDTEXT = 0x09;

	// In room
	public static final byte MSG_C_ROOM_LEAVE = 0x0a; // Bit set
	// 1 byte message type + 2 byte sequence + 4 byte room ID

	public static final byte MSG_C_ROOM_READYTOPLAY = 0x0b; // Bit set
	/*
	 * 1 byte message type + 2 byte sequence + 4 byte room ID + 1 byte boolean
	 * readtToPlay flag
	 */

	public static final byte MSG_C_ROOM_SETINFO = 0x0c; // Bit set
	public static final byte MSG_C_ROOM_SETINFO_NAME = 0x00;
	public static final byte MSG_C_ROOM_SETINFO_MAXPLAYER = 0x01;
	public static final byte MSG_C_ROOM_SETINFO_MAPID = 0x02;
	public static final byte MSG_C_ROOM_SETINFO_AI = 0x03;
	public static final byte MSG_C_ROOM_SETINFO_AI_ADD = 0x00;
	public static final byte MSG_C_ROOM_SETINFO_AI_REMOVE = 0x01;
	public static final byte MSG_C_ROOM_SETINFO_AI_DIFFICULTY = 0x02;
	public static final byte MSG_C_ROOM_SETINFO_AI_DIFFICULTY_EASY = 0x00;
	public static final byte MSG_C_ROOM_SETINFO_AI_DIFFICULTY_MEDIUM = 0x01;
	public static final byte MSG_C_ROOM_SETINFO_AI_DIFFICULTY_HARD = 0x02;
	public static final byte MSG_C_ROOM_SETINFO_AI_DIFFICULTY_EXTREME = 0x03;

	/* Header: 1 byte message type + 2 byte sequence + 4 byte room ID */
	/* Valid format: Header + any one of the below */
	/*
	 * set room name: 1 byte constant 0x0 + 1 byte name length + bytes of room
	 * name
	 */
	/*
	 * set max number of players: 1 byte constant 0x1 + 1 byte number of players
	 */
	/*
	 * set map ID: 1 byte constant 0x2 + 4 byte map ID
	 */
	/* add AI: 1 byte constant 0x3 + 1 byte constant 0x0 */
	/* remove AI: 1 byte constant 0x3 + 1 byte constant 0x1 */
	/*
	 * set AI difficulty: 1 byte constant 0x3 + 1 byte constant 0x2 + 1 byte AI
	 * id + 1 byte AI difficulty
	 */

	// public static final byte MSG_C_ROOM_GETINFO = 0x0d;

	// public static final byte MSG_C_ROOM_SENDTEXT = 0x0e;

	// In game

	public static final byte MSG_C_GAME_SENDMOVE = 0x0f; // Bit not set
	/*
	 * 1 byte message type + 2 byte sequence + 4 byte room ID + 2 byte keyboard
	 * state
	 */

	// Server to Client message types
	// Range from 0x40 to 0x7f (up to 64 types of message)

	// Network connection
	public static final byte MSG_S_NET_ACCEPT = 0x40; // Bit set
	// 1 byte message type + 2 byte sequence + 4 byte client id

	public static final byte MSG_S_NET_REJECT = 0x41; // Bit not set
	// 1 byte message type + 2 byte sequence
	/*
	 * TODO rejection message should contain a reason: server full, duplicate
	 * name, invalid name length
	 */

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

	public static final byte MSG_S_LOBBY_PLAYERLIST = 0x47; // Bit set
	/*
	 * 1 byte message type + 2 byte sequence + 4 byte total number of players +
	 * 4 byte packet index + 4 byte max index + 4 byte number of players in this
	 * packet + array of (4 byte player id + 1 byte name length + bytes of name
	 * string)
	 */
	/*
	 * TODO currently packet index and max index are hard-coded to 0 and number
	 * of players in this packet to total number of players, since we don't
	 * expect to have the number of players one packet does not fit(32 players)
	 */
	/* TODO only player IDs and names are encoded, may add inRoom flag later */

	public static final byte MSG_S_LOBBY_ROOMLIST = 0x48; // Bit set
	/*
	 * 1 byte message type + 2 byte sequence + 4 byte total number of rooms + 4
	 * byte packet index + 4 byte max index + 4 byte number of rooms in this
	 * packet + array of (4 byte room id + 1 byte room name length + bytes of
	 * room name string + 1 byte number of players(human + AI) + 1 byte max
	 * player limit + 1 byte inGame boolean flag + 4 byte game map id)
	 */
	/*
	 * TODO max number of rooms is limited to 32 for the same reason above.
	 * Additionally, we cannot have more rooms than players since each room must
	 * have at least one player
	 */

	public static final byte MSG_S_LOBBY_ROOMACCEPT = 0x49; // Bit set
	// 1 byte message type + 2 byte sequence + 4 byte room id

	public static final byte MSG_S_LOBBY_ROOMREJECT = 0x4a; // Bit set
	// 1 byte message type + 2 byte sequence
	/*
	 * TODO rejection message should contain a reason: server full, duplicate
	 * name, invalid name length
	 */

	public static final byte MSG_S_LOBBY_NOTINROOM = 0x4b; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_S_ROOM_ALREADYINROOM = 0x4c; // Bit set
	// 1 byte message type + 2 byte sequence + 4 byte room id

	public static final byte MSG_S_ROOM_HAVELEFT = 0x4d; // Bit set
	// 1 byte message type + 2 byte sequence

	public static final byte MSG_S_ROOM_ROOMINFO = 0x4e; // Bit set
	/*
	 * This type of message should be sent upon each room info update(when a
	 * room is newly created, or a client changes the info of the room)
	 */
	/*
	 * 1 byte message type + 2 byte sequence + 4 byte room id + 1 byte name
	 * length + bytes of name string + 1 byte human player number + 1 byte AI
	 * player number + 1 byte max player limit + 1 byte inGame flag + 4 byte
	 * game map id + array of up to 4 human player info(4 byte player id + 1
	 * byte player name length + bytes of player string + 1 byte isReadyToPlay
	 * flag) + array of up to 4 AI player info(1 byte AI id + 1 byte AI
	 * difficulty)
	 */

	public static final byte MSG_S_ROOM_GAMESTART = 0x4f; // Bit set
	/*
	 * 1 byte message type + 2 byte sequence + 4 byte room id + 4 byte game map
	 * id + 1 byte map width + 1 byte map height
	 */

	public static final byte MSG_S_ROOM_GAMESTATE = 0x50; // Bit not set
	/*
	 * Header: 1 byte message type + 2 byte sequence + 4 byte room id
	 */
	/*
	 * Map: 1 byte width(in the range [1,16]) + 1 byte height(in the range
	 * [1,16]) + 32 byte bit array for bit 3 + 32 byte bit array for bit 2 + 32
	 * byte bit array for bit 1 + 32 byte bit array for bit 0
	 */
	/*
	 * Players: 1 byte number of players + array of (4 byte player id + 4 byte
	 * pixel position x + 4 byte pixel position y + 4 byte lives + 8 byte speed
	 * in double precision floating point + 4 byte bomb range + 4 byte max bombs
	 * + 2 byte keyboard state bit array + 1 byte isAlive)
	 */
	/*
	 * Bombs: 1 byte number of bombs + array of (4 byte player id + 4 byte pixel
	 * position x + 4 byte pixel position y + 4 byte time to detonate + 4 byte
	 * radius)
	 */
	/*
	 * Audio Events: 2 byte audio event bit array
	 */

	public static final byte MSG_S_ROOM_GAMEOVER = 0x51; // Bit set
	// 1 byte message type + 2 byte sequence + 4 byte room id

}
