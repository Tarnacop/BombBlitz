package bomber.networking;

import java.awt.Point;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
 * Methods for encoding and decoding more complex packets for the client
 * 
 * @author Qiyang Li
 */
public class ClientPacketEncoder {

	/**
	 * Decode List of ClientServerPlayer from bytes in MSG_S_LOBBY_PLAYERLIST
	 * format. The first three bytes in the destination byte array are reserved
	 * for message type and sequence number and will be ignored. The caller
	 * should ensure the first three bytes are correct before calling this
	 * method.
	 * 
	 * @param src
	 *            the source byte array
	 * @param length
	 *            the length of the data in the byte array
	 * @return a list of ClientServerPlayer
	 * @throws IOException
	 */
	public static List<ClientServerPlayer> decodePlayerList(byte[] src, int length) throws IOException {
		if (src == null) {
			throw new IOException("src is null");
		}

		if (length > src.length) {
			throw new IOException("length is invalid");
		}

		if (length < 1 + 2 + 4 + 4 + 4 + 4) {
			throw new IOException("packet format is invalid");
		}

		ByteBuffer buffer = ByteBuffer.wrap(src, 0, length);
		buffer.position(3);

		// int totalPlayers = buffer.getInt();
		// int packetIndex = buffer.getInt();
		// int maxIndex = buffer.getInt();
		buffer.position(15);
		int numPlayers = buffer.getInt();

		buffer.position(19);
		List<ClientServerPlayer> playerList = new ArrayList<ClientServerPlayer>();
		for (int i = 0; i < numPlayers; i++) {
			if (length < buffer.position() + 4) {
				throw new IOException("packet format is invalid");
			}
			int id = buffer.getInt();

			if (length < buffer.position() + 1) {
				throw new IOException("packet format is invalid");
			}
			byte nameLength = buffer.get();

			if (nameLength < 1 || length < buffer.position() + nameLength) {
				throw new IOException("packet format is invalid");
			}
			byte[] nameData = new byte[nameLength];
			buffer.get(nameData);
			String name = new String(nameData, 0, nameLength, "UTF-8");

			if (length < buffer.position() + 1) {
				throw new IOException("packet format is invalid");
			}
			boolean inRoom = buffer.get() != 0;

			int roomID = -1;
			if (inRoom) {
				if (length < buffer.position() + 4) {
					throw new IOException("packet format is invalid");
				}
				roomID = buffer.getInt();
			}

			playerList.add(new ClientServerPlayer(id, name, inRoom, roomID));
		}

		if (length != buffer.position()) {
			throw new IOException("packet format is invalid");
		}

		return playerList;
	}

