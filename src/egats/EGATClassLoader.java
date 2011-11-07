package egats;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Augie
 */
public class EGATClassLoader {

    private Server server;
    private Map<String, ClassLoader> loaders = new HashMap<String, ClassLoader>();

    public EGATClassLoader(Server server) {
        this.server = server;
    }

    public final Server getServer() {
        return server;
    }

    public synchronized final void reload() throws Exception {
        // Clear out the current library (Does there need to be more clean-up here?)
        loaders.clear();
        // Reload all of the libraries
        load();
    }

    public synchronized final void load() throws Exception {
        if (!server.getFlags().contains(Flags.LIB)) {
            throw new Exception("Library flag is not set.");
        }
        String lib = server.getFlags().getString(Flags.LIB);
        File libFile = new File(lib);
        if (!libFile.exists()) {
            throw new Exception("Library directory does not exist: " + libFile.getAbsolutePath());
        }
        // Create a class loader for all JARs in the lib
        for (String filePath : libFile.list()) {
            // Require JAR files
            if (!filePath.toLowerCase().endsWith(".jar")) {
                continue;
            }
            // Already have a loader for this file
            if (loaders.containsKey(filePath)) {
                continue;
            }
            // Create a class loader for this JAR file
            try {
                File file = new File(lib, filePath);
                loaders.put(filePath, new URLClassLoader(new URL[]{file.toURI().toURL()}));
            } catch (Exception e) {
                // Log
                server.logException(e);
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
            // No biggie
        }
        // Try each class loader for loading the path
        for (ClassLoader cl : loaders.values()) {
            try {
                c = cl.loadClass(path);
                // Found it
                continue;
            } catch (ClassNotFoundException e) {
                // No biggie
            }
        }
        // If the class is not found, throw an exception
        if (c == null) {
            throw new ClassNotFoundException("Class not found: " + path);
        }
        return c;
    }
}
