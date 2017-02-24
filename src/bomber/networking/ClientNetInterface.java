package bomber.networking;

public interface ClientNetInterface {

	/**
	 * This method will be called when the client has received disconnection
	 * confirmation from the server, or the client has detected that the
	 * connection has been lost due to timeout
	 */
	public void disconnected();

	/**
	 * This method will be called when the client has received connection
	 * accepted message from the server
	 */
	public void connectionAccepted();

	/**
	 * This method will be called when the client has received connection
	 * rejected message from the server
	 */
	public void connectionRejected();

	/**
	 * This method will be called when the client has received message from the
	 * server indicating that it has already established a connection to the
	 * server (usually caused by attempting to connect to the server while it
	 * has already connected successfully)
	 */
	public void alreadyConnected();

	/**
	 * This method will be called when the client has received message from the
	 * server indicating that it has not established a connection to the server
	 * yet (usually caused by sending other types of messages to the server
	 * before establishing a connection)
	 */
	public void notConnected();

	/**
	 * This method will be called when the client has received a player list
	 * from the server
	 */
	public void playerListReceived();

	/**
	 * This method will be called when the client has received a lobby room list
	 * from the server
	 */
	public void roomListReceived();

	/**
	 * This method will be called when the client has received room join
	 * accepted message from the server
	 */
	public void roomAccepted();

	/**
	 * This method will be called when the client has received room join
	 * rejected message from the server
	 */
	public void roomRejected();

	/**
	 * This method will be called when the client has received message from the
	 * server indicating that it is not in a room yet (usually caused by
	 * attempting to leave a room, send readyToPlay or send a move while it is
	 * still in lobby)
	 */
	public void notInRoom();

	/**
	 * This method will be called when the client has received message from the
	 * server indicating that it is already in a room (usually caused by
	 * attempting to create a room, join a room while it is still in lobby, or
	 * when the room ID stored on client side mismatches the room ID on server
	 * side. The client will correct the room ID automatically when received
	 * this type of message)
	 */
	public void alreadyInRoom();

	/**
	 * This method will be called when the client has received room leave
	 * confirmation message from the server
	 */
	public void haveLeftRoom();

	/**
	 * This method will be called when the client has received updated info of
	 * the room that the client is in from the server
	 */
	public void roomReceived();

	/**
	 * This method will be called when the client has received message from the
	 * server indicating the game has started
	 */
	public void gameStarted();

	/**
	 * This method will be called when the client has received a game state from
	 * the server
	 */
	public void gameStateReceived();

	/**
	 * This method will be called when the client has received message from the
	 * server indicating the game has ended
	 */
	public void gameEnded();

}
