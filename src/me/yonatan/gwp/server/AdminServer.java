package me.yonatan.gwp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

public class AdminServer implements Callable<Void> {
	public static final String DONE = "done";
	public static final String UNKNOWN = "unkown";
	public static final String HELLO = "hello?";

	private int adminPort;
	private Process gaeServerProcess;

	private Thread gaeServerThread;

	public AdminServer(int adminPort, Process gaeServerProcess) {
		this.adminPort = adminPort;
		this.gaeServerProcess = gaeServerProcess;
	}

	public AdminServer(int adminPort, Thread gaeServerThread) {
		this.adminPort = adminPort;
		this.gaeServerThread = gaeServerThread;
	}

	@Override
	public Void call() throws Exception {
		try {
			ServerSocket ss = new ServerSocket(adminPort);

			System.out.println("Listening");
			Socket client = ss.accept();
			System.out.println("Accepted");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					client.getOutputStream()), true);

			String line;
			out.println(HELLO);
			System.out.println("Waiting for command");
			line = in.readLine();
			System.out.println("got " + line);
			if (Stopper.STOP.equalsIgnoreCase(line)) {
				System.out.println("STOP it!");
				out.println(DONE);
				if (gaeServerProcess != null)
					gaeServerProcess.destroy();
				if (gaeServerThread != null) {
					gaeServerThread.interrupt();
				}

			} else {
				out.println(UNKNOWN);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
