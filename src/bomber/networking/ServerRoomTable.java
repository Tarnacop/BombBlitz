package bomber.networking;

import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServerRoomTable wraps two maps which map Integer(ID of room) and String(name
 * of room) to ServerClientInfo respectively
 */
public class ServerRoomTable {
	// private ConcurrentHashMap<String, ServerRoom> nameTable = new
	// ConcurrentHashMap<String, ServerRoom>();
	private ConcurrentHashMap<Integer, ServerRoom> idTable = new ConcurrentHashMap<Integer, ServerRoom>();
	private final int maxTableSize;

	/**
	 * Construct a ServerRoomTable with maximum capacity of tableSize
	 * 
	 * @param tableSize
	 *            the maximum capacity of the table
	 */
	public ServerRoomTable(int maxTableSize) {
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
	 * Add a room to the table. Neither the room itself nor the name of the room
	 * can be null. The ID of the room will be chosen by this table
	 * automatically. A room will not be added when the size of the table
	 * reaches maxTableSize
	 * 
	 * @param room
	 *            the room to be added
	 */
	public synchronized void put(ServerRoom room) {
		if (idTable.size() >= maxTableSize) {
			return;
		}

		int id = nextAvailableID();
		if (id < 0) {
			return;
		}

		if (room == null || room.getName() == null) {
			return;
		}

		room.setID(id);
		// nameTable.put(room.getName(), room);
		idTable.put(room.getID(), room);

	}

	/**
	 * Remove a room from the table
	 * 
	 * @param key
	 *            the name of the room to be removed
	 */
	// public synchronized void remove(String key) {
	// ServerRoom room = nameTable.get(key);
	// if (room == null) {
	// return;
	// }
	//
	// int id = room.getID();
	//
	// nameTable.remove(key);
	// idTable.remove(id);
	// }

	/**
	 * Remove a room from the table
	 * 
	 * @param key
	 *            the ID of the room to be removed
	 */
	public synchronized void remove(int key) {
		ServerRoom room = idTable.get(key);
		if (room == null) {
			return;
		}

		// String name = room.getName();

		// nameTable.remove(name);
		idTable.remove(key);
	}

	/**
	 * Returns the number of rooms in the table
	 * 
	 * @return
	 */
	public int size() {
		return idTable.size();
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key
	 * 
	 * @param key
	 *            the name of the room
	 * @return the room or null when the room doesn't exist
	 */
	// public ServerRoom get(String key) {
	// return nameTable.get(key);
	// }

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key
	 * 
	 * @param key
	 *            the ID of the room
	 * @return the room or null when the room doesn't exist
	 */
	public ServerRoom get(int key) {
		return idTable.get(key);
	}

	/**
	 * Returns a Set view of the mappings contained in this map
	 * 
	 * @return the set
	 */
	// public Set<Entry<String, ServerRoom>> entrySet() {
	// return nameTable.entrySet();
	// }

	/**
	 * Returns a Set view of the mappings contained in this map
	 * 
	 * @return the set
	 */
	public Set<Entry<Integer, ServerRoom>> entrySet2() {
		return idTable.entrySet();
	}

	/**
	 * Returns true if the table contains the room
	 * 
	 * @param room
	 *            the room
	 * @return true if the table contains the room
	 */
	public boolean contains(ServerRoom room) {
		if (room == null) {
			return false;
		}

		return idTable.containsValue(room);
	}

	/**
	 * Returns true if the table contains a room with the name
	 * 
	 * @param name
	 *            the name of the room
	 * @return true if the table contains the room
	 */
	// public boolean contains(String name) {
	// return nameTable.containsKey(name);
	// }

	/**
	 * Returns true if the table contains a room with the id
	 * 
	 * @param id
	 *            the ID of the room
	 * @return true if the table contains the room
	 */
	public boolean contains(int id) {
		return idTable.containsKey(id);
	}

}