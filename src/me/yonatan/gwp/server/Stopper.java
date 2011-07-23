package me.yonatan.gwp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class Stopper {
	public static final String STOP = "stop";

	private static final Logger log = Logger.getLogger(Stopper.class.getName());

	public static void main(String[] args) throws IOException {
		log.info("Stopping server");
		Socket socket = new Socket("localhost", Runner.ADMIN_PORT);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		String inputLine = in.readLine();
		if (!AdminServer.HELLO.equalsIgnoreCase(inputLine)) {
			log.severe("Got bad response from server - " + inputLine);
			return;
		}
		log.info("Sending stop command");
		out.println(STOP);
		if (!AdminServer.DONE.equalsIgnoreCase(inputLine)) {
			log.severe("Got bad response from server - " + inputLine);
			return;
		}
		log.info("Sever recieved stop command succesfully");
	}
}
