package egats;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;

/**
 * Utility for working with tools.
 * Tools can be either Java (given as JARs) or Python scripts.
 * A configuration file says which scripts are executable processes.
 * @author Augie Hill - augie@umich.edu
 */
public class Toolkit {

    private final Server server;
    // Java JARs in the toolkit
    private Set<ClassLoader> javaClassLoaders;
    // Python scripts in the toolkit
    private Set<String> pyScripts;
    // Data structures for the allowed runnable tools
    private List<String> toolList;
    private Set<String> toolSet;

    static {
        Flags.setDefault(Flags.TOOLKIT, "toolkit");
        Flags.setDefault(Flags.TOOLS_CONFIG, ((String) Flags.getDefault(Flags.TOOLKIT)) + "/tools.conf");
    }

    /**
     * 
     * @param server
     * @throws Exception 
     */
    public Toolkit(Server server) throws Exception {
        this.server = server;
        load();
    }

    /**
     * 
     * @return 
     */
    public final String getToolkitDirectoryPath() {
        return server.getFlags().getString(Flags.TOOLKIT);
    }

    /**
     * 
     * @return 
     */
    public final String getToolkitConfigFilePath() {
        return server.getFlags().getString(Flags.TOOLS_CONFIG);
    }

    /**
     * 
     * @return 
     */
    public synchronized final List<String> getTools() {
        return toolList;
    }

    /**
     * 
     * @param tool
     * @return 
     */
    public synchronized final boolean isTool(String tool) {
        return toolSet.contains(tool);
    }

    /**
     * Loads the JAR and py files from the toolkit.
     * Also reads the toolkit configuration file.
     * @throws Exception 
     */
    public synchronized final void load() throws Exception {
        // Create a file handle to the toolkit directory
        File toolkitDir = new File(getToolkitDirectoryPath());
        if (!toolkitDir.exists()) {
            // If testing, make a dummy toolkit
            if (Flags.TESTING) {
                toolkitDir.mkdirs();
            } else {
                throw new Exception("Toolkit directory does not exist: " + toolkitDir.getAbsolutePath());
            }
        }

        // Create a class loader for each JAR in the toolkit
        javaClassLoaders = new HashSet<ClassLoader>();
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
        javaClassLoaders = Collections.unmodifiableSet(javaClassLoaders);

        // Note every python script in the toolkit
        pyScripts = new HashSet<String>();
        loadPyScripts(toolkitDir, toolkitDir);
        pyScripts = Collections.unmodifiableSet(pyScripts);

        // Create a file handle to the toolkit configuration file
        File toolkitConfigFile = new File(getToolkitConfigFilePath());
        // If testing, make a dummy config
        if (Flags.TESTING) {
            FileUtils.copyInputStreamToFile(Toolkit.class.getResourceAsStream("/egats/toolkit/toolkit.conf"), toolkitConfigFile);
        } else if (!toolkitConfigFile.exists()) {
            throw new Exception("Toolkit configuration file does not exist: " + toolkitConfigFile.getAbsolutePath());
        }

        // Read the allowed tools
        String config = FileUtils.readFileToString(toolkitConfigFile);
        String[] tools = config.split("\n");
        toolList = new LinkedList<String>();
        for (String tool : tools) {
            toolList.add(tool.trim());
        }
        toolList = Collections.unmodifiableList(toolList);
        toolSet = new HashSet<String>();
        toolSet.addAll(toolList);
        toolSet = Collections.unmodifiableSet(toolSet);
    }

    /**
     * Recursively loads all of the Python scripts in the toolkit.
     * @param toolkitDir
     * @param file 
     */
    private synchronized void loadPyScripts(File toolkitDir, File file) {
        if (file.isFile()) {
            if (file.getName().toLowerCase().endsWith(".py")) {
                // Independent of the location of the toolkit directory
                String pyScript = file.getAbsolutePath().replace(toolkitDir.getAbsolutePath() + File.separator, "");
                pyScripts.add(pyScript.replace("\\", "/"));
            }
        } // It's a directory
        else {
            for (File listFile : file.listFiles()) {
                loadPyScripts(toolkitDir, listFile);
            }
        }
    }

    /**
     * Get the class with the given path.
     * First tries the local class loader, then tries each of the class loaders
     * from the JAR files in the toolkit.
     * @param path
     * @return 
     */
    public synchronized final Class getClass(String path) {
        // Is it something basic?
        Class c = null;
        try {
            c = Class.forName(path);
            if (c != null) {
                return c;
            }
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

        return c;
    }

    /**
     * 
     * @param pyScript
     * @return 
     */
    public synchronized final boolean containsPyScript(String pyScript) {
        return pyScripts.contains(pyScript);
    }
}
