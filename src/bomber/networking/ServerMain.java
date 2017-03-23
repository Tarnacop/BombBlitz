package bomber.networking;

import java.net.SocketException;
import java.util.Scanner;

/**
 * Main server class
 * 
 * @author Qiyang Li
 */
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

				} else if (cmds[0].equals("h")) {

					pUsage();

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
		System.out.println("Invalid command, type \"h\" to list available commands");
	}

	private static void pUsage() {
		System.out.println("Type \"exit\" to terminate the server");
		System.out.println("Type \"tickrate\" to show the tickrate of the server");
		System.out.println("Type \"tickrate <tickrate>\" to set the tickrate");
	}
}
