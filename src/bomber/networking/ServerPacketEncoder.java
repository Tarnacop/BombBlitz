package bomber.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 * Methods for encoding and decoding more complex packets for the server
 *
 */
public class ServerPacketEncoder {
	/**
	 * Encode List of ClientServerPlayer from ServerClientTable in
	 * MSG_S_LOBBY_PLAYERLIST format. The first three bytes in the destination
	 * byte array are reserved for message type and sequence number and will not
	 * be overwritten by this method. The caller should set the first three
	 * bytes properly before sending.
	 * 
	 * @param table
	 *            the client table
	 * @param dest
	 *            the destination byte array that will be sent over the network
	 * @return the number of bytes of data written to the byte array
	 * @throws IOException
	 */
	public static int encodePlayerList(ServerClientTable table, byte[] dest) throws IOException {
		if (table == null || dest == null) {
			throw new IOException("table or dest is null");
		}

		// length required before array of player id and name pairs
		int len = 1 + 2 + 4 + 4 + 4 + 4;
		if (dest.length < len) {
			throw new IOException("dest is too short");
		}

		// wrap byte array into a ByteBuffer
		ByteBuffer buffer = ByteBuffer.wrap(dest);
		// turn table into an ArrayList
		ArrayList<Entry<SocketAddress, ServerClientInfo>> list = new ArrayList<Entry<SocketAddress, ServerClientInfo>>(
				table.entrySet());
		// sort the ArrayList by player id
		list.sort(new Comparator<Entry<SocketAddress, ServerClientInfo>>() {
			@Override
			public int compare(Entry<SocketAddress, ServerClientInfo> o1, Entry<SocketAddress, ServerClientInfo> o2) {
				return o1.getValue().getID() - o2.getValue().getID();
			}
		});

		// put the 4 integers
		buffer.putInt(3, list.size());
		buffer.putInt(7, 0);
		buffer.putInt(11, 0);
		buffer.putInt(15, list.size());

		// put id and names into byte array
		buffer.position(1 + 2 + 4 + 4 + 4 + 4);
		for (Entry<SocketAddress, ServerClientInfo> e : list) {
			if (dest.length < buffer.position() + 4) {
				throw new IOException("dest is too short");
			}
			buffer.putInt(e.getValue().getID());

			byte[] nameData = e.getValue().getName().getBytes("UTF-8");
			if (dest.length < buffer.position() + 1 + nameData.length) {
				throw new IOException("dest is too short");
			}
			buffer.put((byte) nameData.length);
			buffer.put(nameData);
		}

		len = buffer.position();
		return len;
	}

	/**
	 * Encode List of ClientServerRoom from ServerRoomTable in
	 * MSG_S_LOBBY_ROOMLIST format. The first three bytes in the destination
	 * byte array are reserved for message type and sequence number and will not
	 * be overwritten by this method. The caller should set the first three
	 * bytes properly before sending.
	 * 
	 * @param table
	 *            the room table
	 * @param dest
	 *            the destination byte array that will be sent over the network
	 * @return the number of bytes of data written to the byte array
	 * @throws IOException
	 */
	public static int encodeRoomList(ServerRoomTable table, byte[] dest) throws IOException {
		if (table == null || dest == null) {
			throw new IOException("table or dest is null");
		}

		// length required before actual array of room info
		int len = 1 + 2 + 4 + 4 + 4 + 4;
		if (dest.length < len) {
			throw new IOException("dest is too short");
		}

		// wrap byte array into a ByteBuffer
		ByteBuffer buffer = ByteBuffer.wrap(dest);
		// turn table into an ArrayList
		ArrayList<Entry<Integer, ServerRoom>> list = new ArrayList<Entry<Integer, ServerRoom>>(table.entrySet2());
		// sort the ArrayList by player id
		list.sort(new Comparator<Entry<Integer, ServerRoom>>() {
			@Override
			public int compare(Entry<Integer, ServerRoom> o1, Entry<Integer, ServerRoom> o2) {
				return o1.getValue().getID() - o2.getValue().getID();
			}
		});

		// put the 4 integers
		buffer.putInt(3, list.size());
		buffer.putInt(7, 0);
		buffer.putInt(11, 0);
		buffer.putInt(15, list.size());

		// put room info into byte array
		buffer.position(1 + 2 + 4 + 4 + 4 + 4);
		for (Entry<Integer, ServerRoom> e : list) {
			// put room ID
			if (dest.length < buffer.position() + 4) {
				throw new IOException("dest is too short");
			}
			buffer.putInt(e.getValue().getID());

			// put room name
			byte[] nameData = e.getValue().getName().getBytes("UTF-8");
			if (dest.length < buffer.position() + 1 + nameData.length) {
				throw new IOException("dest is too short");
			}
			buffer.put((byte) nameData.length);
			buffer.put(nameData);

			// put player number
			if (dest.length < buffer.position() + 1) {
				throw new IOException("dest is too short");
			}
			buffer.put((byte) e.getValue().getPlayerNumber());

			// put max player limit
			if (dest.length < buffer.position() + 1) {
				throw new IOException("dest is too short");
			}
			buffer.put((byte) e.getValue().getMaxPlayer());

			// put inGame boolean flag
			if (dest.length < buffer.position() + 1) {
				throw new IOException("dest is too short");
			}
			byte inGame;
			if (e.getValue().isInGame()) {
				inGame = 1;
			} else {
				inGame = 0;
			}
			buffer.put(inGame);

			// put game map id
			if (dest.length < buffer.position() + 4) {
				throw new IOException("dest is too short");
			}
			int mapID = e.getValue().getMapID();
			buffer.putInt(mapID);

		}

		len = buffer.position();
		return len;
	}

	// TODO Tests
	public static void main(String[] args) {

		// player list test
		ServerClientTable clientTable = new ServerClientTable(32);

		for (int i = 0; i < 32; i++) {
			SocketAddress sockAddr = new InetSocketAddress("12.12.12." + i, 1221);
			ServerClientInfo client = new ServerClientInfo(sockAddr, "client " + i);
			clientTable.put(client);
		}

		byte[] arr = new byte[2000];
		int ret = 0;
		try {
			ret = encodePlayerList(clientTable, arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("encodePlayerList returns " + ret);

		// System.out.println(ServerThread.toHex(arr, ret));

		List<ClientServerPlayer> playerList = null;
		try {
			playerList = ClientPacketEncoder.decodePlayerList(arr, ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("decodePlayerList:");
		for (ClientServerPlayer p : playerList) {
			System.out.printf("ID: %d, Name: %s\n", p.getID(), p.getName());
		}

		// room list test
		ServerRoomTable roomTable = new ServerRoomTable(32);

		for (int i = 0; i < 32; i++) {
			ServerRoom room = new ServerRoom("test_room " + i, clientTable.get(i), i);
			roomTable.put(room);
		}

		try {
			ret = encodeRoomList(roomTable, arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("encodeRoomList returns " + ret);

		List<ClientServerLobbyRoom> roomList = null;
		try {
			roomList = ClientPacketEncoder.decodeRoomList(arr, ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("decodeRoomList:");
		for (ClientServerLobbyRoom r : roomList) {
			System.out.printf("ID: %d, Name: %s, Number of players: %d, Max players: %d, inGame: %b, Map ID: %d\n",
					r.getID(), r.getName(), r.getPlayerNumber(), r.getMaxPlayer(), r.isInGame(), r.getMapID());
		}
	}
}
