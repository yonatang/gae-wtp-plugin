package me.yonatan.gwp.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Runner {

	private static final Logger log = Logger.getLogger(Runner.class.getName());

	static final int ADMIN_PORT = 9999;

	class ShutdownHook extends Thread {
		private final Future<Void> future;

		ShutdownHook(Future<Void> future) {
			this.future = future;
		}

		@Override
		public void run() {
			future.cancel(true);
		}
	}

	// TODO jrebel support - both dir and args

	private static final String entryClass = "com.google.appengine.tools.development.DevAppServerMain";

	private void run(String[] args) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		Class<?> devAppServerMainClass = systemClassLoader
				.loadClass(entryClass);
		Class<?> strArrType = new String[0].getClass();

		Method mainMethod = devAppServerMainClass.getMethod("main", strArrType);
		log.info("Starting GAE server");
		mainMethod.invoke(null, (Object) args);

	}

	public void runAdminServer() throws IOException {

		final Future<Void> future = Executors.newSingleThreadExecutor().submit(
				new AdminServer(ADMIN_PORT, Thread.currentThread()));

		final Thread t = new Thread();
		log.info("Starting gae admin server on port " + ADMIN_PORT);
		t.start();
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(future));
	}

	private Runner() throws IOException {
	}

	public static void main(String[] args) throws Exception {
		Runner runner = new Runner();
		runner.runAdminServer();
		runner.run(args);
	}
}
