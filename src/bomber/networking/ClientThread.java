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

public class ClientThread implements Runnable {
	private final PrintStream printStream;

	private final InetSocketAddress serverSockAddr;

	// optional: wrap the variables below into a configuration object
	// time out value for server
	// currently 25 seconds only, for testing
	private long serverTimeOut = 25;
	// the interval at which the client will test whether the connection is
	// timeout
	private long keepAliveInterval = 10;
	// the interval at which the client will request the latest list of players
	// and rooms from the server
	private long listRequestInterval = 10;
	// unacknowledged packets will be detected and retransmitted every 500ms
	private long retransmitInterval = 500;
	// maximum number of retransmissions per packet
	private int maxRetransmitCount = 20;

	// the socket for client
	private final DatagramSocket socket;

	// history of packets sent to server
	private final ClientServerInfo serverInfo;

	// whether the connection has been established
	private boolean connected = false;

	// whether the client is in room(a client can either in lobby or in room)
	private boolean inRoom = false;
	private int roomID = -1;

	// list of players connected to the server
	private List<ClientServerPlayer> playerList = new ArrayList<ClientServerPlayer>();

	// list of rooms in the server lobby
	private List<ClientServerLobbyRoom> roomList = new ArrayList<ClientServerLobbyRoom>();

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
		// server keep alive task
		Runnable keepAliveTask = () -> {
			if (!isConnected()) {
				setInRoom(false, -1);
				return;
			}

			Instant now = Instant.now();
			if (now.getEpochSecond() - serverInfo.getTimeStamp() > serverTimeOut) {
				pClient("keepAliveTask: warning, connection to server possibly timeout, set connected to false");
				// TODO do something when server timeout
				setConnected(false);

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

		// player list and room request task
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
		};
		scheduledExecutor.scheduleWithFixedDelay(listRequestTask, 0, listRequestInterval, TimeUnit.SECONDS);

		// server packet acknowledgement checking and retransmission task
		Runnable retransmitTask = () -> {
			ArrayList<PacketHistoryEntry> packetList = serverInfo.getPacketHistoryList();
			for (PacketHistoryEntry f : packetList) {
				if (f != null && !f.isAcked() && f.getRetransmissionCount() < maxRetransmitCount) {
					pClientf(
							"retransmitTask: retransmit packet %d created at %d with length %d and retransmission count %d to server %s\n",
							f.getSequence(), f.getCreationTimeStamp(), f.getPacketLength(),
							f.getRetransmissionCountAndIncrement(), serverSockAddr);
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
				socket.close();
				break;
			}
		}
		pClient("exiting");
		System.exit(0);
	}

	private void processPacket(DatagramPacket packet) throws IOException {
		if (packet.getLength() < 3) {
			// ignore short packets
			return;
		}

		SocketAddress sockAddr = packet.getSocketAddress();

		if (sockAddr.equals(serverSockAddr)) {
			// pClient("received message from " + sockAddr + " with length " +
			// packet.getLength());
			serverInfo.updateTimeStamp();
		} else {
			pClient("unexpected packet from " + sockAddr);
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
		}

		switch (messageType) {
		case ProtocolConstant.MSG_S_NET_ACCEPT: {
			pClient("connection has been accepted by the server");

			// TODO do something
			setConnected(true);
			setInRoom(false, -1);

			break;
		}

		case ProtocolConstant.MSG_S_NET_REJECT: {
			pClient("connection has been rejected by the server");

			// TODO do something
			setConnected(false);

			break;
		}

		case ProtocolConstant.MSG_S_NET_ALREADYCONNECTED: {
			pClient("you have already connected to the server");

			// TODO do something
			setConnected(true);

			break;
		}

		case ProtocolConstant.MSG_S_NET_NOTCONNECTED: {
			pClient("you have not connected to the server, setting connected to false");

			// TODO do something
			setConnected(false);

			break;
		}

		case ProtocolConstant.MSG_S_NET_DISCONNECTED: {
			pClient("you have disconnected from the server, setting connected to false");

			// TODO do something
			setConnected(false);

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
			// pClient("received player list from server");

			try {
				playerList = ClientPacketEncoder.decodePlayerList(recvBuffer, packet.getLength());
			} catch (IOException e) {
				pClient("failed to decode player list: " + e);
				return;
			}

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_ROOMLIST: {
			// pClient("received room list from server");

			try {
				roomList = ClientPacketEncoder.decodeRoomList(recvBuffer, packet.getLength());
			} catch (IOException e) {
				pClient("failed to decode room list: " + e);
				return;
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

			pClient("room creation/join has been accepted, room ID: " + roomID);

			setInRoom(true, roomID);

			// TODO do something

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_ROOMREJECT: {
			pClient("room creation/join has been rejected by the server");

			// TODO do something

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_NOTINROOM: {
			pClient("you are not in a room");

			setInRoom(false, -1);

			// TODO do something

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

			pClient("you are already in room with ID " + roomID);

			setInRoom(true, roomID);

			// TODO do something

			break;
		}

		case ProtocolConstant.MSG_S_ROOM_HAVELEFT: {
			pClient("you have left the room");

			setInRoom(false, -1);

			// TODO do something

			break;
		}

		// TODO testing case
		case 'm': {

			String msg = new String(recvBuffer, 3, packet.getLength() - 3);
			pClient(msg);

			break;
		}

		default: {
			pClient("default case: message type " + messageType);
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

	private void pClientf(String string, Object... args) {
		printStream.printf("Client: " + string, args);
	}

	private synchronized void setConnected(boolean isConnected) {
		if (isConnected) {
			this.connected = true;
		} else {
			this.connected = false;
			setInRoom(false, -1);
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
	}

	public synchronized void sendRaw(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverSockAddr);
		socket.send(packet);
	}

	/**
	 * Send a connection request to the server with a nickname
	 * 
	 * @param name
	 *            the nickname to use
	 * @throws IOException
	 */
	public synchronized void connect(String name) throws IOException {
		// ignore null or empty name
		if (name == null || name.length() < 1) {
			throw new IOException("name cannot be null or zero length");
		}
		byte[] nameData = name.getBytes("UTF-8");

		// prepare the buffer
		publicSendByteBuffer.put(3, (byte) nameData.length);
		publicSendByteBuffer.position(4);
		publicSendByteBuffer.put(nameData);

		// send the packet
		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 1 + nameData.length, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_NET_CONNECT, true);
	}

	/**
	 * Send a request to the server for the latest list of players
	 * 
	 * @throws IOException
	 */
	public synchronized void updatePlayerList() throws IOException {
		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_LOBBY_GETPLAYERLIST, true);
	}

	/**
	 * Send a request to the server for the latest list of rooms
	 * 
	 * @throws IOException
	 */
	public synchronized void updateRoomList() throws IOException {
		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_LOBBY_GETROOMLIST, true);
	}

	/**
	 * Send a disconnection request to the server
	 * 
	 * @throws IOException
	 */
	public synchronized void disconnect() throws IOException {
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
		return !inRoom;
	}

	/**
	 * Get the latest list of players received from the server. Client will also
	 * automatically request the list periodically
	 * 
	 * @return a list of ClientServerPlayer objects
	 */
	public List<ClientServerPlayer> getPlayerList() {
		return playerList;
	}

	/**
	 * Get the latest list of rooms received from the server. Client will also
	 * automatically request the list periodically when it is NOT in a room(when
	 * it is in lobby)
	 * 
	 * @return a list of ClientServerLobbyRoom objects
	 */
	public List<ClientServerLobbyRoom> getRoomList() {
		return roomList;
	}

	/**
	 * Get the ID of the room the client is currently in
	 * 
	 * @return a non-negative room ID, or -1 when the client is not in a room
	 */
	public int getRoomID() {
		if (!inRoom) {
			roomID = -1;
		}

		return roomID;
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
	public synchronized void createRoom(String roomName, byte maxPlayer, int mapID) throws IOException {
		if (roomName == null || roomName.length() < 1) {
			throw new IOException("name cannot be null or have zero length");
		}
		byte[] nameData = roomName.getBytes("UTF-8");

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.put((byte) nameData.length);
		publicSendByteBuffer.put(nameData);
		publicSendByteBuffer.put(maxPlayer);
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
		if (inRoom) {
			pClient("warning: client is possibly already in a room");
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
		if (!inRoom) {
			pClient("warning: client is possibly already not in a room");
		}

		// prepare the buffer
		publicSendByteBuffer.position(3);
		publicSendByteBuffer.putInt(this.roomID);

		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2 + 4, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_ROOM_LEAVE, true);
	}

	/**
	 * Terminate the thread. It is advisable to leave the room and disconnect
	 * from server first
	 */
	public synchronized void exit() {
		socket.close();
	}

}