	/**
	 * Decode List of ClientServerLobbyRoom from bytes in MSG_S_LOBBY_ROOMLIST
	 * format. The first three bytes in the destination byte array are reserved
	 * for message type and sequence number and will be ignored. The caller
	 * should ensure the first three bytes are correct before calling this
	 * method.
	 * 
	 * @param src
	 *            the source byte array
	 * @param length
	 *            the length of the data in the byte array
	 * @return a list of ClientServerLobbyRoom
	 * @throws IOException
	 */
	public static List<ClientServerLobbyRoom> decodeRoomList(byte[] src, int length) throws IOException {
		if (src == null) {
			throw new IOException("src is null");
		}

		if (length > src.length) {
			throw new IOException("length is invalid");
		}

		if (length < 1 + 2 + 4 + 4 + 4 + 4) {
			throw new IOException("packet format is invalid");
		}

		ByteBuffer buffer = ByteBuffer.wrap(src, 0, length);
		buffer.position(3);

		// int totalPlayers = buffer.getInt();
		// int packetIndex = buffer.getInt();
		// int maxIndex = buffer.getInt();
		buffer.position(15);
		int numRooms = buffer.getInt();

		buffer.position(19);
		List<ClientServerLobbyRoom> roomList = new ArrayList<ClientServerLobbyRoom>();
		for (int i = 0; i < numRooms; i++) {
			// get room id
			if (length < buffer.position() + 4) {
				throw new IOException("packet format is invalid");
			}
			int id = buffer.getInt();

			// get room name length
			if (length < buffer.position() + 1) {
				throw new IOException("packet format is invalid");
			}
			byte nameLength = buffer.get();

			// get room name
			if (nameLength < 1 || length < buffer.position() + nameLength) {
				throw new IOException("packet format is invalid");
			}
			byte[] nameData = new byte[nameLength];
			buffer.get(nameData);
			String name = new String(nameData, 0, nameLength, "UTF-8");

			// get player number
			if (length < buffer.position() + 1) {
				throw new IOException("packet format is invalid");
			}
			byte playerNumber = buffer.get();

			// get max player limit
			if (length < buffer.position() + 1) {
				throw new IOException("packet format is invalid");
			}
			byte maxPlayer = buffer.get();

			// get inGame boolean flag
			if (length < buffer.position() + 1) {
				throw new IOException("packet format is invalid");
			}
			boolean inGame = false;
			if (buffer.get() == 1) {
				inGame = true;
			}

			// get game map ID
			if (length < buffer.position() + 4) {
				throw new IOException("packet format is invalid");
			}
			int mapID = buffer.getInt();

			// get number of human players
			if (length < buffer.position() + 1) {
				throw new IOException("packet format is invalid");
			}
			byte humanPlayerNumber = buffer.get();

			// get id of human players
			if (humanPlayerNumber < 0 || length < buffer.position() + humanPlayerNumber * 4) {
				throw new IOException("packet format is invalid");
			}
			int[] humanPlayerID = new int[humanPlayerNumber];
			for (int j = 0; j < humanPlayerNumber; j++) {
				humanPlayerID[j] = buffer.getInt();
			}

			roomList.add(new ClientServerLobbyRoom(id, name, playerNumber, maxPlayer, inGame, mapID, humanPlayerID));
		}

		if (length != buffer.position()) {
			throw new IOException("packet format is invalid");
		}

		return roomList;
	}

	/**
	 * Decode ClientServerRoom from bytes in MSG_S_ROOM_ROOMINFO format. The
	 * first three bytes in the destination byte array are reserved for message
	 * type and sequence number and will be ignored. The caller should ensure
	 * the first three bytes are correct before calling this method.
	 * 
	 * @param src
	 *            the source byte array
	 * @param length
	 *            the length of the data in the byte array
	 * @return a ClientServerRoom
	 * @throws IOException
	 */
	public static ClientServerRoom decodeRoom(byte[] src, int length) throws IOException {
		if (src == null) {
			throw new IOException("src is null");
		}

		if (length > src.length) {
			throw new IOException("length is invalid");
		}

		if (length < 1 + 2 + 4 + 1) {
			throw new IOException("packet format is invalid");
		}

		ByteBuffer buffer = ByteBuffer.wrap(src, 0, length);
		buffer.position(3);

		// get room ID
		int roomID = buffer.getInt();

		// get room name
		byte roomNameLength = buffer.get();
		if (roomNameLength < 1 || length < buffer.position() + roomNameLength) {
			throw new IOException("packet format is invalid");
		}
		byte[] roomNameData = new byte[roomNameLength];
		buffer.get(roomNameData);
		String name = new String(roomNameData, 0, roomNameLength, "UTF-8");

		/*
		 * get number of human players, AI players, max players, inGame flag,
		 * map ID and max map ID
		 */
		if (length < buffer.position() + 1 + 1 + 1 + 1 + 4 + 4) {
			throw new IOException("packet format is invalid");
		}
		byte humanPlayerNumber = buffer.get();
		byte aiPlayerNumber = buffer.get();
		byte maxPlayer = buffer.get();
		boolean inGame = buffer.get() != 0;
		int mapID = buffer.getInt();
		int maxMapID = buffer.getInt();
		if (humanPlayerNumber < 0 || aiPlayerNumber < 0 || maxPlayer < 2 || mapID < 0 || maxMapID < 0
				|| maxMapID < mapID) {
			throw new IOException("packet format is invalid");
		}

		// get human player info
		List<ClientServerPlayer> humanPlayerList = new ArrayList<ClientServerPlayer>(4);
		for (int i = 0; i < humanPlayerNumber; i++) {
			if (length < buffer.position() + 4 + 1) {
				throw new IOException("packet format is invalid");
			}

			// get player ID
			int playerID = buffer.getInt();

			// get player name
			byte playerNameLength = buffer.get();
			if (playerNameLength < 1 || length < buffer.position() + playerNameLength) {
				throw new IOException("packet format is invalid");
			}
			byte[] playerNameData = new byte[playerNameLength];
			buffer.get(playerNameData);
			String playerName = new String(playerNameData, 0, playerNameLength, "UTF-8");

			if (length < buffer.position() + 1) {
				throw new IOException("packet format is invalid");
			}

			boolean readyToPlay = buffer.get() != 0;

			humanPlayerList.add(new ClientServerPlayer(playerID, playerName, readyToPlay));
		}

		// get AI player info
		List<ClientServerAI> aiPlayerList = new ArrayList<ClientServerAI>(4);
		for (int i = 0; i < aiPlayerNumber; i++) {
			if (length < buffer.position() + 1 + 1) {
				throw new IOException("packet format is invalid");
			}

			// get AI ID
			byte playerID = buffer.get();
			// get AI difficulty
			byte difficulty = buffer.get();
			AIDifficulty aiDifficulty = byteToAIDifficulty(difficulty);

			aiPlayerList.add(new ClientServerAI(playerID, aiDifficulty));
		}

		if (length != buffer.position()) {
			throw new IOException("packet format is invalid");
		}

		ClientServerRoom room = new ClientServerRoom(roomID, name, humanPlayerNumber, aiPlayerNumber, maxPlayer, inGame,
				mapID, maxMapID);
		room.setHumanPlayerList(humanPlayerList);
		room.setAIPlayerList(aiPlayerList);

		return room;
	}

