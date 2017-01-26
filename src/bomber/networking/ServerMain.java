package bomber.networking;

import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) {
		int port = 1423;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}

		ServerThread server = new ServerThread(port);
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
