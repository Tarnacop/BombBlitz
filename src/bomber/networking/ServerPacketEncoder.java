package bomber.networking;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import bomber.AI.AIDifficulty;
import bomber.game.AudioEvent;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.KeyboardState;
import bomber.game.Map;
import bomber.game.Movement;
import bomber.game.Player;

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
	 * @return the number of bytes of the encoded data in the byte array
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
	 * @return the number of bytes of the encoded data in the byte array
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

	/**
	 * Encode ServerRoom into MSG_S_ROOM_ROOMINFO format. The first three bytes
	 * in the destination byte array are reserved for message type and sequence
	 * number and will not be overwritten by this method. The caller should set
	 * the first three bytes properly before sending.
	 * 
	 * @param room
	 *            the room
	 * @param dest
	 *            the destination byte array that will be sent over the network
	 * @return the number of bytes of the encoded data in the byte array
	 * @throws IOException
	 */
	public static int encodeRoom(ServerRoom room, byte[] dest) throws IOException {
		if (room == null || dest == null) {
			throw new IOException("room or dest is null");
		}

		// length required before room name
		int len = 1 + 2 + 4;
		if (dest.length < len) {
			throw new IOException("dest is too short");
		}

		// wrap byte array into a ByteBuffer
		ByteBuffer buffer = ByteBuffer.wrap(dest);

		// put room id into byte array
		buffer.position(3);
		buffer.putInt(room.getID());

		// put room name into byte array
		byte[] roomNameData = room.getName().getBytes("UTF-8");
		if (dest.length < buffer.position() + 1 + roomNameData.length) {
			throw new IOException("dest is too short");
		}
		buffer.put((byte) roomNameData.length);
		buffer.put(roomNameData);

		/*
		 * put number of human players, AI players, max players, inGame flag and
		 * map ID
		 */
		if (dest.length < buffer.position() + 1 + 1 + 1 + 1 + 4) {
			throw new IOException("dest is too short");
		}
		buffer.put((byte) room.getHumanPlayerNumber());
		buffer.put((byte) room.getAIPlayerNumber());
		buffer.put((byte) room.getMaxPlayer());
		if (room.isInGame()) {
			buffer.put((byte) 1);
		} else {
			buffer.put((byte) 0);
		}
		buffer.putInt(room.getMapID());

		// put human player info
		ServerClientInfo[] humanPlayers = room.getHumanPlayers();
		for (int i = 0; i < humanPlayers.length; i++) {
			ServerClientInfo player = humanPlayers[i];
			byte[] nameData = player.getName().getBytes("UTF-8");
			if (dest.length < buffer.position() + 4 + 1 + nameData.length + 1) {
				throw new IOException("dest is too short");
			}
			// put player id
			buffer.putInt(player.getID());
			// put player name
			buffer.put((byte) nameData.length);
			buffer.put(nameData);
			// put ready to play flag
			if (player.isReadyToPlay()) {
				buffer.put((byte) 1);
			} else {
				buffer.put((byte) 0);
			}
		}

		// put AI player info
		ServerAI[] aiPlayers = room.getAIPlayers();
		for (int i = 0; i < aiPlayers.length; i++) {
			ServerAI ai = aiPlayers[i];
			byte id = ai.getID();
			byte difficulty = aiDifficultyToByte(ai.getDifficulty());

			if (dest.length < buffer.position() + 1 + 1) {
				throw new IOException("dest is too short");
			}
			// put AI id
			buffer.put(id);
			// put AI difficulty
			buffer.put(difficulty);
		}

		len = buffer.position();
		return len;
	}

	/**
	 * Encode GameState into MSG_S_ROOM_GAMESTATE format. The first three bytes
	 * in the destination byte array are reserved for message type and sequence
	 * number and will not be overwritten by this method. The caller should set
	 * the first three bytes properly before sending.
	 * 
	 * @param gameState
	 *            the GameState object
	 * @param roomID
	 *            the ID of the room in which the game is taking place
	 * @param dest
	 *            the destination byte array that will be sent over the network
	 * @return the number of bytes of the encoded data in the byte array
	 * @throws IOException
	 */
	/**
	 * @param gameState
	 * @param roomID
	 * @param dest
	 * @return
	 * @throws IOException
	 */
	public static int encodeGameState(GameState gameState, int roomID, byte[] dest) throws IOException {
		if (gameState == null || dest == null) {
			throw new IOException("table or dest is null");
		}

		// null check
		byte gridMapWidth;
		byte gridMapHeight = -1;
		Block[][] gridMap;

		byte numPlayer;
		List<Player> playerList;

		byte numBomb;
		List<Bomb> bombList;

		byte numAudioEvent;
		List<AudioEvent> audioEventList;

		Map map = gameState.getMap();
		if (map == null) {
			throw new IOException("map is null");
		}

		gridMap = map.getGridMap();
		if (gridMap == null) {
			throw new IOException("gridMap is null");
		}

		if (gridMap.length < 1 || gridMap.length > 16) {
			throw new IOException("gridMap width is not in the range [1,16]");
		}
		gridMapWidth = (byte) gridMap.length;

		for (int i = 0; i < gridMap.length - 1; i++) {
			Block[] column = gridMap[i];
			Block[] columnNext = gridMap[i + 1];

			if (column == null || columnNext == null) {
				throw new IOException("gridMap has null column");
			}

			if (column.length != columnNext.length) {
				throw new IOException("gridMap has inconsistent column length");
			}

			for (Block b : column) {
				if (b == null) {
					throw new IOException("gridMap has null block");
				}
			}

			for (Block b : columnNext) {
				if (b == null) {
					throw new IOException("gridMap has null block");
				}
			}

			gridMapHeight = (byte) column.length;
		}

		if (gridMapHeight < 1 || gridMapHeight > 16) {
			throw new IOException("gridMap height is not in the range [1,16]");
		}

		playerList = gameState.getPlayers();
		if (playerList == null) {
			throw new IOException("playerList is null");
		}

		if (playerList.size() < 1 || playerList.size() > Byte.MAX_VALUE) {
			throw new IOException("playerList size is not in the range [1,127]");
		}
		numPlayer = (byte) playerList.size();

		for (Player p : playerList) {
			if (p == null) {
				throw new IOException("playerList has null player");
			}

			if (p.getGridPos() == null || p.getKeyState() == null || p.getPos() == null
					|| p.getKeyState().getMovement() == null) {
				throw new IOException("player has null fields");
			}
		}

		bombList = gameState.getBombs();
		if (bombList == null) {
			throw new IOException("bombList is null");
		}

		/*
		 * if (bombList.size() < 0 || bombList.size() > Byte.MAX_VALUE) { throw
		 * new IOException("bombList size is not in the range [0,127]"); }
		 */
		if (bombList.size() > Byte.MAX_VALUE) {
			numBomb = Byte.MAX_VALUE;
		} else {
			numBomb = (byte) bombList.size();
		}

		for (Bomb b : bombList) {
			if (b == null) {
				throw new IOException("bombList has null bomb");
			}

			if (b.getGridPos() == null || b.getPos() == null) {
				throw new IOException("bomb has null position");
			}
		}

		audioEventList = gameState.getAudioEvents();
		if (audioEventList == null) {
			throw new IOException("audioEventList is null");
		}

		/*
		 * if (audioEventList.size() < 0 || audioEventList.size() > 16) { throw
		 * new IOException("audioEventList size is not in the range [0,16]"); }
		 */
		if (audioEventList.size() > 16) {
			numAudioEvent = 16;
		} else {
			numAudioEvent = (byte) audioEventList.size();
		}

		for (AudioEvent e : audioEventList) {
			if (e == null) {
				throw new IOException("audioEventList has null audio event");
			}
		}

		// length calculation
		int dataLength = 7 + 130 + 1 + 35 * numPlayer + 1 + 20 * numBomb + 2;
		if (dataLength > dest.length) {
			throw new IOException("dest is too short to fit " + dataLength + " bytes");
		}

		ByteBuffer buffer = ByteBuffer.wrap(dest);

		// Header 7 Bytes
		buffer.position(3);
		buffer.putInt(roomID);

		// Map (130 Bytes)
		buffer.put(gridMapWidth);
		buffer.put(gridMapHeight);

		long bitArr[][] = new long[4][4];
		for (int y = 0; y < gridMapHeight; y++) {
			for (int x = 0; x < gridMapWidth; x++) {

				Block b = gridMap[x][y];

				byte bits = blockToByte(b);

				int bitIndex = x + y * gridMapWidth;

				for (int i = 3; i >= 0; i--) {
					if (BitArray.getBit(bits, i)) {
						bitArr[i][bitIndex / 64] = BitArray.setBit(bitArr[i][bitIndex / 64], bitIndex % 64, true);
					}
				}

			}
		}

		for (int b = 3; b >= 0; b--) {
			for (int i = 3; i >= 0; i--) {
				buffer.putLong(bitArr[b][i]);
			}
		}

		// Players (1 + numPlayer * 35 Bytes)
		buffer.put(numPlayer);

		for (int i = 0; i < numPlayer; i++) {
			Player p = playerList.get(i);

			KeyboardState k = p.getKeyState();
			Movement movement = k.getMovement();
			boolean bomb = k.isBomb();
			short keyState = 0;
			if (movement == Movement.NONE) {
				keyState = BitArray.setBit(keyState, 0, true);
			} else if (movement == Movement.UP) {
				keyState = BitArray.setBit(keyState, 1, true);
			} else if (movement == Movement.DOWN) {
				keyState = BitArray.setBit(keyState, 2, true);
			} else if (movement == Movement.LEFT) {
				keyState = BitArray.setBit(keyState, 3, true);
			} else if (movement == Movement.RIGHT) {
				keyState = BitArray.setBit(keyState, 4, true);
			}
			if (bomb) {
				keyState = BitArray.setBit(keyState, 5, true);
			}

			byte alive;
			if (p.isAlive()) {
				alive = 1;
			} else {
				alive = 0;
			}

			buffer.putInt(p.getPlayerID());
			buffer.putInt(p.getPos().x);
			buffer.putInt(p.getPos().y);
			buffer.putInt(p.getLives());
			buffer.putDouble(p.getSpeed());
			buffer.putInt(p.getBombRange());
			buffer.putInt(p.getMaxNrOfBombs());
			buffer.putShort(keyState);
			buffer.put(alive);
		}

		// Bombs (1 + numBomb * 20)
		buffer.put(numBomb);

		for (int i = 0; i < numBomb; i++) {
			Bomb b = bombList.get(i);

			buffer.putInt(b.getPlayerID());
			buffer.putInt(b.getPos().x);
			buffer.putInt(b.getPos().y);
			buffer.putInt(b.getTime());
			buffer.putInt(b.getRadius());
		}

		// Audio Events
		short audioState = 0;

		for (int i = 0; i < numAudioEvent; i++) {
			AudioEvent e = audioEventList.get(i);

			if (e == AudioEvent.PLACE_BOMB) {
				audioState = BitArray.setBit(audioState, 0, true);
			} else if (e == AudioEvent.EXPLOSION) {
				audioState = BitArray.setBit(audioState, 1, true);
			} else if (e == AudioEvent.PLAYER_DEATH) {
				audioState = BitArray.setBit(audioState, 2, true);
			} else if (e == AudioEvent.MOVEMENT) {
				audioState = BitArray.setBit(audioState, 3, true);
			} else if (e == AudioEvent.POWERUP) {
				audioState = BitArray.setBit(audioState, 4, true);
			}
		}

		buffer.putShort(audioState);

		// final length check
		if (buffer.position() != dataLength) {
			throw new IOException("BUG: inconsistency between calculated length and byte buffer position");
		}

		return dataLength;
	}

	/**
	 * Convert short into KeyboardState
	 * 
	 * @param k
	 *            the short
	 * @return the KeyboardState
	 */
	public static KeyboardState shortToKeyboardState(short k) {
		KeyboardState keyboardState = new KeyboardState();
		keyboardState.setMovement(Movement.NONE);
		keyboardState.setBomb(false);

		if (BitArray.getBit(k, 0)) {
			keyboardState.setMovement(Movement.NONE);
		} else if (BitArray.getBit(k, 1)) {
			keyboardState.setMovement(Movement.UP);
		} else if (BitArray.getBit(k, 2)) {
			keyboardState.setMovement(Movement.DOWN);
		} else if (BitArray.getBit(k, 3)) {
			keyboardState.setMovement(Movement.LEFT);
		} else if (BitArray.getBit(k, 4)) {
			keyboardState.setMovement(Movement.RIGHT);
		}
		keyboardState.setBomb(BitArray.getBit(k, 5));

		return keyboardState;
	}

	/**
	 * Convert Block into byte (only bit 3 to bit 0 in the byte are used)
	 * 
	 * @param block
	 *            the Block
	 * @return the byte
	 */
	public static byte blockToByte(Block block) {
		byte b = 0;

		switch (block) {
		case BLANK:
			b = 0;
			break;

		case SOLID:
			b = 1;
			break;

		case SOFT:
			b = 2;
			break;

		case BLAST:
			b = 3;
			break;

		case PLUS_BOMB:
			b = 4;
			break;

		case MINUS_BOMB:
			b = 5;
			break;

		case PLUS_RANGE:
			b = 6;
			break;

		case MINUS_RANGE:
			b = 7;
			break;

		case PLUS_SPEED:
			b = 8;
			break;

		case MINUS_SPEED:
			b = 9;
			break;

		default:
			b = 0;
			break;
		}

		return b;
	}

	/**
	 * Convert AIDifficulty into byte
	 * 
	 * @param difficulty
	 *            the AIDifficulty
	 * @return the byte
	 */
	public static byte aiDifficultyToByte(AIDifficulty difficulty) {
		byte aiDifficulty;
		switch (difficulty) {
		case EASY:
			aiDifficulty = ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_EASY;
			break;
		case MEDIUM:
			aiDifficulty = ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_MEDIUM;
			break;
		case HARD:
			aiDifficulty = ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_HARD;
			break;
		case EXTREME:
			aiDifficulty = ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_EXTREME;
			break;
		default:
			aiDifficulty = ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_MEDIUM;
			break;
		}
		return aiDifficulty;
	}
}
