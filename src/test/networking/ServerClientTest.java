package test.networking;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.ClientThread;
import bomber.networking.ServerConfiguration;
import bomber.networking.ServerThread;

public class ServerClientTest {
	private ServerThread server;
	private ClientThread client;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		int port = 1423;

		ServerConfiguration config = new ServerConfiguration();
		config.setTickRate(60);

		try {
			server = new ServerThread(port, config);
		} catch (SocketException e) {
			fail(e.toString());
		}

		Thread serverThread = new Thread(server);
		serverThread.start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		try {
			client = new ClientThread("127.0.0.1", port);
		} catch (SocketException e) {
			System.out.println(e);
			System.exit(1);
		}

		Thread clientThread = new Thread(client);
		clientThread.start();

		try {
			client.connect("P1");
			client.disconnect();
			client.exit();
		} catch (IOException e) {
			fail(e.toString());
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		server.exit();

	}

}
