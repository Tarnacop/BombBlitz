package bomber.networking;

/**
 * Class representing packets that have been transmitted which requires
 * retransmission if not acknowledged
 * 
 * @author Qiyang Li
 */
public class PacketHistoryEntry {
	// the sequence number of the packet
	private short sequence;

	// the data of the packet
	private byte[] packetData = null;
	// the length of the packet
	private int packetLength = 0;

	// whether the packet has been acknowledged
	private boolean acked = false;
	// number of retransmissions so far
	private int retransmissionCount = 0;

	// the time when the packet entry was created in milliseconds
	private long creationTimeStamp = System.currentTimeMillis();

	/**
	 * Construct a packet history entry
	 * 
	 * @param sequence
	 *            the sequence number of the packet
	 * @param data
	 *            the data of the packet, data will be copied and stored in a
	 *            new array internally
	 * @param length
	 *            the length of the packet
	 */
	public PacketHistoryEntry(short sequence, byte[] data, int length) {
		this.sequence = sequence;
		this.packetData = data.clone();
		this.packetLength = Math.min(data.length, length);
	}

	/**
	 * Reset the parameters of this packet history entry
	 * 
	 * @param sequence
	 *            the sequence number of the packet
	 * @param data
	 *            the data of the packet, data will be copied and stored in a
	 *            new array internally
	 * @param length
	 *            the length of the packet
	 */
	public void reset(short sequence, byte[] data, int length) {
		this.sequence = sequence;

		int minLen = Math.min(data.length, length);
		if (packetData == null || packetData.length < data.length) {
			packetData = data.clone();
		} else {
			for (int i = 0; i < minLen; i++) {
				packetData[i] = data[i];
			}
		}
		packetLength = minLen;

		acked = false;
		retransmissionCount = 0;
		creationTimeStamp = System.currentTimeMillis();
	}

	/**
	 * Get the sequence number of the packet
	 * 
	 * @return the sequence number of the packet
	 */
	public short getSequence() {
		return sequence;
	}

	/**
	 * Get the data of the packet
	 * 
	 * @return the data of the packet
	 */
	public byte[] getPacketData() {
		return packetData;
	}

	/**
	 * Get the length of the packet
	 * 
	 * @return the length of the packet
	 */
	public int getPacketLength() {
		return packetLength;
	}

	/**
	 * Check whether the packet has been acknowledged
	 * 
	 * @return whether the packet has been acknowledged
	 */
	public boolean isAcked() {
		return acked;
	}

	/**
	 * Get the number of retransmissions of the packet
	 * 
	 * @return the number of retransmissions of the packet
	 */
	public int getRetransmissionCount() {
		return retransmissionCount;
	}

	/**
	 * Get the number of retransmissions of the packet and increment the
	 * retransmission counter
	 * 
	 * @return the number of retransmissions of the packet
	 */
	public int getRetransmissionCountAndIncrement() {
		return retransmissionCount++;
	}

	/**
	 * Get the time when the packet entry was created
	 * 
	 * @return the time when the packet was created in milliseconds
	 */
	public long getCreationTimeStamp() {
		return creationTimeStamp;
	}

	/**
	 * Set whether the packet has been acknowledged
	 * 
	 * @param acked
	 *            whether the packet has been acknowledged
	 */
	public void setAcked(boolean acked) {
		this.acked = acked;
	}

	/**
	 * Increment the retransmission counter
	 */
	public void incrementRetransmissionCount() {
		retransmissionCount++;
	}
}
