package bomber.networking;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bomber.AI.AIDifficulty;
import bomber.game.Block;
import bomber.game.KeyboardState;
import bomber.game.Map;
import bomber.game.Maps;

/**
 * Thread for server networking
 * 
 * @author Qiyang Li
 */
public class ServerThread implements Runnable {
	private final int port;

	private final PrintStream printStream;

	private final ServerConfiguration config;

	private final DatagramSocket socket;

	// table for storing nonce
	private final Hashtable<SocketAddress, Long> nonceTable;
	private final SecureRandom random = new SecureRandom();

	// table for storing client info
	private final ServerClientTable clientTable;

	// table for storing room info
	private final ServerRoomTable roomTable;

	// list of maps
	private List<Map> mapList;

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
		this.nonceTable = new Hashtable<>(config.getMaxPlayer());
		this.clientTable = new ServerClientTable(config.getMaxPlayer());
		this.roomTable = new ServerRoomTable(config.getMaxPlayer());
		initMaps();

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
		this.nonceTable = new Hashtable<>(config.getMaxPlayer());
		this.clientTable = new ServerClientTable(config.getMaxPlayer());
		this.roomTable = new ServerRoomTable(config.getMaxPlayer());
		initMaps();

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
		this.nonceTable = new Hashtable<>(config.getMaxPlayer());
		this.clientTable = new ServerClientTable(config.getMaxPlayer());
		this.roomTable = new ServerRoomTable(config.getMaxPlayer());
		initMaps();

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
					 * remove every reference of the client on the server(client
					 * table, room list and game sessions)
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
						/*
						 * pServerf(
						 * "retransmitTask: Retransmitting packet %d created at %d with length %d and retransmission count %d to client %s\n"
						 * , f.getSequence(), f.getCreationTimeStamp(),
						 * f.getPacketLength(),
						 * f.getRetransmissionCountAndIncrement(), e.getKey());
						 */
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
				// terminate active game sessions
				for (Entry<Integer, ServerRoom> e1 : roomTable.entrySet2()) {
					if (e1 != null && e1.getValue() != null && e1.getValue().getGame() != null) {
						e1.getValue().getGame().terminate();
					}
				}
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

