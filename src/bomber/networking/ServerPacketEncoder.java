package bomber.networking;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map.Entry;

/**
 * 
 * Methods for encoding and decoding more complex packets for the server
 *
 */
public class ServerPacketEncoder {
	/**
	 * Encode player list from table to bytes. The first three bytes are
	 * reserved for message type and sequence number and will not be
	 * overwritten. The caller should set the first three bytes properly before
	 * sending.
	 * 
	 * @param table
	 *            the client table
	 * @param dest
	 *            the destination byte array that will be sent over the network
	 * @return the number of bytes of data written to the byte array, or -1 on
	 *         error
	 */
	public static int encodePlayerList(ServerClientTable table, byte[] dest) {
		if (table == null || dest == null) {
			return -1;
		}

		int len = 0;

		// TODO unfinished
		System.out.println(table.size());
		len += 4;
		for (Entry<SocketAddress, ServerClientInfo> e : table.entrySet()) {
			System.out.println(e.getValue().getID());
			len += 4;
			System.out.println(e.getValue().getName());
			len += 1;
			len += e.getValue().getName().length();
		}

		return len;
	}

	public static void main(String[] args) {

		// TODO test
		ServerClientTable table = new ServerClientTable(64);

		for (int i = 0; i < 64; i++) {
			SocketAddress sockAddr = new InetSocketAddress("12.12.12." + i, 1221);
			ServerClientInfo client = new ServerClientInfo(sockAddr, "client " + i);
			table.put(client);
		}

		byte[] arr = new byte[500];
		int ret = encodePlayerList(table, arr);
		System.out.println("encodePlayerList returns " + ret);

	}
}
