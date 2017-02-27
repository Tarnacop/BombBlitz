package bomber.networking;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bomber.AI.AIDifficulty;
import bomber.game.GameState;
import bomber.game.KeyboardState;

public class ClientThread implements Runnable {
	private final PrintStream printStream;

	private final InetSocketAddress serverSockAddr;

	// time out value for server, currently 25 seconds
	private long serverTimeOut = 25;
	// the interval at which the client will test whether a new connection
	// attempt is timeout
	private long connectionAttemptTimeout = 15;
	private long connectionAttemptTimeoutInterval = 3;
	// the interval at which the client will test whether an established
	// connection is timeout
	private long keepAliveInterval = 10;
	// the interval at which the client will request the latest list of players
	// and rooms from the server
	private long listRequestInterval = 5;
	// unacknowledged packets will be detected and retransmitted every 500ms
	private long retransmitInterval = 500;
	// maximum number of retransmissions per packet
	private int maxRetransmitCount = 20;

	// the socket for client
	private final DatagramSocket socket;

	// history of packets sent to server
	private final ClientServerInfo serverInfo;

	/*
	 * whether there is a connection in progress(after connected() being called
	 * but before server's response)
	 */
	private boolean attemptingConnection = false;
	private long attemptingConnectionTimeStamp;

	// whether the connection has been established
	private boolean connected = false;

	// the name of the client
	private String name = "defname";
	private String tmpName = "tmpname";

	// the ID of the client
	private int clientID = -1;

	// whether the client is in room(a client can either in lobby or in room)
	private boolean inRoom = false;
	private int roomID = -1;

	// the ID, width and height of the map in which the client will play
	private int mapID = 0;
	private byte mapWidth = 16;
	private byte mapHeight = 16;

	// whether the client is in game(a client must be in room when it is in
	// game)
	private boolean inGame = false;

	// list of players connected to the server
	private List<ClientServerPlayer> playerList = new ArrayList<ClientServerPlayer>();

	// list of rooms in the server lobby
	private List<ClientServerLobbyRoom> roomList = new ArrayList<ClientServerLobbyRoom>();

	// room the client is currently in
	private ClientServerRoom room = null;

	// last received game state will be here
	private GameState gameState = null;

	// list of client network event listeners
	private List<ClientNetInterface> netList = new ArrayList<ClientNetInterface>();

