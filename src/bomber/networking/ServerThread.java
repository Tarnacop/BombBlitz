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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerThread implements Runnable {
	private int port;
	private PrintStream printStream;

	private ServerConfiguration config;

	private DatagramSocket socket;

	// table for storing client info
	private ConcurrentHashMap<SocketAddress, ServerClientInfo> clientTable = new ConcurrentHashMap<SocketAddress, ServerClientInfo>();

	// 2000 bytes of receiving buffer
	private final int recvBufferLen = 2000;
	private byte[] recvBuffer = new byte[recvBufferLen];
	private ByteBuffer recvByteBuffer = ByteBuffer.wrap(recvBuffer);
	private DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);

	// 2000 bytes of sending buffer
	private final int sendBufferLen = 2000;
	private byte[] sendBuffer = new byte[sendBufferLen];
	private ByteBuffer sendByteBuffer = ByteBuffer.wrap(sendBuffer);

	// scheduled executor for client keep alive and packet retransmission
	ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

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

		// open socket
		socket = new DatagramSocket(port);
		socket.setSoTimeout(0);

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

		// open socket
		socket = new DatagramSocket(port);
		socket.setSoTimeout(0);

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

		// open socket
		socket = new DatagramSocket(port);
		socket.setSoTimeout(0);

	}

	public void run() {
		pServer("now listening on port " + port);

		// set up tasks
		// client keep alive task
		Runnable keepAliveTask = () -> {
			Instant now = Instant.now();
			for (Entry<SocketAddress, ServerClientInfo> e : clientTable.entrySet()) {
				if (now.getEpochSecond() - e.getValue().getTimeStamp() > config.getClientTimeOut()) {
					pServerf("keepAliveTask: removing %s due to timeout\n", e.getKey());
					// TODO need to remove every reference of the client on the
					// server(client table, room list, game sessions, etc)
					clientTable.remove(e.getKey());
				} else {
					// send a ping packet
					try {
						byte[] data = new byte[3];
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
			// Instant now = Instant.now();
			// pServer("retransmitTask");
			for (Entry<SocketAddress, ServerClientInfo> e : clientTable.entrySet()) {
				// pServer("retransmitTask: checking client " + e.getKey());
				ArrayList<PacketHistoryEntry> packetList = e.getValue().getPacketHistoryList();
				for (PacketHistoryEntry f : packetList) {
					if (f != null && !f.isAcked() && f.getRetransmissionCount() < config.getMaxRetransmitCount()) {
						pServerf(
								"retransmitTask: retransmit packet %d created at %d with length %d and retransmission count %d to client %s\n",
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
				socket.close();
				break;
			}
		}
		pServer("exiting");
		System.exit(0);
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

		pServer("received message from " + sockAddr + " with length " + packet.getLength());

		ServerClientInfo clientInfo = clientTable.get(sockAddr);
		if (clientInfo != null) {
			pServer("existing client " + sockAddr + ", rtd: " + clientInfo.getRoundTripDelay());
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
			// packet length check
			if (packet.getLength() < 5) {
				return;
			}
			byte nameLength = recvByteBuffer.get(3);
			if (1 + 2 + 1 + nameLength != packet.getLength()) {
				return;
			}

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

			// get name from packet
			String name = new String(recvBuffer, 4, nameLength, "UTF-8");
			pServer("new client with name " + name + " from " + sockAddr);

			// create and add client
			ServerClientInfo client = new ServerClientInfo(sockAddr);
			client.setName(name);

			clientTable.put(sockAddr, client);

			// tell the client that the connection has been accepted
			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 1 + 2, sockAddr);
			sendPacket(p, ProtocolConstant.MSG_S_NET_ACCEPT, true);

			/*
			 * TODO a client may be rejected when server is full or there is
			 * duplicate name
			 */

			break;
		}

		case ProtocolConstant.MSG_C_NET_DISCONNECT: {
			pServer("deleting client " + sockAddr + " on request");

			// TODO need to remove every reference of the client on the
			// server(client table, room list, game sessions, etc)
			clientTable.remove(sockAddr);

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

			// pServer("message type ACK");

			if (clientInfo != null) {
				for (PacketHistoryEntry e : clientInfo.getPacketHistoryList()) {
					if (e != null && !e.isAcked() && e.getSequence() == recvByteBuffer.getShort(3)) {
						pServerf("setting ACK to true for packet %d to client %s\n", e.getSequence(), sockAddr);
						e.setAcked(true);
						clientInfo.setRoundTripDelay(System.currentTimeMillis() - e.getCreationTimeStamp());
					}
				}
			}

			break;
		}

		case ProtocolConstant.MSG_C_LOBBY_GETROOMLIST: {

			break;
		}

		// testing case
		case 's': {
			pServer("message type 's', table size: " + clientTable.size());

			String reply = "table size: " + clientTable.size() + "\n";
			byte[] replyData = reply.getBytes("UTF-8");

			sendByteBuffer.position(3);
			sendByteBuffer.put(replyData);

			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + replyData.length, sockAddr);
			sendPacket(p, (byte) 'm', true);

			break;
		}

		// testing case
		case 't': {
			long time = System.currentTimeMillis();

			pServerf("message type 't', time: %d\n", time);

			String reply = "time: " + time + "\n";
			byte[] replyData = reply.getBytes("UTF-8");

			sendByteBuffer.position(3);
			sendByteBuffer.put(replyData);

			DatagramPacket p = new DatagramPacket(sendBuffer, 0, 3 + replyData.length, sockAddr);
			sendPacket(p, (byte) 'm', true);

			break;
		}

		default: {
			pServer("default case: message type " + messageType);
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

	// cannot retransmit when recipient is not in client table
	private void sendPacket(DatagramPacket packet, byte type, boolean tryRetransmit) throws IOException {
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
				pServerf("sending with sequence %d to %s\n", sequence, packet.getSocketAddress());
				socket.send(packet);
			} else {
				pServer("recipient does not exist in client table but tryRetransmit is set to true");
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

	public void exit() {
		socket.close();
	}

	public String toHex(byte[] data, int length) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length; i++) {
			sb.append(String.format("%02x", data[i]));
		}

		return sb.toString();
	}

}
