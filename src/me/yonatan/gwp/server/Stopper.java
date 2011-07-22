package me.yonatan.gwp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Stopper {
	public static final String STOP = "stop";

	public static void main(String[] args) throws IOException {
		System.out.println("STOPPER");
		Socket socket = new Socket("localhost", Runner.ADMIN_PORT);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		String inputLine = in.readLine();
		System.out.println("Got " + inputLine);
		if (!AdminServer.HELLO.equalsIgnoreCase(inputLine)) {
			System.out.println("Bad server");
			return;
		}
		System.out.println("Writing stop");
		out.println("stop");
		if (!AdminServer.DONE.equalsIgnoreCase(inputLine)) {
			System.out.println("Bad server");
			return;
		}
	}
}
