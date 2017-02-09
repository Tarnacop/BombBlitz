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

	// private String hostname;
	// private int port;
	private final InetSocketAddress serverSockAddr;

	// TODO wrap the variables below into a configuration object
	// time out value for server
	// currently 25 seconds only, for testing
	private long serverTimeOut = 25;
	private long keepAliveInterval = 10;
	// unacknowledged packets will be detected and retransmitted every 500ms
	private long retransmitInterval = 500;
	// maximum number of retransmissions per packet
	private int maxRetransmitCount = 10;

	// the socket for client
	private final DatagramSocket socket;

	// history of packets sent to server
	private final ClientServerInfo serverInfo;

	// whether the connection has been established
	private boolean connected = false;

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
			if (!connected) {
				return;
			}

			Instant now = Instant.now();
			if (now.getEpochSecond() - serverInfo.getTimeStamp() > serverTimeOut) {
				pClient("keepAliveTask: warning, server possibly timeout, set connected to false");
				// TODO do something when server timeout
				connected = false;

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
			connected = true;

			break;
		}

		case ProtocolConstant.MSG_S_NET_REJECT: {
			pClient("connection has been rejected by the server");

			// TODO do something
			connected = false;

			break;
		}

		case ProtocolConstant.MSG_S_NET_ALREADYCONNECTED: {
			pClient("you have already connected to the server");

			// TODO do something
			connected = true;

			break;
		}

		case ProtocolConstant.MSG_S_NET_NOTCONNECTED: {
			pClient("you have not connected to the server, setting connected to false");

			// TODO do something
			connected = false;

			break;
		}

		case ProtocolConstant.MSG_S_NET_DISCONNECTED: {
			pClient("you have disconnected from the server, setting connected to false");

			// TODO do something
			connected = false;

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
			pClient("received player list from server");

			try {
				playerList = ClientPacketEncoder.decodePlayerList(recvBuffer, packet.getLength());
			} catch (IOException e) {
				pClient("failed to decode player list: " + e);
				return;
			}

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_ROOMLIST: {
			pClient("received room list from server");

			try {
				roomList = ClientPacketEncoder.decodeRoomList(recvBuffer, packet.getLength());
			} catch (IOException e) {
				pClient("failed to decode room list: " + e);
				return;
			}

			break;
		}

		case ProtocolConstant.MSG_S_LOBBY_ROOMREJECT: {
			pClient("room creation has been rejected by the server");

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

	public synchronized void sendRaw(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverSockAddr);
		socket.send(packet);
	}

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

	public synchronized void updatePlayerList() throws IOException {
		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_LOBBY_GETPLAYERLIST, true);
	}

	public synchronized void updateRoomList() throws IOException {
		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_LOBBY_GETROOMLIST, true);
	}

	public synchronized void disconnect() throws IOException {
		DatagramPacket p = new DatagramPacket(publicSendBuffer, 0, 1 + 2, serverSockAddr);
		sendPacket(p, ProtocolConstant.MSG_C_NET_DISCONNECT, true);
	}

	public boolean isConnected() {
		return connected;
	}

	public List<ClientServerPlayer> getPlayerList() {
		return playerList;
	}

	public List<ClientServerLobbyRoom> getRoomList() {
		return roomList;
	}

	public synchronized void createRoom(String roomName, byte maxPlayer, int mapID) throws IOException {
		if (roomName == null || roomName.length() < 1) {
			throw new IOException("name cannot be null or zero length");
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

	public synchronized void exit() {
		socket.close();
	}

}
