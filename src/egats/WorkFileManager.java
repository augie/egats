package egats;

import java.io.File;
import java.io.FileWriter;

/**
 * Manages the temporary working files of the server.
 * @author Augie Hill - augman85@gmail.com
 */
public class WorkFileManager {

    private final Server server;

    static {
        Flags.setDefault(Flags.WORK, "work");
    }

    public WorkFileManager(Server server) {
        this.server = server;
    }

    public final File getFile(EGATSObject o) throws Exception {
        // Open a file handle to the working directory
        File workDir = new File(server.getFlags().getString(Flags.WORK));

        // Create the working directory if it does not exist
        if (!workDir.exists() && !workDir.mkdirs()) {
            throw new Exception("Could not create working directory: " + workDir.getAbsolutePath());
        }

        // Open a file handle to the objects directory
        File objsDir = new File(workDir.getAbsolutePath(), "objs");

        // Create the working directory if it does not exist
        if (!objsDir.exists() && !objsDir.mkdirs()) {
            throw new Exception("Could not create objects working directory: " + objsDir.getAbsolutePath());
        }

        // Open a file handle to the object file
        File objFile = new File(objsDir.getAbsolutePath(), o.getID());

        // If the file doesn't exist, create one and copy the object in
        if (!objFile.exists()) {
            // Create an empty file
            if (!objFile.createNewFile()) {
                throw new Exception("Could not create object file: " + objFile.getAbsolutePath());
            }
            // Copy the object into the file
            FileWriter writer = null;
            try {
                writer = new FileWriter(objFile);
                writer.write(o.getObject());
            } finally {
                IOUtil.safeClose(writer);
            }
        }

        return objFile;
    }

    public final File getFile(EGATSObject o, EGATSObjectFile of) throws Exception {
        // Open a file handle to the working directory
        File workDir = new File(server.getFlags().getString(Flags.WORK));

        // Create the working directory if it does not exist
        if (!workDir.exists() && !workDir.mkdirs()) {
            throw new Exception("Could not create working directory: " + workDir.getAbsolutePath());
        }

        // Open a file handle to the objects directory
        File objFilesDir = new File(workDir.getAbsolutePath(), "objfiles");

        // Create the working directory if it does not exist
        if (!objFilesDir.exists() && !objFilesDir.mkdirs()) {
            throw new Exception("Could not create object files working directory: " + objFilesDir.getAbsolutePath());
        }

        // Open a file handle to the object file
        File objDir = new File(objFilesDir.getAbsolutePath(), o.getID());

        // Create the working directory if it does not exist
        if (!objDir.exists() && !objDir.mkdirs()) {
            throw new Exception("Could not create object working directory: " + objDir.getAbsolutePath());
        }

        // Open a file handle to the object file
        File objFile = new File(objDir.getAbsolutePath(), of.name);

        // If the file doesn't exist, create one and copy the object in
        if (!objFile.exists()) {
            // Create an empty file
            if (!objFile.createNewFile()) {
                throw new Exception("Could not create object file: " + objFile.getAbsolutePath());
            }
            // Copy the object into the file
            FileWriter writer = null;
            try {
                writer = new FileWriter(objFile);
                writer.write(of.object);
            } finally {
                IOUtil.safeClose(writer);
            }
        }

        return objFile;
    }
}
