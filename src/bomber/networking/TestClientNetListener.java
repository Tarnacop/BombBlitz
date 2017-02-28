package bomber.networking;

import bomber.game.AudioEvent;

public class TestClientNetListener implements ClientNetInterface {

	private ClientThread client;

	public TestClientNetListener(ClientThread client) {
		this.client = client;
	}

	@Override
	public void disconnected() {
		System.out.println("Client has disconnected from the server");
	}

	public void connectionAttemptTimeout() {
		System.out.println("Client has failed to connect to the server due to timeout");
	}

	@Override
	public void connectionAccepted() {
		System.out.println("Client has connected to the server successfully, ID: " + client.getClientID());
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
	public void roomReceived() {
		System.out.println("Client has received updated room info from the server");
		ClientServerRoom room = client.getRoom();
		System.out.printf(
				"room ID: %d, room name: %s, human player: %d, AI player: %d, max player: %d, inGame: %b, map ID: %d\n",
				room.getID(), room.getName(), room.getHumanPlayerNumber(), room.getAIPlayerNumber(),
				room.getMaxPlayer(), room.isInGame(), room.getMapID());
		System.out.println("Human players:");
		for (ClientServerPlayer p : room.getHumanPlayerList()) {
			System.out.printf("player ID: %d, name: %s, ready: %b\n", p.getID(), p.getName(), p.isReadyToPlay());
		}
		System.out.println("AI players:");
		for (ClientServerAI a : room.getAIPlayerList()) {
			System.out.printf("player ID: %d, difficulty: %s\n", a.getID(), a.getDifficulty());
		}
		System.out.println();
	}

	@Override
	public void gameStarted() {
		System.out.printf("Client has received game start message from the server, map ID: %d, width: %d, height: %d\n",
				client.getMapID(), client.getMapWidth(), client.getMapHeight());
	}

	@Override
	public void gameStateReceived() {
		System.out.println("Client has received a game state from the server");
		System.out.println(client.getGameState());
		System.out.println("Number of Audio Events: " + client.getGameState().getAudioEvents().size());
		for (AudioEvent a : client.getGameState().getAudioEvents()) {
			System.out.print(a + " ");
		}
		System.out.println();
	}

	@Override
	public void gameEnded() {
		System.out.println("Client has received game end message from the server");
	}

}
