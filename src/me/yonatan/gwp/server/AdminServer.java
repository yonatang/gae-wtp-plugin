package me.yonatan.gwp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class AdminServer implements Callable<Void> {
	public static final String DONE = "done";
	public static final String UNKNOWN = "unkown";
	public static final String HELLO = "hello?";

	private static final Logger log=Logger.getLogger(AdminServer.class.getName());
	
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
			//TODO more robust server, that doesn't hangs after one connection
			ServerSocket ss = new ServerSocket(adminPort);

			log.info("Starting Admin server");
			Socket client = ss.accept();
			log.info("Admin server got request");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					client.getOutputStream()), true);
			String line;
			out.println(HELLO);
			line = in.readLine();
			log.info("Got "+line+" command");
			if (Stopper.STOP.equalsIgnoreCase(line)) {
				log.info("Stopping GAE server");
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
			log.severe("Problem in the admin server - "+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
