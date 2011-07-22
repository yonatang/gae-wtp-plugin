package me.yonatan.gwp.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

	public static final String sourceJarName = "appengine-tools-api.jar";

	private static Logger log = Logger.getLogger(GaeJarDropWizard.class
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

		String sourceFilename = serverRootDirectory + File.separatorChar
				+ "lib" + File.separatorChar + sourceJarName;
		String targetFilename = FilenameUtils.removeExtension(sourceFilename)
				+ ".tmp";
		String backupFilename = FilenameUtils.removeExtension(sourceFilename)
				+ ".bak";
		File sourceFile = new File(sourceFilename);

		File targetFile = new File(targetFilename);

		try {
			FileUtils.forceDelete(targetFile);
			writeRuntimeJar(sourceFile, targetFile);

			if (!new File(backupFilename).exists()) {
				sourceFile.renameTo(new File(backupFilename));
			} else {
				sourceFile.delete();
			}
			targetFile.renameTo(new File(sourceFilename));
		} catch (Exception e) {
			log.severe("Exception " + e.getMessage());
		}

		log.info(gsr.getServerInstanceProperties().toString());
	}

	private void writeRuntimeJar(File originalFile, File outputFile)
			throws Exception {
		JarFile sourceJar = new JarFile(originalFile);
		JarOutputStream targetJar = new JarOutputStream(
				new BufferedOutputStream(new FileOutputStream(outputFile)));
		// TODO write entries as STORED
		// targetJar.setMethod(JarEntry.STORED);
		try {

			copyJar(sourceJar, targetJar);
			writeHackClasses(targetJar);
		} finally {
			try {
				sourceJar.close();
			} catch (Exception e) {
			}
			try {
				targetJar.close();
			} catch (Exception e) {
			}

		}
	}

	private void writeHackClasses(JarOutputStream targetJar) throws IOException {

		String pack = "me/yonatan/gwp/server/";
		Class<?>[] classes = new Class[] { AdminServer.class, Runner.class,
				Stopper.class, Runner.ShutdownHook.class };
		String[] names = new String[] { pack + "AdminServer.class",
				pack + "Runner.class", pack + "Stopper.class",
				pack + "Runner$ShutdownHook.class" };
		for (int i = 0; i < names.length; i++) {
			InputStream in = classes[i].getResourceAsStream("/" + names[i]);
			System.out.println(names[i] + "  -  " + in + "\n\n");
			targetJar.putNextEntry(new ZipEntry(names[i]));
			IOUtils.copy(in, targetJar);
			targetJar.closeEntry();
			in.close();
		}
	}

	private void copyJar(JarFile sourceJar, JarOutputStream targetJar)
			throws IOException {
		Enumeration<JarEntry> entires = sourceJar.entries();

		while (entires.hasMoreElements()) {
			JarEntry entry = entires.nextElement();
			if (entry.getName().startsWith("me/yonatan/"))
				continue;
			JarEntry targetEntry = new JarEntry(entry.getName());

			targetJar.putNextEntry(targetEntry);
			IOUtils.copy(sourceJar.getInputStream(entry), targetJar);
			targetJar.closeEntry();

		}

	}
}
