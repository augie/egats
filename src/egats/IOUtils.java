package egats;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Contains commonly used utility methods related to input and output not
 * available in Apache Commons IO.
 * @author Augie Hill - augie@umich.edu
 */
public class IOUtils extends org.apache.commons.io.IOUtils {

    /**
     * Reads the contents of a resource in the Java class path.
     * @param resource
     * @return
     * @throws IOException 
     */
    public static String toString(String resource) throws IOException {
        return toString(IOUtils.class.getResourceAsStream(resource));
    }

    /**
     * Also flushes the output stream before closing.
     * @param output
     */
    public static void closeQuietly(OutputStream output) {
        if (output == null) {
            return;
        }
        try {
            output.flush();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
        try {
            output.close();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
    }

    /**
     * Also flushes the writer before closing.
     * @param writer
     */
    public static void closeQuietly(Writer writer) {
        if (writer == null) {
            return;
        }
        try {
            writer.flush();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
        try {
            writer.close();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
    }

    /**
     * Closes the server without raising an exception.
     * @param server
     */
    public static void closeQuietly(Server server) {
        if (server == null) {
            return;
        }
        try {
            server.close();
        } catch (Exception e) {
            // Log
            // TODO
        }
    }
}
