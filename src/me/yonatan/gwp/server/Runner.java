package me.yonatan.gwp.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Runner {

	private static final Logger logger = Logger.getLogger(Runner.class
			.getName());

	static final int ADMIN_PORT = 9999;

	// TODO jrebel support - both dir and args

	private static final String entryClass = "com.google.appengine.tools.development.DevAppServerMain";
	private Process serverProcess = null;

	private void run(String[] args) {
		ProcessBuilder builder = new ProcessBuilder(new String[0]);
		String home = System.getProperty("java.home");
		String javaExe = home + File.separator + "bin" + File.separator
				+ "java";

		List<String> jvmArgs = new ArrayList<String>();
		ArrayList<String> appServerArgs = new ArrayList<String>();
		List<String> command = builder.command();
		command.add(javaExe);

		boolean startOnFirstThread = System.getProperty("os.name")
				.equalsIgnoreCase("Mac OS X");

		for (int i = 0; i < args.length; ++i) {
			if (args[i].startsWith("--jvm_flag")) {
				int indexOfSplit = args[i].indexOf(61);
				if (indexOfSplit == -1) {
					String msg = "--jvm_flag syntax is --jvm_flag=<flag>\n--jvm_flag may be repeated to supply multiple flags";

					throw new IllegalArgumentException(msg);
				}
				String jvmArg = args[i].substring(indexOfSplit + 1);
				jvmArgs.add(jvmArg);
			} else if (args[i].startsWith("--startOnFirstThread=")) {
				String arg = args[i].substring(args[i].indexOf(61) + 1);
				startOnFirstThread = Boolean.valueOf(arg).booleanValue();
			} else {
				appServerArgs.add(args[i]);
			}
		}

		if (entryClass == null) {
			throw new IllegalArgumentException("missing entry classname");
		}
		// TODO
		// File newWorkingDir = newWorkingDir(args);
		// builder.directory(newWorkingDir);

		if (startOnFirstThread) {
			jvmArgs.add("-XstartOnFirstThread");
		}

		String classpath = System.getProperty("java.class.path");
		StringBuffer newClassPath = new StringBuffer();
		assert (classpath != null) : "classpath must not be null";
		String[] paths = classpath.split(File.pathSeparator);
		for (int i = 0; i < paths.length; ++i) {
			newClassPath.append(new File(paths[i]).getAbsolutePath());
			if (i != paths.length - 1) {
				newClassPath.append(File.pathSeparator);
			}
		}

		String sdkRoot = null;

		List<String> absoluteAppServerArgs = new ArrayList<String>(
				appServerArgs.size());

		for (int i = 0; i < appServerArgs.size(); ++i) {
			String arg = appServerArgs.get(i);
			if (arg.startsWith("--sdk_root=")) {
				sdkRoot = new File(arg.split("=")[1]).getAbsolutePath();
				arg = "--sdk_root=" + sdkRoot;
			} else if (i == appServerArgs.size() - 1) {
				arg = new File(arg).getAbsolutePath();
			}
			absoluteAppServerArgs.add(arg);
		}

		String agentJar = sdkRoot + "/lib/agent/appengine-agent.jar";
		agentJar = agentJar.replace('/', File.separatorChar);
		jvmArgs.add("-javaagent:" + agentJar);

		String jdkOverridesJar = sdkRoot
				+ "/lib/override/appengine-dev-jdk-overrides.jar";
		jdkOverridesJar = jdkOverridesJar.replace('/', File.separatorChar);
		jvmArgs.add("-Xbootclasspath/p:" + jdkOverridesJar);

		command.addAll(jvmArgs);
		command.add("-classpath");
		command.add(newClassPath.toString());
		System.out.println(newClassPath.toString());
		command.add(entryClass);
		command.addAll(absoluteAppServerArgs);

		logger.fine("Executing " + command);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (Runner.this.serverProcess != null) {
					Runner.this.serverProcess.destroy();
				}
			}
		});
		try {
			this.serverProcess = builder.start();
		} catch (IOException e) {
			throw new RuntimeException("Unable to start the process", e);
		}

		new Thread(new OutputPump(this.serverProcess.getInputStream(),
				new PrintWriter(System.out, true))).start();

		new Thread(new OutputPump(this.serverProcess.getErrorStream(),
				new PrintWriter(System.err, true))).start();
		try {
			this.serverProcess.waitFor();
		} catch (InterruptedException e) {
		}
		this.serverProcess.destroy();
		this.serverProcess = null;
	}

	public void runAdminServer() throws IOException {
		final Future<Void> future = Executors.newSingleThreadExecutor().submit(
				new AdminServer(ADMIN_PORT, serverProcess));

		final Thread t = new Thread();
		t.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				future.cancel(true);
			}
		});
	}

	private Runner() throws IOException {
	}

	public static void main(String[] args) throws Exception {
		Runner runner = new Runner();
		runner.runAdminServer();
		runner.run(args);
	}
}