	/**
	 * Decode GameState from bytes in MSG_S_ROOM_GAMESTATE format. The first
	 * three bytes in the destination byte array are reserved for message type
	 * and sequence number and will be ignored. The caller should ensure the
	 * first three bytes are correct before calling this method.
	 * 
	 * @param gameState
	 *            the game state to modify, or null to create a new game state
	 * @param src
	 *            the source byte array
	 * @param length
	 *            the length of the data in the byte array
	 * @return the gameState in the argument if it is not null, or a new
	 *         GameState object if it is null
	 * @throws IOException
	 */
	public static GameState decodeGameState(GameState gameState, byte[] src, int length) throws IOException {
		if (gameState == null) {
			// gameState = new GameState(new Map("map", new
			// Block[gridMapWidth][gridMapHeight], null), null);
			return decodeGameState(src, length);
		}

		if (src == null) {
			throw new IOException("src is null");
		}

		if (length > src.length) {
			throw new IOException("length is invalid");
		}

		if (length < 7 + 130 + 1) {
			throw new IOException("packet format is invalid");
		}

		ByteBuffer buffer = ByteBuffer.wrap(src, 0, length);

		buffer.position(7);
		byte gridMapWidth = buffer.get();
		if (gridMapWidth < 1 || gridMapWidth > 16) {
			throw new IOException("gridMap width is not in the range [1,16]");
		}

		byte gridMapHeight = buffer.get();
		if (gridMapHeight < 1 || gridMapHeight > 16) {
			throw new IOException("gridMap height is not in the range [1,16]");
		}

		if (gameState.getMap() == null || gameState.getMap().getGridMap() == null
				|| gameState.getMap().getGridMap().length != gridMapWidth) {
			gameState.setMap(new Map("map", new Block[gridMapWidth][gridMapHeight], null));
		}

		Block[][] gridMap = gameState.getMap().getGridMap();
		for (int i = 0; i < gridMap.length; i++) {
			if (gridMap[i] == null || gridMap[i].length != gridMapHeight) {
				gridMap[i] = new Block[gridMapHeight];
			}
		}

		buffer.position(7 + 130);
		byte numPlayer = buffer.get();
		if (numPlayer < 1) {
			throw new IOException("playerList size is not in the range [1,127]");
		}

		if (length < 7 + 130 + 1 + 35 * numPlayer + 1) {
			throw new IOException("packet format is invalid");
		}

		buffer.position(7 + 130 + 1 + 35 * numPlayer);
		byte numBomb = buffer.get();
		if (numBomb < 0) {
			throw new IOException("bombList size is not in the range [0,127]");
		}

		if (length != 7 + 130 + 1 + 35 * numPlayer + 1 + 20 * numBomb + 2) {
			throw new IOException("packet format is invalid");
		}

		// Map
		buffer.position(7 + 1 + 1);
		long bitArr[][] = new long[4][4];
		for (int b = 3; b >= 0; b--) {
			for (int i = 3; i >= 0; i--) {
				bitArr[b][i] = buffer.getLong();
			}
		}

		for (int x = 0; x < gridMapWidth; x++) {
			for (int y = 0; y < gridMapHeight; y++) {
				Block b;

				byte bits = 0;
				int bitIndex = x + y * gridMapWidth;
				for (int i = 3; i >= 0; i--) {
					boolean bit = BitArray.getBit(bitArr[i][bitIndex / 64], bitIndex % 64);
					bits = BitArray.setBit(bits, i, bit);
				}

				b = byteToBlock(bits);
				if (gridMap[x][y] != b) {
					gridMap[x][y] = b;
				}
			}
		}

		// Players
		buffer.position(7 + 130 + 1);
		if (gameState.getPlayers() == null) {
			gameState.setPlayers(new ArrayList<>(4));
		}
		List<Player> playerList = gameState.getPlayers();
		synchronized (playerList) {
			while (playerList.size() > numPlayer) {
				playerList.remove(playerList.size() - 1);
			}
			while (playerList.size() < numPlayer) {
				playerList.add(null);
			}
		}
		for (int i = 0; i < numPlayer; i++) {
			Player player;
			synchronized (playerList) {
				player = playerList.get(i);
				if (player == null) {
					player = new Player(null, new Point(0, 0), 3, 321);
					playerList.set(i, player);
				}
			}

			int id = buffer.getInt();
			int posX = buffer.getInt();
			int posY = buffer.getInt();
			int lives = buffer.getInt();
			double speed = buffer.getDouble();
			int bombRange = buffer.getInt();
			int maxBomb = buffer.getInt();
			short keyState = buffer.getShort();
			byte alive = buffer.get();

			boolean isAlive = alive == 1;
			KeyboardState k = player.getKeyState();
			if (k == null) {
				k = new KeyboardState();
				player.setKeyState(k);
			}

			k.setMovement(Movement.NONE);
			k.setBomb(false);
			if (BitArray.getBit(keyState, 0)) {
				k.setMovement(Movement.NONE);
			} else if (BitArray.getBit(keyState, 1)) {
				k.setMovement(Movement.UP);
			} else if (BitArray.getBit(keyState, 2)) {
				k.setMovement(Movement.DOWN);
			} else if (BitArray.getBit(keyState, 3)) {
				k.setMovement(Movement.LEFT);
			} else if (BitArray.getBit(keyState, 4)) {
				k.setMovement(Movement.RIGHT);
			}
			k.setBomb(BitArray.getBit(keyState, 5));

			// player.setName("player " + id);
			player.getPos().x = posX;
			player.getPos().y = posY;
			player.setLives(lives);
			player.setSpeed(speed);
			player.setPlayerID(id);
			player.setBombRange(bombRange);
			player.setMaxNrOfBombs(maxBomb);
			player.setAlive(isAlive);
		}

		// Bombs
		buffer.position(7 + 130 + 1 + 35 * numPlayer + 1);
		if (gameState.getBombs() == null) {
			gameState.setBombs(new ArrayList<>());
		}
		List<Bomb> bombList = gameState.getBombs();
		synchronized (bombList) {
			while (bombList.size() > numBomb) {
				bombList.remove(bombList.size() - 1);
			}
			while (bombList.size() < numBomb) {
				bombList.add(null);
			}
		}
		for (int i = 0; i < numBomb; i++) {
			Bomb bomb;
			synchronized (bombList) {
				bomb = bombList.get(i);
				if (bomb == null) {
					bomb = new Bomb(null, new Point(0, 0), 0, 0);
					bombList.set(i, bomb);
				}
			}

			int playerID = buffer.getInt();
			int posX = buffer.getInt();
			int posY = buffer.getInt();
			int time = buffer.getInt();
			int radius = buffer.getInt();

			// bomb.setPlayerName("bomb from " + playerID);
			bomb.getPos().x = posX;
			bomb.getPos().y = posY;
			bomb.setTime(time);
			bomb.setRadius(radius);
			bomb.setPlayerID(playerID);
		}

		// Audio Events
		buffer.position(7 + 130 + 1 + 35 * numPlayer + 1 + 20 * numBomb);
		short audioState = buffer.getShort();

		List<AudioEvent> audioEventList = new ArrayList<>(16);
		if (BitArray.getBit(audioState, 0)) {
			audioEventList.add(AudioEvent.PLACE_BOMB);
		}
		if (BitArray.getBit(audioState, 1)) {
			audioEventList.add(AudioEvent.EXPLOSION);
		}
		if (BitArray.getBit(audioState, 2)) {
			audioEventList.add(AudioEvent.PLAYER_DEATH);
		}
		if (BitArray.getBit(audioState, 3)) {
			// movement sound removed
			// audioEventList.add(AudioEvent.MOVEMENT);
		}
		if (BitArray.getBit(audioState, 4)) {
			audioEventList.add(AudioEvent.POWERUP);
		}
		gameState.setAudioEvents(audioEventList);

		return gameState;
	}

