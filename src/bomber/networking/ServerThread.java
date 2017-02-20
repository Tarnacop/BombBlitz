package bomber.networking;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bomber.game.KeyboardState;

public class ServerThread implements Runnable {
	private final int port;

	private final PrintStream printStream;

	private final ServerConfiguration config;

	private final DatagramSocket socket;

	// table for storing client info
	private final ServerClientTable clientTable;

	// table for storing room info
	private final ServerRoomTable roomTable;

	// 2000 bytes of receiving buffer
	private final int recvBufferLen = 2000;
	private final byte[] recvBuffer = new byte[recvBufferLen];
	private final ByteBuffer recvByteBuffer = ByteBuffer.wrap(recvBuffer);
	private final DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);

	// 2000 bytes of sending buffer
	private final int sendBufferLen = 2000;
	private final byte[] sendBuffer = new byte[sendBufferLen];
	private final ByteBuffer sendByteBuffer = ByteBuffer.wrap(sendBuffer);

	// scheduled executor for client keep alive and packet retransmission tasks
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

	/**
	 * Create a Runnable server object for use as a thread
	 * 
	 * @param port
	 *            the UDP port on which the server listens
	 * @param printStream
	 *            the stream to which the server prints log messages
	 * @param config
	 *            the configuration of the server
	 * @throws SocketException
	 */
	public ServerThread(int port, PrintStream printStream, ServerConfiguration config) throws SocketException {
		this.port = port;
		this.printStream = printStream;
		this.config = config;
		this.clientTable = new ServerClientTable(config.getMaxPlayer());
		this.roomTable = new ServerRoomTable(config.getMaxPlayer());

		// open socket
		socket = new DatagramSocket(port);
	}

	/**
	 * Create a Runnable server object for use as a thread
	 * 
	 * @param port
	 *            the UDP port on which the server listens
	 * @param config
	 *            the configuration of the server
	 * @throws SocketException
	 */
	public ServerThread(int port, ServerConfiguration config) throws SocketException {
		this.port = port;
		this.printStream = System.out;
		this.config = config;
		this.clientTable = new ServerClientTable(config.getMaxPlayer());
		this.roomTable = new ServerRoomTable(config.getMaxPlayer());

		// open socket
		socket = new DatagramSocket(port);
	}

	/**
	 * Create a Runnable server object for use as a thread
	 * 
	 * @param port
	 *            the UDP port on which the server listens
	 * @throws SocketException
	 */
	public ServerThread(int port) throws SocketException {
		this.port = port;
		this.printStream = System.out;
		this.config = new ServerConfiguration();
		this.clientTable = new ServerClientTable(config.getMaxPlayer());
		this.roomTable = new ServerRoomTable(config.getMaxPlayer());

		// open socket
		socket = new DatagramSocket(port);
	}

	public void run() {
		pServer("Now listening on port " + port);

		// set up tasks
		// client keep alive task
		Runnable keepAliveTask = () -> {
			Instant now = Instant.now();
			for (Entry<SocketAddress, ServerClientInfo> e : clientTable.entrySet()) {
				if (now.getEpochSecond() - e.getValue().getTimeStamp() > config.getClientTimeOut()) {
					pServerf("keepAliveTask: Removing %s due to timeout\n", e.getKey());
					/*
					 * TODO need to remove every reference of the client on the
					 * server(from client table, room list and game sessions)
					 */
					removeClient(e.getValue());
				} else {
					// send a ping packet to each active client
					try {
						byte[] data = new byte[3];
						// must be set to true in sendPacket for acknowledgement
						sendPacket(new DatagramPacket(data, data.length, e.getKey()), ProtocolConstant.MSG_S_NET_PING,
								true);
					} catch (IOException e1) {
						pServer("keepAliveTask: " + e1);
					}
				}
			}
		};
		scheduledExecutor.scheduleWithFixedDelay(keepAliveTask, config.getKeepAliveInterval(),
				config.getKeepAliveInterval(), TimeUnit.SECONDS);
		// client packet acknowledgement checking and retransmission task
		Runnable retransmitTask = () -> {
			for (Entry<SocketAddress, ServerClientInfo> e : clientTable.entrySet()) {
				ArrayList<PacketHistoryEntry> packetList = e.getValue().getPacketHistoryList();
				for (PacketHistoryEntry f : packetList) {
					if (f != null && !f.isAcked() && f.getRetransmissionCount() < config.getMaxRetransmitCount()) {
						pServerf(
								"retransmitTask: Retransmitting packet %d created at %d with length %d and retransmission count %d to client %s\n",
								f.getSequence(), f.getCreationTimeStamp(), f.getPacketLength(),
								f.getRetransmissionCountAndIncrement(), e.getKey());
						try {
							sendPacket(new DatagramPacket(f.getPacketData(), f.getPacketLength(), e.getKey()));
						} catch (IOException e1) {
							pServer("retransmitTask: " + e);
						}
					}
				}
			}
		};
		scheduledExecutor.scheduleWithFixedDelay(retransmitTask, config.getRetransmitInterval(),
				config.getRetransmitInterval(), TimeUnit.MILLISECONDS);

		// main loop
		while (true) {
			try {
				recvPacket(packet);
				processPacket(packet);
			} catch (SocketTimeoutException e) {
				pServer("" + e);
			} catch (IOException e) {
				pServer("" + e);
				scheduledExecutor.shutdown();
				socket.close();
				break;
			}
		}
		pServer("Exiting");
	}

	private void processPacket(DatagramPacket packet) throws IOException {
		if (packet.getLength() < 3) {
			// ignore short packets
			return;
		}

		SocketAddress sockAddr = packet.getSocketAddress();

		byte messageType = (byte) (recvByteBuffer.get(0) & (~ProtocolConstant.MSG_B_HASSEQUENCE));
		boolean messageHasSequence = (recvByteBuffer.get(0) & ProtocolConstant.MSG_B_HASSEQUENCE) != 0;
		short messageSequence = 0;
		if (messageHasSequence) {
			messageSequence = recvByteBuffer.getShort(1);
			// send acknowledgement to client
			sendByteBuffer.putShort(3, messageSequence);
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 2, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_NET_ACK);
		}

		ServerClientInfo clientInfo = clientTable.get(sockAddr);
		if (clientInfo != null) {
			clientInfo.updateTimeStamp();
		} else {
			/*
			 * if the client does not exist on the server and the message type
			 * is not MSG_C_NET_CONNECT, tell the client it has not connected to
			 * the server yet
			 */
			if (messageType != ProtocolConstant.MSG_C_NET_CONNECT) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_NET_NOTCONNECTED, true);

				return;
			}
		}

		switch (messageType) {
		case ProtocolConstant.MSG_C_NET_CONNECT: {
			// TODO should reject with a reason in this type of message

			// packet length check
			if (packet.getLength() < 5) {
				return;
			}

			// name length check
			byte nameLength = recvByteBuffer.get(3);
			if (1 + 2 + 1 + nameLength != packet.getLength()) {
				return;
			}
			if (nameLength < 1) {
				pServer("Warning: name length smaller than 1 in conection request from " + sockAddr
						+ ", request ignored");
				return;
			}
			if (nameLength > config.getMaxNameLength()) {
				pServer("Warning: name length longer than " + config.getMaxNameLength() + " in conection request from "
						+ sockAddr + ", request ignored");
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_NET_REJECT, false);
				return;
			}

			// only valid packets can reach here

			// existence check
			if (clientInfo != null) {
				/*
				 * when the client exists, tell the client that it has already
				 * connected to the server
				 */
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_NET_ALREADYCONNECTED, true);

				return;
			}

			// only new clients can reach here

			/*
			 * a client may be rejected when server is full or there is
			 * duplicate name
			 */

			// server full check
			if (clientTable.size() >= config.getMaxPlayer()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_NET_REJECT, false);
				return;
			}

			// get name from packet
			String name = new String(recvBuffer, 4, nameLength, "UTF-8");

			// duplicate name check
			if (clientTable.contains(name)) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_NET_REJECT, false);
				return;
			}

			// create and add client
			ServerClientInfo client = new ServerClientInfo(sockAddr, name);
			clientTable.put(client);

			// tell the client that the connection has been accepted
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_NET_ACCEPT, true);

			pServerf("Accepted new client with name %s, id %d from %s\n", client.getName(), client.getID(),
					client.getSocketAddress());

			break;
		}

		case ProtocolConstant.MSG_C_NET_DISCONNECT: {
			pServer("Deleting client " + sockAddr + " due to disconnection request");

			removeClient(clientTable.get(sockAddr));

			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_NET_DISCONNECTED, false);

			break;
		}

		case ProtocolConstant.MSG_C_NET_PING: {
			/*
			 * ping request from client, which is supposed to have the sequence
			 * bit set and acknowledged before the switch statement
			 */

			// no need to take further action

			break;
		}

		case ProtocolConstant.MSG_C_NET_ACK: {
			if (packet.getLength() < 5) {
				return;
			}

			if (clientInfo != null) {
				for (PacketHistoryEntry e : clientInfo.getPacketHistoryList()) {
					if (e != null && !e.isAcked() && e.getSequence() == recvByteBuffer.getShort(3)) {
						e.setAcked(true);
						clientInfo.setRoundTripDelay(System.currentTimeMillis() - e.getCreationTimeStamp());
					}
				}
			}

			break;
		}

		case ProtocolConstant.MSG_C_LOBBY_GETPLAYERLIST: {
			int len = 0;
			try {
				len = ServerPacketEncoder.encodePlayerList(clientTable, sendBuffer);
			} catch (IOException e) {
				pServer("Failed to encode player list: " + e);
				return;
			}

			DatagramPacket p = new DatagramPacket(sendBuffer, 0, len, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_LOBBY_PLAYERLIST, true);

			break;
		}

		case ProtocolConstant.MSG_C_LOBBY_GETROOMLIST: {
			int len = 0;
			try {
				len = ServerPacketEncoder.encodeRoomList(roomTable, sendBuffer);
			} catch (IOException e) {
				pServer("Failed to encode room list: " + e);
				return;
			}

			DatagramPacket p = new DatagramPacket(sendBuffer, 0, len, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMLIST, true);

			break;
		}

		case ProtocolConstant.MSG_C_LOBBY_CREATEROOM: {
			// TODO should reject with a reason in this type of message

			recvByteBuffer.position(3);
			if (packet.getLength() < recvByteBuffer.position() + 1) {
				pServer("Failed to decode room creation request from " + sockAddr);
				return;
			}
			byte nameLength = recvByteBuffer.get();

			// get room name string
			if (nameLength < 1) {
				pServer("Warning: Invalid name length in room creation request from " + sockAddr + ", request ignored");
				return;
			}
			if (nameLength > config.getMaxNameLength()) {
				pServer("Warning: name length longer than " + config.getMaxNameLength()
						+ " in room creation request from " + sockAddr + ", request ignored");
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, false);
				return;
			}
			byte[] nameData = new byte[nameLength];
			recvByteBuffer.get(nameData);
			String roomName = new String(nameData, 0, nameLength, "UTF-8");

			// get max player limit and map ID
			if (packet.getLength() < recvByteBuffer.position() + 1 + 4) {
				pServer("Failed to decode room creation request from " + sockAddr);
				return;
			}
			byte maxPlayer = recvByteBuffer.get();
			int mapID = recvByteBuffer.getInt();
			pServerf("Room creation request from %s: Room name: %s, Max player limit: %d, Map ID: %d\n", sockAddr,
					roomName, maxPlayer, mapID);

			// check whether maxPlayer is in the range [2,4]
			if (maxPlayer < 2) {
				pServerf("Warning: maxPlayer value %d out of range, capping to 2\n", maxPlayer);
				maxPlayer = 2;
			} else if (maxPlayer > 4) {
				pServerf("Warning: maxPlayer value %d out of range, capping to 4\n", maxPlayer);
				maxPlayer = 4;
			}

			// check whether mapID is non-negative
			if (mapID < 0) {
				pServerf("Warning: mapID value %d out of range, capping to 0\n", mapID);
				mapID = 0;
			}

			// check whether the player is already in a room
			ServerClientInfo client = clientTable.get(sockAddr);
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}
			if (client.isInRoom()) {
				ServerRoom room = client.getRoom();
				if (room == null) {
					pServer("Bug: room should not be null in this situation");
					return;
				}
				sendByteBuffer.putInt(3, room.getID());
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// check whether there is duplicate room name
			if (roomTable.contains(roomName) || roomTable.size() >= clientTable.size()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, true);
				return;
			}

			// create the room and update the info of the client
			ServerRoom room = new ServerRoom(roomName, client, mapID);
			room.setMaxPlayer(maxPlayer);
			roomTable.put(room);
			client.setInRoom(true);
			client.setReadyToPlay(false);
			client.setRoom(room);

			// tell the client it has been accepted into the new room
			sendByteBuffer.putInt(3, room.getID());
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + 4, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMACCEPT, true);

			// TODO send room info MSG_S_ROOM_ROOMINFO to this client

			break;
		}

		case ProtocolConstant.MSG_C_LOBBY_JOINROOM: {
			if (packet.getLength() < 7) {
				pServer("Failed to decode room join request from " + sockAddr);
				return;
			}
			int roomID = recvByteBuffer.getInt(3);
			/*
			 * if (roomID < 0) { pServer(
			 * "Client bug: roomID should not be negative"); DatagramPacket p =
			 * new DatagramPacket(sendBuffer, 0, 3, sockAddr); sendPacket(p,
			 * ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, true); return; }
			 */

			pServerf("Room join request from %s, room ID: %d\n", sockAddr, roomID);

			// check whether the client is already in a room
			ServerClientInfo client = clientTable.get(sockAddr);
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}
			if (client.isInRoom()) {
				ServerRoom room = client.getRoom();
				if (room == null) {
					pServer("Bug: room should not be null in this situation");
					return;
				}
				sendByteBuffer.putInt(3, room.getID());
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// check whether the room exists
			ServerRoom room = roomTable.get(roomID);
			if (room == null) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, true);
				return;
			}

			// check whether the room is full
			if (room.getPlayerNumber() >= room.getMaxPlayer()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, true);
				return;
			}

			// check whether the room has a game in progress
			if (room.isInGame()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, true);
				return;
			}

			// update the info of the client and the room
			client.setInRoom(true);
			client.setReadyToPlay(false);
			client.setRoom(room);
			room.addPlayer(client);

			// tell the client it has been accepted into the new room
			sendByteBuffer.putInt(3, room.getID());
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + 4, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMACCEPT, true);

			/*
			 * TODO send room info MSG_S_ROOM_ROOMINFO to all clients in this
			 * room
			 */

			break;

		}

		case ProtocolConstant.MSG_C_ROOM_LEAVE: {
			if (packet.getLength() < 7) {
				pServer("Failed to decode room leave request from " + sockAddr);
				return;
			}

			int roomID = recvByteBuffer.getInt(3);
			/*
			 * if (roomID < 0) { pServer("Bug: roomID should not be negative");
			 * }
			 */

			pServerf("Room leave request from %s, room ID: %d\n", sockAddr, roomID);

			// check whether the client is already in a room
			ServerClientInfo client = clientTable.get(sockAddr);
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}

			// when the client is not in room yet
			if (!client.isInRoom()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_NOTINROOM, true);
				return;
			}

			// when the client is in room
			ServerRoom room = client.getRoom();
			if (room == null) {
				pServer("Bug: room should not be null in this situation");
				return;
			}

			/*
			 * check whether the room ID in request matches the room ID on
			 * server side
			 */
			if (roomID != room.getID()) {
				pServerf("Warning: roomID mismatch: %d(request) != %d(server)\n", roomID, room.getID());

				sendByteBuffer.putInt(3, room.getID());
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// remove the client from room
			removeClientFromRoom(client);
			// TODO is the client also in game ?
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_ROOM_HAVELEFT, true);

			/*
			 * TODO send room info MSG_S_ROOM_ROOMINFO to remaining clients in
			 * this room
			 */

			break;

		}

		case ProtocolConstant.MSG_C_ROOM_READYTOPLAY: {
			if (packet.getLength() < 8) {
				pServer("Failed to decode readyToPlay request from " + sockAddr);
				return;
			}

			int roomID = recvByteBuffer.getInt(3);
			/*
			 * if (roomID < 0) { pServer("Bug: roomID should not be negative");
			 * }
			 */

			boolean readyToPlay = recvByteBuffer.get(7) != 0;

			pServer("readyToPlay request from " + sockAddr + ", roomID: " + roomID + ", ready: " + readyToPlay);

			// check whether the client is already in a room
			ServerClientInfo client = clientTable.get(sockAddr);
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}

			// when the client is not in room yet
			if (!client.isInRoom()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_NOTINROOM, true);
				return;
			}

			// when the client is in room
			ServerRoom room = client.getRoom();
			if (room == null) {
				pServer("Bug: room should not be null in this situation");
				return;
			}

			/*
			 * check whether the room ID in request matches the room ID on
			 * server side
			 */
			if (roomID != room.getID()) {
				pServerf("roomID mismatch: %d(request) != %d(server)\n", roomID, room.getID());

				sendByteBuffer.putInt(3, room.getID());
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// update the client info
			client.setReadyToPlay(readyToPlay);

			// if readyToPlay is false, the game definitely won't start
			if (!readyToPlay) {
				return;
			}

			/*
			 * TODO send room info MSG_S_ROOM_ROOMINFO to all clients in this
			 * room
			 */

			// check whether there is already a game in progress
			if (room.isInGame()) {
				return;
			}

			// at least 2 players required to start the game
			if (room.getPlayerNumber() < 2) {
				return;
			}

			// create the game according to the map ID of the room
			if (!room.createGame(config.getTickRate(), this)) {
				pServerf("Failed to create game with map ID %d\n", room.getMapID());
				// TODO tell clients this error
				return;
			}

			// if all clients in this room are ready, start the game
			if (room.allPlayersReady()) {
				/*
				 * ServerGame should be responsible for sending updated game
				 * states to clients in this particular game session(game start,
				 * game state and game over messages) while ServerThread will
				 * update the KeyboardState of the players itself
				 */
				room.getGame().start();
			}

			break;

		}

		case ProtocolConstant.MSG_C_ROOM_SETINFO: {
			if (packet.getLength() < 7) {
				pServer("Failed to decode readyToPlay request from " + sockAddr);
				return;
			}

			int roomID = recvByteBuffer.getInt(3);

			// check whether the client is already in a room
			ServerClientInfo client = clientTable.get(sockAddr);
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}

			// when the client is not in room yet
			if (!client.isInRoom()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_NOTINROOM, true);
				return;
			}

			// when the client is in room
			ServerRoom room = client.getRoom();
			if (room == null) {
				pServer("Bug: room should not be null in this situation");
				return;
			}

			/*
			 * check whether the room ID in request matches the room ID on
			 * server side
			 */
			if (roomID != room.getID()) {
				pServerf("roomID mismatch: %d(request) != %d(server)\n", roomID, room.getID());

				sendByteBuffer.putInt(3, room.getID());
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// room info cannot be changed when game is in progress
			if (room.isInGame()) {
				return;
			}

			// TODO unfinished
			// change the room info
			if (packet.getLength() >= 9) {
				recvByteBuffer.position(7);
				byte changeType = recvByteBuffer.get();
				if (changeType == ProtocolConstant.MSG_C_ROOM_SETINFO_AI) {
					byte op = recvByteBuffer.get();
					if (op == ProtocolConstant.MSG_C_ROOM_SETINFO_AI_ADD) {
						// pServer("Adding one AI to room " + room.getID());
						room.addAI();
					} else if (op == ProtocolConstant.MSG_C_ROOM_SETINFO_AI_REMOVE) {
						// pServer("Removing one AI from room " + room.getID());
						room.removeAI();
					}
				}
			}

			/*
			 * TODO send room info MSG_S_ROOM_ROOMINFO to all clients in this
			 * room
			 */

			break;

		}

		case ProtocolConstant.MSG_C_GAME_SENDMOVE: {
			if (packet.getLength() < 9) {
				pServer("Failed to decode readyToPlay request from " + sockAddr);
				return;
			}

			int roomID = recvByteBuffer.getInt(3);
			/*
			 * if (roomID < 0) { pServer("Bug: roomID should not be negative");
			 * }
			 */

			short keyState = recvByteBuffer.getShort(7);
			KeyboardState keyboardState = ServerPacketEncoder.shortToKeyboardState(keyState);

			pServer("Received movement from " + sockAddr + ", roomID: " + roomID + ", direction: "
					+ keyboardState.getMovement() + ", bomb: " + keyboardState.isBomb());

			// check whether the client is already in a room
			ServerClientInfo client = clientTable.get(sockAddr);
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}

			// when the client is not in room yet
			if (!client.isInRoom()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_NOTINROOM, true);
				return;
			}

			// when the client is in room
			ServerRoom room = client.getRoom();
			if (room == null) {
				pServer("Bug: room should not be null in this situation");
				return;
			}

			/*
			 * check whether the room ID in request matches the room ID on
			 * server side
			 */
			if (roomID != room.getID()) {
				pServerf("roomID mismatch: %d(request) != %d(server)\n", roomID, room.getID());

				sendByteBuffer.putInt(3, room.getID());
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// set the keyboard state of the player in game
			ServerGame game = room.getGame();
			if (game != null) {
				game.setPlayerKeyState(client.getID(), keyboardState);
			}

			break;

		}

		// TODO testing cases
		case 's': {
			pServer("Message type 's', table size: " + clientTable.size());

			String reply = "table size: " + clientTable.size() + "\n";
			byte[] replyData = reply.getBytes("UTF-8");

			sendByteBuffer.position(3);
			sendByteBuffer.put(replyData);

			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + replyData.length, sockAddr);
			sendPacket(p, (byte) 'm', true);

			break;
		}

		case 't': {
			long time = System.currentTimeMillis();

			pServerf("Message type 't', time: %d\n", time);

			String reply = "time: " + time + "\n";
			byte[] replyData = reply.getBytes("UTF-8");

			sendByteBuffer.position(3);
			sendByteBuffer.put(replyData);

			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + replyData.length, sockAddr);
			sendPacket(p, (byte) 'm', true);

			break;
		}

		default: {
			pServer("Default case: message type " + String.format("0x%02x", messageType));
		}

		}
	}

	public synchronized void sendPacket(DatagramPacket packet) throws IOException {
		socket.send(packet);
	}

	public synchronized void sendPacket(DatagramPacket packet, byte type) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
		buffer.put(0, (byte) (type & (~ProtocolConstant.MSG_B_HASSEQUENCE)));
		buffer.putShort(1, (short) 0);
		socket.send(packet);
	}

	// Note: retransmission cannot work when recipient is not in client table
	public synchronized void sendPacket(DatagramPacket packet, byte type, boolean tryRetransmit) throws IOException {
		if (tryRetransmit) {
			ServerClientInfo clientInfo = clientTable.get(packet.getSocketAddress());
			if (clientInfo != null) {
				short sequence = clientInfo.getNextPacketSequenceAndIncrement();

				// set the sequence number in the packet before sending
				ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
				buffer.put(0, (byte) (type | ProtocolConstant.MSG_B_HASSEQUENCE));
				buffer.putShort(1, sequence);

				// insert the packet to history
				clientInfo.insertPacket(sequence, packet);

				// send the packet
				socket.send(packet);
			} else {
				// pServer("recipient does not exist in client table but
				// tryRetransmit is set to true");
				sendPacket(packet, type);
			}
		} else {
			sendPacket(packet, type);
		}
	}

	private void pServer(String string) {
		printStream.println("Server: " + string);
	}

	private void pServerf(String string, Object... args) {
		printStream.printf("Server: " + string, args);
	}

	private void recvPacket(DatagramPacket packet) throws IOException {
		socket.receive(packet);
	}

	private void removeClient(ServerClientInfo client) {
		if (client == null) {
			return;
		}

		removeClientFromRoom(client);

		clientTable.remove(client.getSocketAddress());

		// TODO remove references to the client in game (if the player is in a
		// game)
	}

	private void removeClientFromRoom(ServerClientInfo client) {
		if (client == null) {
			return;
		}

		if (!client.isInRoom()) {
			return;
		}

		ServerRoom room = client.getRoom();
		if (room == null) {
			return;
		}

		pServerf("Removing client %s from room\n", client.getSocketAddress());

		if (room.getHumanPlayerNumber() < 2 && room.containsPlayer(client)) {
			pServerf("Removing room %s with ID %d due to %s being the only client in this room\n", room.getName(),
					room.getID(), client.getSocketAddress());

			room.removePlayer(client);
			roomTable.remove(room.getID());
		} else if (room.getHumanPlayerNumber() > 1 && room.containsPlayer(client)) {
			pServerf("Removing client %s from room %s with ID %d\n", client.getSocketAddress(), room.getName(),
					room.getID());

			room.removePlayer(client);
		}

		client.setInRoom(false);
		client.setRoom(null);
		client.setReadyToPlay(false);

		// TODO remove references to the client in game (if the player is in a
		// game)

	}

	public void exit() {
		socket.close();
	}

	public static String toHex(byte[] data, int length) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length; i++) {
			sb.append(String.format("%02x ", data[i]));
		}

		return sb.toString();
	}

}