		// Steam-compatible A2S_INFO query response
		if (packet.getLength() == 25 && recvByteBuffer.getInt(0) == 0xffffffff && recvByteBuffer.get(24) == 0) {
			if (recvByteBuffer.getLong(4) == 0x54536F7572636520l && recvByteBuffer.getLong(12) == 0x456E67696E652051l
					&& recvByteBuffer.getInt(20) == 0x75657279) {
				sendSteamQueryResponse(sockAddr);
				return;
			}
		}

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
			/*
			 * log the sequence number of last 100 received packets from this
			 * client and drop duplicate packets based on the sequence number
			 */
			if (messageHasSequence && clientInfo.isSequenceDuplicate(messageSequence)) {
				return;
			}
		} else {
			/*
			 * if the client does not exist in the client table and the message
			 * type is not MSG_C_NET_GETNONCE or MSG_C_NET_CONNECT, tell the
			 * client it has not connected to the server yet
			 */
			if (messageType == ProtocolConstant.MSG_C_NET_GETNONCE) {
				// ignore short packets
				if (packet.getLength() < 11) {
					return;
				}

				// generate the nonce
				long nonce;
				Long n = nonceTable.get(sockAddr);
				if (n == null) {
					nonce = random.nextLong();
				} else {
					nonce = n.longValue();
				}

				// if the nonce table grows too large, empty it first
				if (nonceTable.size() > 10 * config.getMaxPlayer()) {
					nonceTable.clear();
				}

				// store the nonce
				nonceTable.put(sockAddr, nonce);

				// send the nonce
				sendByteBuffer.putLong(3, nonce);
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 8, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_NET_NONCE, false);

				return;

			} else if (messageType != ProtocolConstant.MSG_C_NET_CONNECT) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_NET_NOTCONNECTED, false);

				return;
			}
		}

		/*
		 * only new clients with message type MSG_C_NET_CONNECT or connected
		 * clients can reach here
		 */

		switch (messageType) {
		case ProtocolConstant.MSG_C_NET_GETNONCE: {
			if (clientInfo != null) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_NET_ALREADYCONNECTED, true);
			} else {
				System.out.println("Server bug: this line should be unreachable");
			}

			break;
		}

		case ProtocolConstant.MSG_C_NET_CONNECT: {
			// packet length check
			if (packet.getLength() < 13) {
				return;
			}

			// nonce check
			long nonce = recvByteBuffer.getLong(3);
			Long n = nonceTable.get(sockAddr);
			if (n == null || n.longValue() != nonce) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_NET_REJECT_REASON_OTHER);
				sendPacket(p, ProtocolConstant.MSG_S_NET_REJECT, false);
				return;
			} else {
				/*
				 * nonceTable is cleared after exceeding a particular size
				 */
			}

			// name length check
			byte nameLength = recvByteBuffer.get(11);
			if (1 + 2 + 8 + 1 + nameLength != packet.getLength()) {
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_NET_REJECT_REASON_INVALIDNAMELENGTH);
				sendPacket(p, ProtocolConstant.MSG_S_NET_REJECT, false);
				return;
			}

			// only valid packets can reach here

			// existence check
			ServerClientInfo client = clientInfo;
			if (client != null) {
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_NET_REJECT_REASON_SERVERFULL);
				sendPacket(p, ProtocolConstant.MSG_S_NET_REJECT, false);
				return;
			}

			// get name from packet
			String name = new String(recvBuffer, 1 + 2 + 8 + 1, nameLength, "UTF-8");

			// duplicate name check
			if (clientTable.contains(name)) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_NET_REJECT_REASON_DUPLICATENAME);
				sendPacket(p, ProtocolConstant.MSG_S_NET_REJECT, false);
				return;
			}

			// create and add client
			client = new ServerClientInfo(sockAddr, name);
			clientTable.put(client);

			// tell the client that the connection has been accepted
			sendByteBuffer.putInt(3, client.getID());
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_NET_ACCEPT, true);

			pServerf("Accepted new client with name %s, id %d from %s\n", client.getName(), client.getID(),
					client.getSocketAddress());

			break;
		}

		case ProtocolConstant.MSG_C_NET_DISCONNECT: {
			pServer("Deleting client " + sockAddr + " due to disconnection request");

			removeClient(clientInfo);

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
						long lastDelay = clientInfo.getRoundTripDelay();
						long thisDelay = System.currentTimeMillis() - e.getCreationTimeStamp();
						clientInfo.setRoundTripDelay(lastDelay + (long) (0.1f * (float) (thisDelay - lastDelay)));
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT_REASON_INVALIDNAMELENGTH);
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
			ServerClientInfo client = clientInfo;
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// check whether there are more rooms than clients
			if (roomTable.size() >= clientTable.size()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT_REASON_OTHER);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, true);
				return;
			}

			// create the room and update the info of the client
			ServerRoom room = new ServerRoom(roomName, client, mapList, mapID);
			room.setMaxPlayer(maxPlayer);
			roomTable.put(room);
			client.setInRoom(true);
			client.setReadyToPlay(false);
			client.setRoom(room);

			// tell the client it has been accepted into the new room
			sendByteBuffer.putInt(3, room.getID());
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMACCEPT, true);

			// send room info MSG_S_ROOM_ROOMINFO to this client
			int encodedRoomLen = 0;
			try {
				encodedRoomLen = ServerPacketEncoder.encodeRoom(room, sendBuffer);
			} catch (IOException e) {
				pServer("Failed to encode room: " + e);
				return;
			}
			DatagramPacket roomP = new DatagramPacket(sendBuffer, 0, encodedRoomLen, sockAddr);
			sendPacket(roomP, ProtocolConstant.MSG_S_ROOM_ROOMINFO, true);

			break;
		}

		case ProtocolConstant.MSG_C_LOBBY_JOINROOM: {
			if (packet.getLength() < 7) {
				pServer("Failed to decode room join request from " + sockAddr);
				return;
			}
			int roomID = recvByteBuffer.getInt(3);

			pServerf("Room join request from %s, room ID: %d\n", sockAddr, roomID);

			// check whether the client is already in a room
			ServerClientInfo client = clientInfo;
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// check whether the room exists
			ServerRoom room = roomTable.get(roomID);
			if (room == null) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT_REASON_INVALIDROOMID);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, true);
				return;
			}

			// check whether the room is full
			if (room.getPlayerNumber() >= room.getMaxPlayer()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT_REASON_ROOMFULL);
				sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT, true);
				return;
			}

			// check whether the room has a game in progress
			if (room.isInGame()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 1, sockAddr);
				sendByteBuffer.put(3, ProtocolConstant.MSG_S_LOBBY_ROOMREJECT_REASON_GAMEINPROGRESS);
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
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_LOBBY_ROOMACCEPT, true);

			// send room info MSG_S_ROOM_ROOMINFO to all clients in this room
			int encodedRoomLen = 0;
			try {
				encodedRoomLen = ServerPacketEncoder.encodeRoom(room, sendBuffer);
			} catch (IOException e) {
				pServer("Failed to encode room: " + e);
				return;
			}
			for (ServerClientInfo c : room.getHumanPlayers()) {
				DatagramPacket roomP = new DatagramPacket(sendBuffer, 0, encodedRoomLen, c.getSocketAddress());
				sendPacket(roomP, ProtocolConstant.MSG_S_ROOM_ROOMINFO, true);
			}

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
			ServerClientInfo client = clientInfo;
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}

			// when the client is not in room yet
			if (!client.isInRoom()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// remove the client from room
			removeClientFromRoom(client);
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_ROOM_HAVELEFT, true);

			/*
			 * send room info MSG_S_ROOM_ROOMINFO to remaining clients in this
			 * room
			 */
			int encodedRoomLen = 0;
			try {
				encodedRoomLen = ServerPacketEncoder.encodeRoom(room, sendBuffer);
			} catch (IOException e) {
				pServer("Failed to encode room: " + e);
				return;
			}
			for (ServerClientInfo c : room.getHumanPlayers()) {
				DatagramPacket roomP = new DatagramPacket(sendBuffer, 0, encodedRoomLen, c.getSocketAddress());
				sendPacket(roomP, ProtocolConstant.MSG_S_ROOM_ROOMINFO, true);
			}

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
			ServerClientInfo client = clientInfo;
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}

			// when the client is not in room yet
			if (!client.isInRoom()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// update the client info
			client.setReadyToPlay(readyToPlay);

			// send room info MSG_S_ROOM_ROOMINFO to all clients in this room
			int encodedRoomLen = 0;
			try {
				encodedRoomLen = ServerPacketEncoder.encodeRoom(room, sendBuffer);
			} catch (IOException e) {
				pServer("Failed to encode room: " + e);
				return;
			}
			for (ServerClientInfo c : room.getHumanPlayers()) {
				DatagramPacket roomP = new DatagramPacket(sendBuffer, 0, encodedRoomLen, c.getSocketAddress());
				sendPacket(roomP, ProtocolConstant.MSG_S_ROOM_ROOMINFO, true);
			}

			// if readyToPlay is false, the game definitely won't start
			if (!readyToPlay) {
				return;
			}

			// check whether there is already a game in progress
			if (room.isInGame()) {
				return;
			}

			// at least 2 players required to start the game
			if (room.getPlayerNumber() < 2) {
				return;
			}

			// if all clients in this room are ready, create and start the game
			if (room.allPlayersReady()) {
				/*
				 * ServerGame should be responsible for sending updated game
				 * states to clients in this particular game session(game start,
				 * game state and game end messages) while ServerThread will
				 * update the KeyboardState of the players itself
				 */
				// create the game according to the map ID of the room
				room.createGame(config.getTickRate(), this);

				room.getGame().start();
			}

			break;

		}

		case ProtocolConstant.MSG_C_ROOM_SETINFO: {
			if (packet.getLength() < 8) {
				pServer("Failed to decode readyToPlay request from " + sockAddr);
				return;
			}

			int roomID = recvByteBuffer.getInt(3);

			// check whether the client is already in a room
			ServerClientInfo client = clientInfo;
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}

			// when the client is not in room yet
			if (!client.isInRoom()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
				sendPacket(p, ProtocolConstant.MSG_S_ROOM_ALREADYINROOM, true);
				return;
			}

			// room info cannot be changed when game is in progress
			if (room.isInGame()) {
				return;
			}

			// change the room info
			recvByteBuffer.position(7);
			byte changeType = recvByteBuffer.get();
			if (changeType == ProtocolConstant.MSG_C_ROOM_SETINFO_NAME && packet.getLength() > 9) {
				byte nameLength = recvByteBuffer.get();
				if (nameLength < 1 || nameLength > config.getMaxNameLength() || packet.getLength() < 9 + nameLength) {
					return;
				} else {
					String name = new String(recvBuffer, 9, nameLength, "UTF-8");
					room.setName(name);
				}
			} else if (changeType == ProtocolConstant.MSG_C_ROOM_SETINFO_MAXPLAYER && packet.getLength() >= 9) {
				byte maxPlayer = recvByteBuffer.get();
				room.setMaxPlayer(maxPlayer);
			} else if (changeType == ProtocolConstant.MSG_C_ROOM_SETINFO_MAPID && packet.getLength() >= 12) {
				int mapID = recvByteBuffer.getInt();
				room.setMapID(mapID);
			} else if (changeType == ProtocolConstant.MSG_C_ROOM_SETINFO_AI && packet.getLength() >= 9) {
				byte op = recvByteBuffer.get();
				if (op == ProtocolConstant.MSG_C_ROOM_SETINFO_AI_ADD) {
					room.addAI();
				} else if (op == ProtocolConstant.MSG_C_ROOM_SETINFO_AI_REMOVE) {
					room.removeAI();
				} else if (op == ProtocolConstant.MSG_C_ROOM_SETINFO_AI_DIFFICULTY && packet.getLength() >= 11) {
					byte aiID = recvByteBuffer.get();
					byte difficulty = recvByteBuffer.get();
					AIDifficulty aiDifficulty;
					switch (difficulty) {
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
					room.setAIDifficulty(aiID, aiDifficulty);
				} else {
					return;
				}
			} else if (changeType == ProtocolConstant.MSG_C_ROOM_SETINFO_ADDMAP) {
				try {
					Map customMap = ServerPacketEncoder.decodeCustomMap(recvBuffer, packet.getLength());
					room.addCustomMap(customMap);
				} catch (IOException e) {
					pServer("Failed to decode custom map from " + sockAddr + ": " + e);
				}
			} else {
				return;
			}

			// send room info MSG_S_ROOM_ROOMINFO to all clients in this room
			int encodedRoomLen = 0;
			try {
				encodedRoomLen = ServerPacketEncoder.encodeRoom(room, sendBuffer);
			} catch (IOException e) {
				pServer("Failed to encode room: " + e);
				return;
			}
			for (ServerClientInfo c : room.getHumanPlayers()) {
				DatagramPacket roomP = new DatagramPacket(sendBuffer, 0, encodedRoomLen, c.getSocketAddress());
				sendPacket(roomP, ProtocolConstant.MSG_S_ROOM_ROOMINFO, true);
			}

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

			/*
			 * pServer("Received movement from " + sockAddr + ", roomID: " +
			 * roomID + ", direction: " + keyboardState.getMovement() +
			 * ", bomb: " + keyboardState.isBomb());
			 */

			// check whether the client is already in a room
			ServerClientInfo client = clientInfo;
			if (client == null) {
				pServer("Bug: client should not be null in this situation");
				return;
			}

			// when the client is not in room yet
			if (!client.isInRoom()) {
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
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
				DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2 + 4, sockAddr);
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

		default: {
			pServer("Default case: message type " + String.format("0x%02x", messageType));
		}

		}
	}

	private void sendSteamQueryResponse(SocketAddress sockAddr) throws IOException {
		try {
			sendByteBuffer.position(0);
			// header
			sendByteBuffer.putInt(0xffffffff);
			sendByteBuffer.put((byte) 0x49);
			// protocol version
			sendByteBuffer.put((byte) 0x11);
			// name of the server
			sendByteBuffer.put(config.getServerName().getBytes("UTF-8"));
			sendByteBuffer.put((byte) 0);
			// map of the server
			sendByteBuffer.putShort((short) 0x2000);
			// name of the folder
			sendByteBuffer.putShort((short) 0x2000);
			// full name of the game
			sendByteBuffer.put((config.getGameName() + " " + config.getVersion()).getBytes("UTF-8"));
			sendByteBuffer.put((byte) 0);
			// id of game
			sendByteBuffer.putShort((byte) 0);
			// number of players
			sendByteBuffer.put((byte) clientTable.size());
			// maximum number of players
			sendByteBuffer.put((byte) config.getMaxPlayer());
			// number of bots
			sendByteBuffer.put((byte) 0);
			// type of server
			sendByteBuffer.put((byte) 'd');
			// environment of server
			sendByteBuffer.put((byte) 'l');
			// whether the server requires a password
			sendByteBuffer.put((byte) 0);
			// whether the server uses VAC
			sendByteBuffer.put((byte) 0);
			// version of the game
			sendByteBuffer.put(config.getVersion().getBytes("UTF-8"));
			sendByteBuffer.put((byte) 0);
		} catch (BufferOverflowException e) {
			throw new IOException("send buffer is too small for query response");
		}
		DatagramPacket packet = new DatagramPacket(sendBuffer, sendByteBuffer.position(), sockAddr);
		sendPacket(packet);
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

	private synchronized void removeClient(ServerClientInfo client) {
		if (client == null) {
			return;
		}

		removeClientFromRoom(client);

		clientTable.remove(client.getSocketAddress());

	}

	private synchronized void removeClientFromRoom(ServerClientInfo client) {
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

	}

	private void initMaps() {
		try {
			mapList = new Maps().getMaps();
		} catch (Exception e) {
			mapList = null;
		} finally {
			if (mapList == null) {
				mapList = new ArrayList<Map>(1);
			}

			for (int i = 0; i < mapList.size(); i++) {
				if (mapList.get(i) == null) {
					mapList.set(i, defaultMap());
				}
			}

			if (mapList.size() < 1) {
				mapList.add(defaultMap());
			}
		}
	}

	/**
	 * Get the default map that will be used when the map list failed to
	 * initialise
	 * 
	 * @return the default map
	 */
	public Map defaultMap() {
		Block[][] defaultGridMap = new Block[][] { { Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOLID },

				{ Block.SOLID, Block.SOLID, Block.SOFT, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.SOFT, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },

				{ Block.SOLID, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.SOFT, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID } };
		Map defaultMap = new Map("default map", defaultGridMap, null);

		return defaultMap;
	}

	/**
	 * Send a packet without retransmission
	 * 
	 * @param packet
	 *            the packet to be sent
	 * @throws IOException
	 */
	public synchronized void sendPacket(DatagramPacket packet) throws IOException {
		socket.send(packet);
	}

	/**
	 * Send a packet without retransmission
	 * 
	 * @param packet
	 *            the packet to be sent
	 * @param type
	 *            the type of the message in the packet
	 * @throws IOException
	 */
	public synchronized void sendPacket(DatagramPacket packet, byte type) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
		buffer.put(0, (byte) (type & (~ProtocolConstant.MSG_B_HASSEQUENCE)));
		buffer.putShort(1, (short) 0);
		socket.send(packet);
	}

	/**
	 * Send a packet
	 * 
	 * @param packet
	 *            the packet to be sent
	 * @param type
	 *            the type of the message in the packet
	 * @param tryRetransmit
	 *            true if retransmission for unacknowledged packet is required.
	 *            Note that retransmission cannot work when recipient is not in
	 *            client table
	 * @throws IOException
	 */
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

	/**
	 * Terminate the server
	 */
	public void exit() {
		pServer("Closing the socket");
		socket.close();
	}

}