	/**
	 * Decode GameState from bytes in MSG_S_ROOM_GAMESTATE format. The first
	 * three bytes in the destination byte array are reserved for message type
	 * and sequence number and will be ignored. The caller should ensure the
	 * first three bytes are correct before calling this method.
	 * 
	 * @param src
	 *            the source byte array
	 * @param length
	 *            the length of the data in the byte array
	 * @return a list of ClientServerRoom
	 * @throws IOException
	 */
	private static GameState decodeGameState(byte[] src, int length) throws IOException {
		if (src == null) {
			throw new IOException("src is null");
		}
		if (length > src.length) {
			throw new IOException("length is invalid");
		}
		if (length < 7 + 130 + 1) {
			throw new IOException("packet format is invalid");
		}
		ByteBuffer buffer = ByteBuffer.wrap(src, 0, length);
		buffer.position(7);
		byte gridMapWidth = buffer.get();
		if (gridMapWidth < 1 || gridMapWidth > 16) {
			throw new IOException("gridMap width is not in the range [1,16]");
		}
		byte gridMapHeight = buffer.get();
		if (gridMapHeight < 1 || gridMapHeight > 16) {
			throw new IOException("gridMap height is not in the range [1,16]");
		}
		Block[][] gridMap = new Block[gridMapWidth][gridMapHeight];
		buffer.position(7 + 130);
		byte numPlayer = buffer.get();
		if (numPlayer < 1) {
			throw new IOException("playerList size is not in the range [1,127]");
		}
		if (length < 7 + 130 + 1 + 35 * numPlayer + 1) {
			throw new IOException("packet format is invalid");
		}
		buffer.position(7 + 130 + 1 + 35 * numPlayer);
		byte numBomb = buffer.get();
		if (numBomb < 0) {
			throw new IOException("bombList size is not in the range [0,127]");
		}
		if (length != 7 + 130 + 1 + 35 * numPlayer + 1 + 20 * numBomb + 2) {
			throw new IOException("packet format is invalid");
		}
		// Map
		buffer.position(7 + 1 + 1);
		long bitArr[][] = new long[4][4];
		for (int b = 3; b >= 0; b--) {
			for (int i = 3; i >= 0; i--) {
				bitArr[b][i] = buffer.getLong();
			}
		}
		for (int x = 0; x < gridMapWidth; x++) {
			for (int y = 0; y < gridMapHeight; y++) {
				Block b;

				byte bits = 0;
				int bitIndex = x + y * gridMapWidth;
				for (int i = 3; i >= 0; i--) {
					boolean bit = BitArray.getBit(bitArr[i][bitIndex / 64], bitIndex % 64);
					bits = BitArray.setBit(bits, i, bit);
				}

				b = byteToBlock(bits);
				gridMap[x][y] = b;
			}
		}
		// Players
		buffer.position(7 + 130 + 1);
		List<Player> playerList = new ArrayList<>(numPlayer);
		for (int i = 0; i < numPlayer; i++) {
			int id = buffer.getInt();
			int posX = buffer.getInt();
			int posY = buffer.getInt();
			int lives = buffer.getInt();
			double speed = buffer.getDouble();
			int bombRange = buffer.getInt();
			int maxBomb = buffer.getInt();
			short keyState = buffer.getShort();
			byte alive = buffer.get();
			boolean isAlive = alive == 1;
			KeyboardState k = new KeyboardState();
			k.setMovement(Movement.NONE);
			k.setBomb(false);
			if (BitArray.getBit(keyState, 0)) {
				k.setMovement(Movement.NONE);
			} else if (BitArray.getBit(keyState, 1)) {
				k.setMovement(Movement.UP);
			} else if (BitArray.getBit(keyState, 2)) {
				k.setMovement(Movement.DOWN);
			} else if (BitArray.getBit(keyState, 3)) {
				k.setMovement(Movement.LEFT);
			} else if (BitArray.getBit(keyState, 4)) {
				k.setMovement(Movement.RIGHT);
			}
			k.setBomb(BitArray.getBit(keyState, 5));
			Player p = new Player(/* "player " + id */null, new Point(posX, posY), lives, speed);
			p.setPlayerID(id);
			p.setBombRange(bombRange);
			p.setMaxNrOfBombs(maxBomb);
			p.setKeyState(k);
			p.setAlive(isAlive);
			playerList.add(p);
		}
		// Bombs
		buffer.position(7 + 130 + 1 + 35 * numPlayer + 1);
		List<Bomb> bombList = new ArrayList<>(numBomb);
		for (int i = 0; i < numBomb; i++) {
			int playerID = buffer.getInt();
			int posX = buffer.getInt();
			int posY = buffer.getInt();
			int time = buffer.getInt();
			int radius = buffer.getInt();
			Bomb b = new Bomb(/* "player " + playerID */null, new Point(posX, posY), time, radius);
			b.setPlayerID(playerID);
			bombList.add(b);
		}
		// Audio Events
		buffer.position(7 + 130 + 1 + 35 * numPlayer + 1 + 20 * numBomb);
		short audioState = buffer.getShort();
		List<AudioEvent> audioEventList = new ArrayList<>(16);
		if (BitArray.getBit(audioState, 0)) {
			audioEventList.add(AudioEvent.PLACE_BOMB);
		}
		if (BitArray.getBit(audioState, 1)) {
			audioEventList.add(AudioEvent.EXPLOSION);
		}
		if (BitArray.getBit(audioState, 2)) {
			audioEventList.add(AudioEvent.PLAYER_DEATH);
		}
		if (BitArray.getBit(audioState, 3)) {
			// movement sound removed
			// audioEventList.add(AudioEvent.MOVEMENT);
		}
		if (BitArray.getBit(audioState, 4)) {
			audioEventList.add(AudioEvent.POWERUP);
		}

		GameState gameState = new GameState(new Map("map", gridMap, new ArrayList<>()), playerList);
		gameState.setBombs(bombList);
		gameState.setAudioEvents(audioEventList);

		return gameState;
	}

