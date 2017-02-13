package bomber.networking;

import java.net.SocketException;
import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) {
		int port = -1;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			System.out.println("Usage: <port>");
			System.exit(1);
		}

		ServerThread server = null;
		try {
			server = new ServerThread(port);
		} catch (SocketException e) {
			System.err.println(e);
			System.exit(1);
		}

		Thread serverThread = new Thread(server);
		serverThread.start();

		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] cmds = line.split("\\s+");
			if (cmds[0].equals("exit")) {
				break;
			} else {
				System.out.println("Invalid command");
			}
		}
		scanner.close();

		server.exit();
	}

}
