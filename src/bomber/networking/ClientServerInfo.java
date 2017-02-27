package bomber.networking;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * Client side representation of (the state of) server
 */
public class ClientServerInfo {
	// socket address (IP:Port) of the server
	private SocketAddress sockAddr;

	// time of last packet received from the server in seconds
	private long timeStamp;

	// the next sequence number that should be used for a packet that will be
	// sent to the server
	private short nextPacketSequence = 0;

	// keep track of up to 100 packets sent to the server
	private int nextPacketHistoryListIndex = 0;
	private final int maxPacketHistoryIndex = 99;
	private ArrayList<PacketHistoryEntry> packetHistoryList = new ArrayList<PacketHistoryEntry>(
			maxPacketHistoryIndex + 1);

	/*
	 * keep track of up to 100 packet sequence and time stamp received from the
	 * client for duplicate packet detection
	 */
	private int nextSequenceHistoryIndex = 0;
	private final int maxSequenceHistoryIndex = 99;
	private ArrayList<Entry<Short, Long>> sequenceHistoryList = new ArrayList<>(maxSequenceHistoryIndex + 1);

	/**
	 * Construct a new server representation
	 * 
	 * @param sockAddr
	 *            the socket address of the server
	 */
	public ClientServerInfo(SocketAddress sockAddr) {
		this.sockAddr = sockAddr;
		this.timeStamp = Instant.now().getEpochSecond();
		for (int i = 0; i <= maxPacketHistoryIndex; i++) {
			packetHistoryList.add(null);
		}
		for (int i = 0; i <= maxSequenceHistoryIndex; i++) {
			sequenceHistoryList.add(null);
		}
	}

	/**
	 * Get the socket address (IP:Port) of the server
	 * 
	 * @return the socket address
	 */
	public SocketAddress getSocketAddress() {
		return sockAddr;
	}

	/**
	 * Get time of last packet received from the server in seconds
	 * 
	 * @return the time in seconds
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Get the sequence number that should be used for the next packet to the
	 * server and increment the counter
	 * 
	 * @return the sequence number that should be used
	 */
	public short getNextPacketSequenceAndIncrement() {
		return nextPacketSequence++;
	}

	/**
	 * Get the list of up to 100 recent packets that have been sent to the
	 * server
	 * 
	 * @return the list of packets that have been sent to the server
	 */
	public ArrayList<PacketHistoryEntry> getPacketHistoryList() {
		return packetHistoryList;
	}

	/**
	 * Update the time of last packet received from the server in seconds Should
	 * be called each time a packet is received from the server
	 */
	public void updateTimeStamp() {
		this.timeStamp = Instant.now().getEpochSecond();
	}

	/**
	 * Insert a packet into the packet history list for later retransmission (in
	 * case not acknowledged by the server)
	 * 
	 * @param packetSequence
	 *            the sequence number of the packet
	 * @param packet
	 *            the packet to insert
	 */
	public void insertPacket(short packetSequence, DatagramPacket packet) {
		// index wraps around when there are already 100 packets in history
		// (older packets will be overwritten)
		if (nextPacketHistoryListIndex > maxPacketHistoryIndex) {
			nextPacketHistoryListIndex = 0;
		}

		PacketHistoryEntry phe = packetHistoryList.get(nextPacketHistoryListIndex);
		if (phe == null) {
			packetHistoryList.set(nextPacketHistoryListIndex,
					new PacketHistoryEntry(packetSequence, packet.getData(), packet.getLength()));
		} else {
			phe.reset(packetSequence, packet.getData(), packet.getLength());
		}

		nextPacketHistoryListIndex++;
	}

	/**
	 * Determine whether a packet received from the server is duplicate based on
	 * its sequence number
	 * 
	 * @param sequence
	 *            the sequence number of the packet
	 * @return true if the packet is duplicate
	 */
	public boolean isSequenceDuplicate(short sequence) {
		for (Entry<Short, Long> e : sequenceHistoryList) {
			if (e != null && e.getKey() == sequence) {
				if (System.currentTimeMillis() - e.getValue() < 25000) {
					return true;
				} else {
					e.setValue(System.currentTimeMillis());
					return false;
				}
			}
		}

		if (nextSequenceHistoryIndex > maxSequenceHistoryIndex) {
			nextSequenceHistoryIndex = 0;
		}

		Entry<Short, Long> entry = new AbstractMap.SimpleEntry<Short, Long>(sequence, System.currentTimeMillis());
		sequenceHistoryList.set(nextSequenceHistoryIndex, entry);
		nextSequenceHistoryIndex++;

		return false;
	}

	public String toString() {
		return String.format("SocketAddress: %s, TimeStamp: %d", sockAddr, timeStamp);
	}
}
