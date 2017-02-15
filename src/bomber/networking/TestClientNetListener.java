package bomber.networking;

public class TestClientNetListener implements ClientNetInterface {

	private ClientThread client;

	public TestClientNetListener(ClientThread client) {
		this.client = client;
	}

	@Override
	public void disconnected() {
		System.out.println("Client has disconnected from the server");
	}

	@Override
	public void connectionAccepted() {
		System.out.println("Client has connected to the server successfully");
	}

	@Override
	public void connectionRejected() {
		System.out.println("Client connection has been rejected by the server");
	}

	@Override
	public void alreadyConnected() {
		System.out.println("Client has already connected to the server");
	}

	@Override
	public void notConnected() {
		System.out.println("Client has not connected to the server");
	}

	@Override
	public void playerListReceived() {
		// System.out.println("Client has received a player list from the
		// server");
	}

	@Override
	public void roomListReceived() {
		// System.out.println("Client has received a room list from the
		// server");
	}

	@Override
	public void roomAccepted() {
		System.out.println("Client has been accepted into room " + client.getRoomID());
	}

	@Override
	public void roomRejected() {
		System.out.println("Client has been rejected to create/join a room");
	}

	@Override
	public void notInRoom() {
		System.out.println("Client is not in a room yet");
	}

	@Override
	public void alreadyInRoom() {
		System.out.println("Client is already in room " + client.getRoomID());
	}

	@Override
	public void haveLeftRoom() {
		System.out.println("Client has left the room");
	}

	@Override
	public void gameStarted() {
		System.out.println("Client has received game start message from the server");
	}

	@Override
	public void gameStateReceived() {
		System.out.println("Client has received a game state from the server");
		System.out.println(client.getGameState());
	}

	@Override
	public void gameEnded() {
		System.out.println("Client has received game end message from the server");
	}

}
