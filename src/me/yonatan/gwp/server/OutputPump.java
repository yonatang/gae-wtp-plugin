package me.yonatan.gwp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputPump implements Runnable {
	private BufferedReader stream;
	private PrintWriter output;
	private Logger logger = Logger.getLogger(OutputPump.class.getName());

	public OutputPump(InputStream instream, PrintWriter outstream) {
		this.stream = new BufferedReader(new InputStreamReader(instream));
		this.output = outstream;
	}

	@Override
	public void run() {
		String line = null;
		try {
			while ((line = this.stream.readLine()) != null)
				this.output.println(line);
		} catch (IOException ix) {
			this.logger.log(Level.SEVERE,
					"Unexpected failure while trying to record errors.", ix);
		}
	}

}
