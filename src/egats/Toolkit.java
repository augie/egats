package egats;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class Toolkit {

    private final Server server;
    private final Set<ClassLoader> javaClassLoaders = new HashSet<ClassLoader>();
    private final Set<String> pyScripts = new HashSet<String>();

    static {
        Flags.setDefault(Flags.TOOLKIT, "toolkit");
    }

    public Toolkit(Server server) throws Exception {
        this.server = server;
        load();
    }

    public final String getToolkitDirectoryPath() {
        return server.getFlags().getString(Flags.TOOLKIT);
    }

    public synchronized final void reload() throws Exception {
        // Clear out the current toolkit
        javaClassLoaders.clear();
        pyScripts.clear();
        // Reload the toolkit
        load();
    }

    public synchronized final void load() throws Exception {
        // Create a file handle to the toolkit directory
        File toolkitDir = new File(getToolkitDirectoryPath());
        if (!toolkitDir.exists()) {
            throw new Exception("Toolkit directory does not exist: " + toolkitDir.getAbsolutePath());
        }

        // Create a class loader for each JAR in the toolkit
        for (String fileName : toolkitDir.list()) {
            if (!fileName.toLowerCase().endsWith(".jar")) {
                continue;
            }
            // Create a class loader for this JAR file
            try {
                File file = new File(toolkitDir, fileName);
                javaClassLoaders.add(new URLClassLoader(new URL[]{file.toURI().toURL()}));
            } catch (Exception e) {
                server.logException(e);
            }
        }

        // Note every python script in the toolkit
        loadPyScripts(toolkitDir, toolkitDir);
    }

    private synchronized void loadPyScripts(File toolkitDir, File file) {
        if (file.isFile()) {
            if (file.getName().toLowerCase().endsWith(".py")) {
                // Independent of the location of the toolkit directory
                String pyScript = file.getAbsolutePath().replaceFirst(toolkitDir.getAbsolutePath() + File.separator, "");
                pyScripts.add(pyScript);
            }
        } else {
            for (File listFile : file.listFiles()) {
                loadPyScripts(toolkitDir, listFile);
            }
        }
    }

    public synchronized final Class getClass(String path) throws ClassNotFoundException {
        // Is it something basic?
        Class c = null;
        try {
            c = Class.forName(path);
            return c;
        } catch (ClassNotFoundException e) {
        }

        // Try each class loader for loading the path
        for (ClassLoader cl : javaClassLoaders) {
            try {
                c = cl.loadClass(path);
                continue;
            } catch (ClassNotFoundException e) {
            }
        }

        // If the class is not found, throw an exception
        if (c == null) {
            throw new ClassNotFoundException("Class not found: " + path);
        }

        return c;
    }

    public synchronized final boolean containsPyScript(String pyScript) {
        return pyScripts.contains(pyScript);
    }
}
