package bomber.networking;

import java.net.SocketAddress;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServerClientTable wraps three maps which map SocketAddress, String and
 * Integer to ServerClientInfo respectively
 */
public class ServerClientTable {
	private ConcurrentHashMap<SocketAddress, ServerClientInfo> socketTable = new ConcurrentHashMap<SocketAddress, ServerClientInfo>();
	private ConcurrentHashMap<String, ServerClientInfo> nameTable = new ConcurrentHashMap<String, ServerClientInfo>();
	private ConcurrentHashMap<Integer, ServerClientInfo> idTable = new ConcurrentHashMap<Integer, ServerClientInfo>();
	private final int maxTableSize;

	/**
	 * Construct a ServerClientTable with maximum capacity of tableSize
	 * 
	 * @param tableSize
	 *            the maximum capacity of the table
	 */
	public ServerClientTable(int maxTableSize) {
		this.maxTableSize = maxTableSize;
	}

	private int nextAvailableID() {
		for (int i = 0; i < maxTableSize; i++) {
			if (!idTable.containsKey(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Add a client to the table. Neither the client itself nor the
	 * socketAddress or name of the client can be null. The ID of the client
	 * will be chosen by this table automatically. A client will not be added
	 * when the size of the table reaches maxTableSize
	 * 
	 * @param client
	 *            the client to be added
	 */
	public synchronized void put(ServerClientInfo client) {
		if (socketTable.size() >= maxTableSize) {
			return;
		}

		int id = nextAvailableID();
		if (id < 0) {
			return;
		}

		if (client == null || client.getSocketAddress() == null || client.getName() == null) {
			return;
		}

		client.setID(id);
		socketTable.put(client.getSocketAddress(), client);
		nameTable.put(client.getName(), client);
		idTable.put(client.getID(), client);

	}

	/**
	 * Remove a client from the table
	 * 
	 * @param key
	 *            the SocketAddress of the client to be removed
	 */
	public synchronized void remove(SocketAddress key) {
		ServerClientInfo clientInfo = socketTable.get(key);
		if (clientInfo == null) {
			return;
		}

		String name = clientInfo.getName();
		int id = clientInfo.getID();

		socketTable.remove(key);
		nameTable.remove(name);
		idTable.remove(id);
	}

	/**
	 * Remove a client from the table
	 * 
	 * @param key
	 *            the name of the client to be removed
	 */
	public synchronized void remove(String key) {
		ServerClientInfo clientInfo = nameTable.get(key);
		if (clientInfo == null) {
			return;
		}

		SocketAddress socketAddress = clientInfo.getSocketAddress();
		int id = clientInfo.getID();

		socketTable.remove(socketAddress);
		nameTable.remove(key);
		idTable.remove(id);
	}

	/**
	 * Remove a client from the table
	 * 
	 * @param key
	 *            the ID of the client to be removed
	 */
	public synchronized void remove(int key) {
		ServerClientInfo clientInfo = idTable.get(key);
		if (clientInfo == null) {
			return;
		}

		SocketAddress socketAddress = clientInfo.getSocketAddress();
		String name = clientInfo.getName();

		socketTable.remove(socketAddress);
		nameTable.remove(name);
		idTable.remove(key);
	}

	/**
	 * Returns the number of clients in the table
	 * 
	 * @return
	 */
	public int size() {
		return socketTable.size();
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key
	 * 
	 * @param key
	 *            the SocketAddress of the client
	 * @return the client or null when the client doesn't exist
	 */
	public ServerClientInfo get(SocketAddress key) {
		return socketTable.get(key);
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key
	 * 
	 * @param key
	 *            the name of the client
	 * @return the client or null when the client doesn't exist
	 */
	public ServerClientInfo get(String key) {
		return nameTable.get(key);
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key
	 * 
	 * @param key
	 *            the ID of the client
	 * @return the client or null when the client doesn't exist
	 */
	public ServerClientInfo get(int key) {
		return idTable.get(key);
	}

	/**
	 * Returns a Set view of the mappings contained in this map
	 * 
	 * @return the set
	 */
	public Set<Entry<SocketAddress, ServerClientInfo>> entrySet() {
		return socketTable.entrySet();
	}

	/**
	 * Returns true if the table contains the client
	 * 
	 * @param serverClientInfo
	 *            the client
	 * @return true if the table contains the client
	 */
	public boolean contains(ServerClientInfo serverClientInfo) {
		return socketTable.containsValue(serverClientInfo);
	}

	/**
	 * Returns true if the table contains a client with the socketAddress
	 * 
	 * @param socketAddress
	 *            the SocketAddress of the client
	 * @return true if the table contains the client
	 */
	public boolean contains(SocketAddress socketAddress) {
		return socketTable.containsKey(socketAddress);
	}

	/**
	 * Returns true if the table contains a client with the name
	 * 
	 * @param name
	 *            the name of the client
	 * @return true if the table contains the client
	 */
	public boolean contains(String name) {
		return nameTable.containsKey(name);
	}

	/**
	 * Returns true if the table contains a client with the id
	 * 
	 * @param id
	 *            the ID of the client
	 * @return true if the table contains the client
	 */
	public boolean contains(int id) {
		return idTable.containsKey(id);
	}

}