	/**
	 * Convert KeyboardState into short
	 * 
	 * @param k
	 *            the KeyboardState object
	 * @return the short
	 */
	public static short keyboardStateToShort(KeyboardState k) {
		if (k == null) {
			return 0;
		}

		Movement movement = k.getMovement();
		boolean bomb = k.isBomb();
		short keyState = 0;

		if (movement == null || movement == Movement.NONE) {
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

		return keyState;
	}

	/**
	 * Convert byte into Block (only bit 3 to bit 0 in the byte are used)
	 * 
	 * @param b
	 *            the byte
	 * @return the Block
	 */
	public static Block byteToBlock(byte b) {
		Block block = null;

		switch (b) {
		case 0:
			block = Block.BLANK;
			break;

		case 1:
			block = Block.SOLID;
			break;

		case 2:
			block = Block.SOFT;
			break;

		case 3:
			block = Block.BLAST;
			break;

		case 4:
			block = Block.PLUS_BOMB;
			break;

		case 5:
			block = Block.MINUS_BOMB;
			break;

		case 6:
			block = Block.PLUS_RANGE;
			break;

		case 7:
			block = Block.MINUS_RANGE;
			break;

		case 8:
			block = Block.PLUS_SPEED;
			break;

		case 9:
			block = Block.MINUS_SPEED;
			break;

		case 10:
			block = Block.HOLE;
			break;

		default:
			block = Block.BLANK;
			break;
		}

		return block;
	}

	/**
	 * Convert byte into AIDifficulty
	 * 
	 * @param b
	 *            the byte
	 * @return the AIDifficulty
	 */
	public static AIDifficulty byteToAIDifficulty(byte b) {
		AIDifficulty aiDifficulty;

		switch (b) {
		case ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_EASY:
			aiDifficulty = AIDifficulty.EASY;
			break;
		case ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_MEDIUM:
			aiDifficulty = AIDifficulty.MEDIUM;
			break;
		case ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_HARD:
			aiDifficulty = AIDifficulty.HARD;
			break;
		case ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY_EXTREME:
			aiDifficulty = AIDifficulty.EXTREME;
			break;
		default:
			aiDifficulty = AIDifficulty.MEDIUM;
			break;
		}

		return aiDifficulty;
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

		case HOLE:
			b = 10;
			break;

		default:
			b = 0;
			break;
		}

		return b;
	}

