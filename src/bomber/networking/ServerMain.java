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

		ServerConfiguration config = new ServerConfiguration();
		config.setTickRate(60);

		ServerThread server = null;
		try {
			server = new ServerThread(port, config);
		} catch (SocketException e) {
			System.out.println(e);
			System.exit(1);
		}

		Thread serverThread = new Thread(server);
		serverThread.start();

		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] cmds = line.split("\\s+");
			if (cmds.length < 1) {

				pInvalid();

			} else if (cmds.length == 1) {

				if (cmds[0].equals("exit")) {

					break;

				} else if (cmds[0].equals("tickrate")) {

					System.out.println(config.getTickRate());

				} else {

					pInvalid();

				}

			} else if (cmds.length == 2) {

				if (cmds[0].equals("tickrate")) {

					int tickRate = config.getTickRate();
					try {
						tickRate = Integer.parseInt(cmds[1]);
					} catch (NumberFormatException e) {
						System.out.println("Failed to parse tick rate");
						continue;
					}

					config.setTickRate(tickRate);

				} else {

					pInvalid();

				}

			} else {

				pInvalid();

			}
		}

		scanner.close();

		server.exit();
	}

	private static void pInvalid() {
		System.out.println("Invalid command");
	}
}