	// 2000 bytes of receiving buffer
	private final int recvBufferLen = 2000;
	private final byte[] recvBuffer = new byte[recvBufferLen];
	private final ByteBuffer recvByteBuffer = ByteBuffer.wrap(recvBuffer);
	private final DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);

	// 2000 bytes of sending buffer
	private final int sendBufferLen = 2000;
	private final byte[] sendBuffer = new byte[sendBufferLen];
	private final ByteBuffer sendByteBuffer = ByteBuffer.wrap(sendBuffer);

	// sending buffer for public methods
	private final byte[] publicSendBuffer = new byte[sendBufferLen];
	private final ByteBuffer publicSendByteBuffer = ByteBuffer.wrap(publicSendBuffer);

	// scheduled executor for server keep alive and packet retransmission
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

	public ClientThread(String hostname, int port, PrintStream printStream) throws SocketException {
		// this.hostname = hostname;
		// this.port = port;
		this.printStream = printStream;

		serverSockAddr = new InetSocketAddress(hostname, port);
		if (serverSockAddr.isUnresolved()) {
			throw new SocketException("unresolved hostname " + hostname);
		}
		socket = new DatagramSocket();
		// socket.setSoTimeout(0);

		serverInfo = new ClientServerInfo(serverSockAddr);
	}

	/**
	 * Create a Runnable client object for use as a thread
	 * 
	 * @param hostname
	 *            the host name of the server
	 * @param port
	 *            the UDP port of the server
	 * @throws SocketException
	 */
	public ClientThread(String hostname, int port) throws SocketException {
		// this.hostname = hostname;
		// this.port = port;
		this.printStream = System.out;

		serverSockAddr = new InetSocketAddress(hostname, port);
		if (serverSockAddr.isUnresolved()) {
			throw new SocketException("unresolved hostname " + hostname);
		}
		socket = new DatagramSocket();
		// socket.setSoTimeout(0);

		serverInfo = new ClientServerInfo(serverSockAddr);
	}

	public void run() {
		pClient("ready to connect to " + serverSockAddr);

		// set up tasks
		// connection attempt timeout task
		Runnable connectionAttemptTimeoutTask = () -> {
			if (attemptingConnection
					&& Instant.now().getEpochSecond() - attemptingConnectionTimeStamp > connectionAttemptTimeout) {
				setConnected(false);

				for (ClientNetInterface e : netList) {
					e.connectionAttemptTimeout();
				}

				attemptingConnection = false;
			}
		};
		scheduledExecutor.scheduleWithFixedDelay(connectionAttemptTimeoutTask, connectionAttemptTimeoutInterval,
				connectionAttemptTimeoutInterval, TimeUnit.SECONDS);

		// established connection keep alive task
		Runnable keepAliveTask = () -> {
			if (!isConnected()) {
				setInRoom(false, -1);
				return;
			}

			Instant now = Instant.now();
			if (now.getEpochSecond() - serverInfo.getTimeStamp() > serverTimeOut) {
				pClient("keepAliveTask: Warning, connection to server is possibly timeout, setting connected to false");

				setConnected(false);

				for (ClientNetInterface e : netList) {
					e.disconnected();
				}

			} else {
				// send a ping packet
				try {
					byte[] data = new byte[3];
					sendPacket(new DatagramPacket(data, data.length, serverSockAddr), ProtocolConstant.MSG_C_NET_PING,
							true);
				} catch (IOException e) {
					pClient("keepAliveTask: " + e);
				}
			}
		};
		scheduledExecutor.scheduleWithFixedDelay(keepAliveTask, keepAliveInterval, keepAliveInterval, TimeUnit.SECONDS);

		// player list and room list request task
		Runnable listRequestTask = () -> {
			if (!isConnected()) {
				setInRoom(false, -1);
				return;
			}

			// request room list only when the client is in lobby
			if (isInLobby()) {
				try {
					this.updateRoomList();
				} catch (IOException e) {
					pClient("listRequestTask: " + e);
				}
			}

			// request player list
			try {
				this.updatePlayerList();
			} catch (IOException e) {
				pClient("listRequestTask: " + e);
			}

			// optional: request detailed room info when the client is in room

		};
		scheduledExecutor.scheduleWithFixedDelay(listRequestTask, 0, listRequestInterval, TimeUnit.SECONDS);

		// server packet acknowledgement checking and retransmission task
		Runnable retransmitTask = () -> {
			ArrayList<PacketHistoryEntry> packetList = serverInfo.getPacketHistoryList();
			for (PacketHistoryEntry f : packetList) {
				if (f != null && !f.isAcked() && f.getRetransmissionCount() < maxRetransmitCount) {
					/*
					 * pClientf(
					 * "retransmitTask: Retransmitting packet %d created at %d with length %d and retransmission count %d to server %s\n"
					 * , f.getSequence(), f.getCreationTimeStamp(),
					 * f.getPacketLength(),
					 * f.getRetransmissionCountAndIncrement(), serverSockAddr);
					 */
					try {
						sendPacket(new DatagramPacket(f.getPacketData(), f.getPacketLength(), serverSockAddr));
					} catch (IOException e) {
						pClient("retransmitTask: " + e);
					}
				}
			}
		};
		scheduledExecutor.scheduleWithFixedDelay(retransmitTask, retransmitInterval, retransmitInterval,
				TimeUnit.MILLISECONDS);

		// main loop
		while (true) {
			try {
				recvPacket(packet);
				processPacket(packet);
			} catch (SocketTimeoutException e) {
				pClient("" + e);
			} catch (IOException e) {
				pClient("" + e);
				scheduledExecutor.shutdown();
				socket.close();
				break;
			}
		}
		pClient("Exiting");
	}

	private void processPacket(DatagramPacket packet) throws IOException {
		if (packet.getLength() < 3) {
			// ignore short packets
			return;
		}

		SocketAddress sockAddr = packet.getSocketAddress();

		if (sockAddr.equals(serverSockAddr)) {
			serverInfo.updateTimeStamp();
		} else {
			pClient("Unexpected packet from " + sockAddr);
			return;
		}

		byte messageType = (byte) (recvByteBuffer.get(0) & (~ProtocolConstant.MSG_B_HASSEQUENCE));
		boolean messageHasSequence = (recvByteBuffer.get(0) & ProtocolConstant.MSG_B_HASSEQUENCE) != 0;
		short messageSequence = 0;
		if (messageHasSequence) {
			messageSequence = recvByteBuffer.getShort(1);
			// send acknowledgement to server
			sendByteBuffer.putShort(3, messageSequence);
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 2, serverSockAddr);
			sendPacket(p, ProtocolConstant.MSG_C_NET_ACK);
			/*
			 * log the sequence number of last 100 received packets and drop
			 * duplicate packets based on the sequence number
			 */
			if (messageHasSequence && serverInfo.isSequenceDuplicate(messageSequence)) {
				return;
			}
		}

		switch (messageType) {
		case ProtocolConstant.MSG_S_NET_NONCE: {
			if (packet.getLength() < 11) {
				return;
			}

			if (isConnected()) {
				pClient("Warning: client has possibly already connected");
			}

			long nonce = recvByteBuffer.getLong(3);
			byte[] nameData = tmpName.getBytes("UTF-8");
			// System.out.println("Server nonce: " + nonce);

			// prepare the buffer
			sendByteBuffer.putLong(3, nonce);
			sendByteBuffer.put(11, (byte) nameData.length);
			sendByteBuffer.position(12);
			sendByteBuffer.put(nameData);

			// send the packet
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 8 + 1 + nameData.length, serverSockAddr);
			sendPacket(p, ProtocolConstant.MSG_C_NET_CONNECT, true);

			break;
		}

		case ProtocolConstant.MSG_S_NET_ACCEPT: {
			// pClient("Connection has been accepted by the server");

			// this message must contain a client ID
			if (packet.getLength() < 7) {
				return;
			}

			int id = recvByteBuffer.getInt(3);
			if (id < 0) {
				pClient("Server bug, clientID should not be negative");
				return;
			}

			name = tmpName;
			setConnected(true);
			setInRoom(false, -1);
			setClientID(id);

			for (ClientNetInterface e : netList) {
				e.connectionAccepted();
			}

			attemptingConnection = false;

			break;
		}

		case ProtocolConstant.MSG_S_NET_REJECT: {
			// pClient("Connection has been rejected by the server");

			setConnected(false);

			for (ClientNetInterface e : netList) {
				e.connectionRejected();
			}

			attemptingConnection = false;

			break;
		}

		case ProtocolConstant.MSG_S_NET_ALREADYCONNECTED: {
			// pClient("You have already connected to the server");

			setConnected(true);

			for (ClientNetInterface e : netList) {
				e.alreadyConnected();
			}

			attemptingConnection = false;

			break;
		}

		case ProtocolConstant.MSG_S_NET_NOTCONNECTED: {
			// pClient("You have not connected to the server, setting connected
			// to false");

			setConnected(false);

			for (ClientNetInterface e : netList) {
				e.notConnected();
			}

			attemptingConnection = false;

			break;
		}

		case ProtocolConstant.MSG_S_NET_DISCONNECTED: {
			// pClient("You have disconnected from the server, setting connected
			// to false");

			setConnected(false);

			for (ClientNetInterface e : netList) {
				e.disconnected();
			}

			attemptingConnection = false;

			break;
		}

		case ProtocolConstant.MSG_S_NET_PING: {
			/*
			 * ping request from server, which is supposed to have the sequence
			 * bit set and acknowledged before the switch statement
			 */

			// no need to take further action

			break;
		}

		case ProtocolConstant.MSG_S_NET_ACK: {
			if (packet.getLength() < 5) {
				return;
			}

			// pClient("message type ACK");

			for (PacketHistoryEntry e : serverInfo.getPacketHistoryList()) {
				if (e != null && !e.isAcked() && e.getSequence() == recvByteBuffer.getShort(3)) {
					// pClientf("setting ACK to true for packet %d to server
					// %s\n", e.getSequence(), sockAddr);
					e.setAcked(true);
				}
			}

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_PLAYERLIST: {
			try {
				playerList = ClientPacketEncoder.decodePlayerList(recvBuffer, packet.getLength());
			} catch (IOException e) {
				pClient("Failed to decode player list: " + e);
				return;
			}

			for (ClientNetInterface e : netList) {
				e.playerListReceived();
			}

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_ROOMLIST: {
			try {
				roomList = ClientPacketEncoder.decodeRoomList(recvBuffer, packet.getLength());
			} catch (IOException e) {
				pClient("Failed to decode room list: " + e);
				return;
			}

			for (ClientNetInterface e : netList) {
				e.roomListReceived();
			}

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_ROOMACCEPT: {
			// this message must contain a room ID
			if (packet.getLength() < 7) {
				return;
			}

			int roomID = recvByteBuffer.getInt(3);
			if (roomID < 0) {
				pClient("Server bug, roomID should not be negative");
				return;
			}

			// pClient("Room creation/join has been accepted, room ID: " +
			// roomID);

			setInRoom(true, roomID);

			for (ClientNetInterface e : netList) {
				e.roomAccepted();
			}

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_ROOMREJECT: {
			// pClient("Room creation/join has been rejected by the server");

			for (ClientNetInterface e : netList) {
				e.roomRejected();
			}

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_NOTINROOM: {
			// pClient("You are not in a room");

			setInRoom(false, -1);

			for (ClientNetInterface e : netList) {
				e.notInRoom();
			}

			break;
		}

		case ProtocolConstant.MSG_S_ROOM_ALREADYINROOM: {
			if (packet.getLength() < 7) {
				return;
			}

			int roomID = recvByteBuffer.getInt(3);
			if (roomID < 0) {
				pClient("Server bug, roomID should not be negative");
				return;
			}

			// pClient("You are already in room with ID " + roomID);

			setInRoom(true, roomID);

			for (ClientNetInterface e : netList) {
				e.alreadyInRoom();
			}

			break;
		}

		case ProtocolConstant.MSG_S_ROOM_HAVELEFT: {
			// pClient("You have left the room");

			setInRoom(false, -1);

			for (ClientNetInterface e : netList) {
				e.haveLeftRoom();
			}

			break;
		}

		case ProtocolConstant.MSG_S_ROOM_ROOMINFO: {

			try {
				room = ClientPacketEncoder.decodeRoom(recvBuffer, packet.getLength());
			} catch (IOException e) {
				pClient("Failed to decode room: " + e);
				return;
			}

			setInRoom(true, room.getID());
			setMapID(room.getMapID());

			for (ClientNetInterface e : netList) {
				e.roomReceived();
			}

			break;

		}

		case ProtocolConstant.MSG_S_ROOM_GAMESTART: {
			if (packet.getLength() < 13) {
				return;
			}

			int roomID = recvByteBuffer.getInt(3);
			if (roomID < 0) {
				pClient("Server bug, roomID should not be negative");
				return;
			}

			int mapID = recvByteBuffer.getInt(7);
			if (mapID < 0) {
				pClient("Server bug, mapID should not be negative");
			}

			byte mapWidth = recvByteBuffer.get(11);
			if (mapWidth < 1 || mapWidth > 16) {
				pClient("Server bug, mapWidth should be in the range [1,16]");
				mapWidth = 16;
			}
			byte mapHeight = recvByteBuffer.get(12);
			if (mapHeight < 1 || mapHeight > 16) {
				pClient("Server bug, mapHeight should be in the range [1,16]");
				mapHeight = 16;
			}

			// pClient("Game has started in room with ID " + roomID);

			setInRoom(true, roomID);
			setInGame(true);
			setMapID(mapID);
			setMapWidth(mapWidth);
			setMapHeight(mapHeight);

			for (ClientNetInterface e : netList) {
				e.gameStarted();
			}

			break;
		}

		case ProtocolConstant.MSG_S_ROOM_GAMESTATE: {
			/*
			 * if (packet.getLength() < 7) { return; }
			 * 
			 * int roomID = recvByteBuffer.getInt(3); if (roomID < 0) { pClient(
			 * "Server bug, roomID should not be negative"); return; }
			 */

			try {
				gameState = ClientPacketEncoder.decodeGameState(gameState, recvBuffer, packet.getLength());
			} catch (IOException e) {
				pClient("Failed to decode game state");
				return;
			}

			// pClient("Received game state for room with ID " + roomID);
			// pClient(gameState.toString());

			for (ClientNetInterface e : netList) {
				e.gameStateReceived();
			}

			break;
		}

		case ProtocolConstant.MSG_S_ROOM_GAMEOVER: {
			if (packet.getLength() < 7) {
				return;
			}

			int roomID = recvByteBuffer.getInt(3);
			if (roomID < 0) {
				pClient("Server bug, roomID should not be negative");
				return;
			}

			// pClient("Game has ended in room with ID " + roomID);

			setInRoom(true, roomID);
			setInGame(false);

			for (ClientNetInterface e : netList) {
				e.gameEnded();
			}

			break;
		}

		default: {
			pClient("Default case: message type " + String.format("0x%02x", messageType));
		}

		}
	}

	private void sendPacket(DatagramPacket packet) throws IOException {
		socket.send(packet);
	}

	private void sendPacket(DatagramPacket packet, byte type) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
		buffer.put(0, (byte) (type & (~ProtocolConstant.MSG_B_HASSEQUENCE)));
		buffer.putShort(1, (short) 0);
		socket.send(packet);
	}

	private void sendPacket(DatagramPacket packet, byte type, boolean tryRetransmit) throws IOException {
		if (tryRetransmit) {
			short sequence = serverInfo.getNextPacketSequenceAndIncrement();

			// set the sequence number in the packet before sending
			ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
			buffer.put(0, (byte) (type | ProtocolConstant.MSG_B_HASSEQUENCE));
			buffer.putShort(1, sequence);

			// insert the packet to history
			serverInfo.insertPacket(sequence, packet);

			// send the packet
			// pClientf("sending with sequence %d to %s\n", sequence,
			// packet.getSocketAddress());
			socket.send(packet);

		} else {
			sendPacket(packet, type);
		}
	}

	private void recvPacket(DatagramPacket packet) throws IOException {
		socket.receive(packet);
	}

	private void pClient(String string) {
		printStream.println("Client: " + string);
	}

	/*
	 * private void pClientf(String string, Object... args) {
	 * printStream.printf("Client: " + string, args); }
	 */

	private synchronized void setConnected(boolean isConnected) {
		if (isConnected) {
			this.connected = true;
		} else {
			this.connected = false;
			setInRoom(false, -1);
			setClientID(-1);
		}
	}

	private synchronized void setInRoom(boolean inRoom, int roomID) {
		if (inRoom) {
			this.inRoom = true;
			this.roomID = roomID;
		} else {
			this.inRoom = false;
			this.roomID = -1;
		}
		setInGame(false);
	}

	private void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public synchronized void sendRaw(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverSockAddr);
		socket.send(packet);
	}

	/**
	 * Add a client network event listener
	 * 
	 * @param i
	 *            the client network event listener to be added
	 */
	public synchronized void addNetListener(ClientNetInterface i) {
		if (i == null) {
			return;
		}

		netList.add(i);
	}

	/**
	 * Remove a client network event listener
	 * 
	 * @param i
	 *            the client network event listener to be removed
	 */
	public synchronized void removeNetListener(ClientNetInterface i) {
		if (i == null) {
			return;
		}

		netList.remove(i);
	}

	/**
	 * Remove all client network event listeners
	 */
	public synchronized void removeAllNetListener() {
		netList.clear();
	}

	/**
	 * Send a connection request to the server with a nickname
	 * 
	 * @param name
	 *            the nickname to use
	 * @throws IOException
	 */
	public synchronized void connect(String name) throws IOException {
		if (attemptingConnection) {
			pClient("Error: client is attemping a connection already, please call connect() again later");
			return;
		}

		if (isConnected()) {
			pClient("Warning: client has possibly already connected");
		}

		// ignore null or empty name
		if (name == null || name.length() < 1) {
			throw new IOException("name cannot be null or zero length");
		}

		attemptingConnection = true;
		attemptingConnectionTimeStamp = Instant.now().getEpochSecond();

		tmpName = name;

		// prepare the buffer
		publicSendByteBuffer.putLong(3, 0);

		// send the packet
		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 8, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_NET_GETNONCE, true);
	}

	/**
	 * Send a request to the server for the latest list of players
	 * 
	 * @throws IOException
	 */
	public synchronized void updatePlayerList() throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_LOBBY_GETPLAYERLIST, true);
	}

	/**
	 * Send a request to the server for the latest list of rooms
	 * 
	 * @throws IOException
	 */
	public synchronized void updateRoomList() throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_LOBBY_GETROOMLIST, true);
	}

	/**
	 * Send a disconnection request to the server
	 * 
	 * @throws IOException
	 */
	public synchronized void disconnect() throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected already");
		}

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_NET_DISCONNECT, true);
	}

	/**
	 * Tell whether the client has connected to the server
	 * 
	 * @return true if the client has connected to the server
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Get the name of the client
	 * 
	 * @return the name of the client
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the ID of the client
	 * 
	 * @return a non-negative client ID, or -1 when the client is not connected
	 *         to the server
	 */
	public int getClientID() {
		return clientID;
	}

	private void setClientID(int clientID) {
		this.clientID = clientID;
	}

	/**
	 * Tell whether the client is in a room
	 * 
	 * @return true if the client is in a room, false if the client is in lobby
	 */
	public boolean isInRoom() {
		return inRoom;
	}

	/**
	 * Tell whether the client is in lobby
	 * 
	 * @return true if the client is in lobby, false if the client is in a room
	 */
	public boolean isInLobby() {
		return !isInRoom();
	}

	/**
	 * Tell whether the client is in a game
	 * 
	 * @return true if the client is in a game
	 */
	public boolean isInGame() {
		return inGame;
	}

	/**
	 * Get the last list of players received from the server. Client will also
	 * automatically request the list periodically
	 * 
	 * @return a list of ClientServerPlayer objects
	 */
	public List<ClientServerPlayer> getPlayerList() {
		return playerList;
	}

	/**
	 * Get the last list of rooms received from the server. Client will also
	 * automatically request the list periodically when it is NOT in a room(when
	 * it is in lobby)
	 * 
	 * @return a list of ClientServerLobbyRoom objects
	 */
	public List<ClientServerLobbyRoom> getRoomList() {
		return roomList;
	}

	/**
	 * Get the last received game state from the server. Note that a "null" will
	 * be returned if the client has not received any game state from server yet
	 * 
	 * @return a GameState
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * Get the last received room the client is in from the server. Note that a
	 * "null" will be returned if the client has not joined/received any room
	 * info from server yet
	 * 
	 * @return a ClientServerRoom
	 */
	public ClientServerRoom getRoom() {
		return room;
	}

	/**
	 * Get the ID of the room the client is currently in
	 * 
	 * @return a non-negative room ID, or -1 when the client is not in a room
	 */
	public int getRoomID() {
		return roomID;
	}

	/**
	 * Get the ID of the map in which the client will play. This should only be
	 * called when the client is in a room
	 * 
	 * @return a non-negative map ID
	 */
	public int getMapID() {
		return mapID;
	}

	private void setMapID(int mapID) {
		if (mapID >= 0) {
			this.mapID = mapID;
		}
	}

	/**
	 * Get the grid width of the map in which the client will play. This should
	 * only be called when the game is about to start(when gameStarted() method
	 * in the interface is called)
	 * 
	 * @return the grid width of the map
	 */
	public byte getMapWidth() {
		return mapWidth;
	}

	private void setMapWidth(byte mapWidth) {
		if (mapWidth > 0 && mapWidth <= 16) {
			this.mapWidth = mapWidth;
		}
	}

	/**
	 * Get the grid height of the map in which the client will play. This should
	 * only be called when the game is about to start(when gameStarted() method
	 * in the interface is called)
	 * 
	 * @return the grid height of the map
	 */
	public byte getMapHeight() {
		return mapHeight;
	}

	private void setMapHeight(byte mapHeight) {
		if (mapHeight > 0 && mapHeight <= 16) {
			this.mapHeight = mapHeight;
		}
	}

	/**
	 * Send a room creation request to the server
	 * 
	 * @param roomName
	 *            the name of the room
	 * @param maxPlayer
	 *            the max number of players in the room
	 * @param mapID
	 *            the ID of the map
	 * @throws IOException
	 */
	public synchronized void createRoom(String roomName, int maxPlayer, int mapID) throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (roomName == null || roomName.length() < 1) {
			throw new IOException("name cannot be null or have zero length");
		}
		byte[] nameData = roomName.getBytes("UTF-8");

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.put((byte) nameData.length);
		publicSendByteBuffer.put(nameData);
		publicSendByteBuffer.put((byte) maxPlayer);
		publicSendByteBuffer.putInt(mapID);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 1 + nameData.length + 1 + 4, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_LOBBY_CREATEROOM, true);
	}

	/**
	 * Send a room join request to the server
	 * 
	 * @param roomID
	 *            the ID of the room
	 * @throws IOException
	 */
	public synchronized void joinRoom(int roomID) throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (inRoom) {
			pClient("Warning: client is possibly already in a room");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(roomID);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_LOBBY_JOINROOM, true);
	}

	/**
	 * Send a room leave request to the server
	 * 
	 * @throws IOException
	 */
	public synchronized void leaveRoom() throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly already not in a room");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_LEAVE, true);
	}

	/**
	 * Send a "set room name" request to the server
	 * 
	 * @param name
	 *            the name of the room
	 * @throws IOException
	 */
	public synchronized void setRoomName(String name) throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly not in a room");
		}
		if (isInGame()) {
			pClient("Warning: client is possibly already in a game and this message will be ignored by the server");
		}

		if (name == null || name.length() < 1) {
			throw new IOException("name cannot be null or have zero length");
		}
		byte[] nameData = name.getBytes("UTF-8");

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_NAME);
		publicSendByteBuffer.put((byte) nameData.length);
		publicSendByteBuffer.put(nameData);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4 + 1 + 1 + nameData.length, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_SETINFO, true);
	}

	/**
	 * Send a "set max number of players in room" request to the server
	 * 
	 * @param maxPlayer
	 *            the max number of players in the room
	 * @throws IOException
	 */
	public synchronized void setRoomMaxPlayer(int maxPlayer) throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly not in a room");
		}
		if (isInGame()) {
			pClient("Warning: client is possibly already in a game and this message will be ignored by the server");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_MAXPLAYER);
		publicSendByteBuffer.put((byte) maxPlayer);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4 + 1 + 1, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_SETINFO, true);
	}

	/**
	 * Send a "set map ID of room" request to the server
	 * 
	 * @param mapID
	 *            the ID of the map
	 * @throws IOException
	 */
	public synchronized void setRoomMapID(int mapID) throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly not in a room");
		}
		if (isInGame()) {
			pClient("Warning: client is possibly already in a game and this message will be ignored by the server");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_MAPID);
		publicSendByteBuffer.putInt(mapID);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4 + 1 + 4, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_SETINFO, true);
	}

	/**
	 * Send an "add one AI to room" request to the server
	 * 
	 * @throws IOException
	 */
	public synchronized void addAI() throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly not in a room");
		}
		if (isInGame()) {
			pClient("Warning: client is possibly already in a game and this message will be ignored by the server");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_AI);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_AI_ADD);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4 + 1 + 1, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_SETINFO, true);
	}

	/**
	 * Send a "remove one AI from room" request to the server
	 * 
	 * @throws IOException
	 */
	public synchronized void removeAI() throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly not in a room");
		}
		if (isInGame()) {
			pClient("Warning: client is possibly already in a game and this message will be ignored by the server");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_AI);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_AI_REMOVE);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4 + 1 + 1, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_SETINFO, true);
	}

	/**
	 * Send a "set AI difficulty in room" request to the server
	 * 
	 * @param id
	 *            the id of the AI
	 * @param difficulty
	 *            the difficulty of the AI
	 * @throws IOException
	 */
	public synchronized void setAIDifficulty(int id, AIDifficulty difficulty) throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly not in a room");
		}
		if (isInGame()) {
			pClient("Warning: client is possibly already in a game and this message will be ignored by the server");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_AI);
		publicSendByteBuffer.put(ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY);
		publicSendByteBuffer.put((byte) id);

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

		publicSendByteBuffer.put(aiDifficulty);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4 + 1 + 1 + 1 + 1, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_SETINFO, true);
	}

	/**
	 * Tell the server whether the client is ready to start the game(when it is
	 * in a room). A game will be started by the server when all the clients in
	 * the room are ready to play
	 * 
	 * @param readyToPlay
	 *            true if the player is ready to play, false otherwise
	 * @throws IOException
	 */
	public synchronized void readyToPlay(boolean readyToPlay) throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly not in a room yet");
		}

		byte ready = 0;
		if (readyToPlay) {
			ready = 1;
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);
		publicSendByteBuffer.put(ready);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4 + 1, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_READYTOPLAY, true);
	}

	public synchronized void sendMove(KeyboardState keyboardState) throws IOException {
		if (!isConnected()) {
			pClient("Warning: client has possibly not connected yet");
		}

		if (!isInRoom()) {
			pClient("Warning: client is possibly not in a room yet");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);
		publicSendByteBuffer.putShort(ClientPacketEncoder.keyboardStateToShort(keyboardState));

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_GAME_SENDMOVE, false);
	}

	/**
	 * Terminate the thread. It is advisable to leave the room and disconnect
	 * from the server first
	 */
	public void exit() {
		pClient("exit() called");
		socket.close();
	}

}
