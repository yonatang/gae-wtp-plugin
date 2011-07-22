package me.yonatan.gwp.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.ui.internal.GenericServerRuntimeWizardFragment;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.TaskModel;

@SuppressWarnings("restriction")
public class GaeJarDropWizard extends GenericServerRuntimeWizardFragment {

	private static Logger log = Logger.getLogger(GaeJarDropWizard.class
			.getName());

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		log.info("performFinsish");
		TaskModel tm = getTaskModel();
		Object o = tm.getObject(TaskModel.TASK_RUNTIME);
		IRuntime runtime = (IRuntime) o;
		GenericServerRuntime gsr = ((GenericServerRuntime) runtime.loadAdapter(
				GenericServerRuntime.class, new NullProgressMonitor()));
		String serverRootDirectory = gsr.getServerInstanceProperties()
				.get("serverRootDirectory").toString();

		File file = new File(serverRootDirectory + File.separatorChar + "lib"
				+ File.separatorChar + "hack.jar");
		if (file.exists()) {
			file.delete();
		}

		try {
			writeRuntimeJar(file);
		} catch (Exception e) {
			log.severe("Exception " + e.getMessage());
		}

		log.info(gsr.getServerInstanceProperties().toString());
	}

	private void writeRuntimeJar(File outFile) throws Exception {
		String pack = "me/yonatan/gwp/server/";
		Class<?>[] classes = new Class[] { AdminServer.class, Runner.class,
				Stopper.class };
		String[] names = new String[] { pack + "AdminServer.class",
				pack + "Runner.class", pack + "Stopper.class" };
		OutputStream out = new FileOutputStream(outFile);
		JarOutputStream jar = new JarOutputStream(out);
		for (int i = 0; i < names.length; i++) {
			InputStream in = classes[i].getResourceAsStream("/" + names[i]);
			System.out.println(names[i] + "  -  " + in + "\n\n");
			writeIntoJar(jar, names[i], in);
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(
				"Manifest-Version: 1.0\n".getBytes());
		writeIntoJar(jar, "META-INF/MANIFEST.MF", bis);
		jar.close();
		out.close();

	}

	private void writeIntoJar(JarOutputStream jar, String depName,
			InputStream in) throws IOException {
		jar.putNextEntry(new ZipEntry(depName));
		byte[] b = new byte[65536];
		while (true) {
			int r = in.read(b);
			if (r == -1)
				break;
			jar.write(b, 0, r);
		}
		jar.closeEntry();
		in.close();
	}
}
