package bomber.networking;

public class ServerConfiguration {
	private long clientTimeOut;

	private final long keepAliveInterval;

	private final long retransmitInterval;

	private int maxRetransmitCount;

	private final int maxPlayer;

	private final int maxNameLength = 11;

	/**
	 * Construct a configuration for the server before running
	 * 
	 * @param clientTimeOut
	 *            Max time allowed since last packet from a client in *seconds*
	 * @param keepAliveInterval
	 *            The interval at which the server will check whether clients
	 *            are time-out in *seconds*
	 * @param retransmitInterval
	 *            The interval at which unacknowledged packets will be detected
	 *            and retransmitted in *milliseconds*
	 * @param maxRetransmitCount
	 *            The max number of times the server will attempt to retransmit
	 *            an unacknowledged packet
	 * @param maxPlayer
	 *            The max number of players allowed on the server in the range
	 *            [1,32]
	 */
	public ServerConfiguration(long clientTimeOut, long keepAliveInterval, long retransmitInterval,
			int maxRetransmitCount, int maxPlayer) {
		this.clientTimeOut = clientTimeOut;
		this.keepAliveInterval = keepAliveInterval;
		this.retransmitInterval = retransmitInterval;
		this.maxRetransmitCount = maxRetransmitCount;
		if (maxPlayer < 1 || maxPlayer > 32) {
			this.maxPlayer = 32;
		} else {
			this.maxPlayer = maxPlayer;
		}
	}

	/**
	 * Construct a configuration for the server before running, with default
	 * keepAliveInterval 10 seconds and retransmitInterval 500 milliseconds
	 * 
	 * @param clientTimeOut
	 *            Max time allowed since last packet from a client in *seconds*
	 * @param maxRetransmitCount
	 *            The max number of times the server will attempt to retransmit
	 *            an unacknowledged packet
	 * @param maxPlayer
	 *            The max number of players allowed on the server in the range
	 *            [1,32]
	 */
	public ServerConfiguration(long clientTimeOut, int maxRetransmitCount, int maxPlayer) {
		this.clientTimeOut = clientTimeOut;
		this.keepAliveInterval = 10;
		this.retransmitInterval = 500;
		this.maxRetransmitCount = maxRetransmitCount;
		if (maxPlayer < 1 || maxPlayer > 32) {
			this.maxPlayer = 32;
		} else {
			this.maxPlayer = maxPlayer;
		}
	}

	/**
	 * Construct a configuration for the server before running, with default
	 * clientTimeOut 25 seconds, keepAliveInterval 10 seconds,
	 * retransmitInterval 500 milliseconds, maxRetransmitCount 10 and maxPlayer
	 * 32
	 */
	public ServerConfiguration() {
		this.clientTimeOut = 25;
		this.keepAliveInterval = 10;
		this.retransmitInterval = 500;
		this.maxRetransmitCount = 10;
		this.maxPlayer = 32;
	}

	/**
	 * Max time allowed since last packet from a client. A client will be
	 * considered time-out and removed from server when currentTime -
	 * lastPacketTime > clientTimeOut
	 * 
	 * @return the max time allowed in *seconds*
	 */
	public long getClientTimeOut() {
		return clientTimeOut;
	}

	/**
	 * Set the max time allowed since last packet from a client. A client will
	 * be considered time-out and removed from server when currentTime -
	 * lastPacketTime > clientTimeOut
	 * 
	 * @param clientTimeOut
	 *            the max time allowed in *seconds*
	 */
	public void setClientTimeOut(long clientTimeOut) {
		this.clientTimeOut = clientTimeOut;
	}

	/**
	 * The interval at which the server will check whether clients are time-out
	 * 
	 * @return the interval in *seconds*
	 */
	public long getKeepAliveInterval() {
		return keepAliveInterval;
	}

	/**
	 * The interval at which unacknowledged packets will be detected and
	 * retransmitted
	 * 
	 * @return the interval in *milliseconds*
	 */
	public long getRetransmitInterval() {
		return retransmitInterval;
	}

	/**
	 * The max number of times the server will attempt to retransmit an
	 * unacknowledged packet
	 * 
	 * @return the max number of attempts
	 */
	public int getMaxRetransmitCount() {
		return maxRetransmitCount;
	}

	/**
	 * Set max number of times the server will attempt to retransmit an
	 * unacknowledged packet
	 * 
	 * @param maxRetransmitCount
	 *            the max number of attempts
	 */
	public void setMaxRetransmitCount(int maxRetransmitCount) {
		this.maxRetransmitCount = maxRetransmitCount;
	}

	/**
	 * The max number of players allowed on the server in the range [1,32]
	 * 
	 * @return the max number of players
	 */
	public int getMaxPlayer() {
		return maxPlayer;
	}

	/**
	 * The max allowed length of the name of players and rooms (the minimum
	 * required length is 1)
	 * 
	 * @return the max allowed name length
	 */
	public int getMaxNameLength() {
		return maxNameLength;
	}
}
