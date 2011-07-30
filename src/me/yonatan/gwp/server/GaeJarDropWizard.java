package me.yonatan.gwp.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.ui.internal.GenericServerRuntimeWizardFragment;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.TaskModel;

@SuppressWarnings("restriction")
public class GaeJarDropWizard extends GenericServerRuntimeWizardFragment {

	public static final String APPENGINE_ADMIN_SERVER_NAME = "lib"
			+ File.separator + "appengine-admin-server.jar";

	public static final String sourceJarName = "appengine-tools-api.jar";

	private static final Logger log = Logger.getLogger(GaeJarDropWizard.class
			.getName());

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		super.performFinish(monitor);
		log.info("performFinsish");
		TaskModel tm = getTaskModel();
		Object o = tm.getObject(TaskModel.TASK_RUNTIME);
		IRuntime runtime = (IRuntime) o;
		GenericServerRuntime gsr = ((GenericServerRuntime) runtime.loadAdapter(
				GenericServerRuntime.class, new NullProgressMonitor()));
		String serverRootDirectory = gsr.getServerInstanceProperties()
				.get("serverRootDirectory").toString();

		String targetJarFilename = serverRootDirectory + File.separator
				+ APPENGINE_ADMIN_SERVER_NAME;

		File targetFile = new File(targetJarFilename);

		try {
			FileUtils.deleteQuietly(targetFile);
			writeRuntimeJar(targetFile);
		} catch (Exception e) {
			log.severe("Exception " + e.getMessage());
		}

		log.info("performFinish done");
	}

	private void writeRuntimeJar(File outputFile) throws Exception {
		JarOutputStream targetJar = new JarOutputStream(
				new BufferedOutputStream(new FileOutputStream(outputFile)));
		try {
			writeHackClasses(targetJar);
		} finally {
			try {
				targetJar.close();
			} catch (Exception e) {
				log.severe("Can't close target file " + e.getMessage());
			}

		}
	}

	private void writeHackClasses(JarOutputStream targetJar) throws IOException {

		Class<?>[] classes = new Class[] { AdminServer.class, Runner.class,
				Stopper.class, Runner.ShutdownHook.class };
		String[] names = new String[] { "AdminServer.class", "Runner.class",
				"Stopper.class", "Runner$ShutdownHook.class" };
		for (int i = 0; i < names.length; i++) {
			Class<?> clazz = classes[i];
			String pack = clazz.getPackage().getName().replace('.', '/') + "/";
			String name = pack + names[i];

			InputStream in = clazz.getResourceAsStream("/" + name);
			targetJar.putNextEntry(new ZipEntry(name));
			IOUtils.copy(in, targetJar);
			targetJar.closeEntry();
			in.close();
		}
	}

}