	/**
	 * Encode custom map into MSG_C_ROOM_SETINFO_ADDMAP format. The first three
	 * bytes in the destination byte array are reserved for message type and
	 * sequence number and will not be overwritten by this method. The caller
	 * should set the first three bytes properly before sending.
	 * 
	 * @param roomID
	 *            the ID of the room which the player is in
	 * @param map
	 *            the custom map
	 * @param dest
	 *            the destination byte array that will be sent over the network
	 * @return the number of bytes of the encoded data in the byte array
	 * @throws IOException
	 */
	public static int encodeCustomMap(int roomID, Map map, byte[] dest) throws IOException {
		if (map == null || dest == null) {
			throw new IOException("map or dest is null");
		}

		int len = 1 + 2 + 4 + 1 + 130 + 32;
		if (dest.length < len) {
			throw new IOException("dest is too short");
		}

		// wrap byte array into a ByteBuffer
		ByteBuffer buffer = ByteBuffer.wrap(dest);

		// put room id
		buffer.position(3);
		buffer.putInt(roomID);

		// put MSG_C_ROOM_SETINFO_ADDMAP
		buffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_ADDMAP);

		// put map
		Block[][] gridMap = map.getGridMap();
		if (gridMap == null) {
			throw new IOException("gridMap is null");
		}

		if (gridMap.length < 1 || gridMap.length > 16) {
			throw new IOException("gridMap width is not in the range [1,16]");
		}
		byte gridMapWidth = (byte) gridMap.length;

		byte gridMapHeight = -1;
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

		List<Point> spawnPoints = map.getSpawnPoints();
		if (spawnPoints == null) {
			spawnPoints = new ArrayList<Point>(4);
		}

		while (spawnPoints.size() < 4) {
			spawnPoints.add(new Point(64, 64));
		}

		for (int i = 0; i < 4; i++) {
			Point p = spawnPoints.get(i);
			if (p != null) {
				buffer.putInt(p.x);
				buffer.putInt(p.y);
			} else {
				buffer.putInt(64);
				buffer.putInt(64);
			}
		}

		return len;
	}

}
