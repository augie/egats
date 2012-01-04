package egats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Contains commonly used utility methods related to input and output.
 * @author Augie Hill - augman85@gmail.com
 */
public class IOUtil {
    
    /**
     * 
     * @param resource
     * @return
     * @throws IOException 
     */
    public static String readResource(String resource) throws IOException {
        return readInputStream(IOUtil.class.getResourceAsStream(resource));
    }

    /**
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFile(File file) throws IOException {
        return readInputStream(new FileInputStream(file));
    }

    /**
     * 
     * @param is
     * @return
     * @throws Exception
     */
    public static String readInputStream(InputStream is) throws IOException {
        StringBuilder resourceBuilder = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            boolean first = true;
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!first) {
                    resourceBuilder.append("\n");
                } else {
                    first = false;
                }
                resourceBuilder.append(line);
            }
        } finally {
            safeClose(br);
        }
        return resourceBuilder.toString();
    }

    /**
     *
     * @param is
     */
    public static void safeClose(InputStream is) {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
    }

    /**
     *
     * @param r
     */
    public static void safeClose(Reader r) {
        if (r == null) {
            return;
        }
        try {
            r.close();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
    }

    /**
     *
     * @param os
     */
    public static void safeClose(OutputStream os) {
        if (os == null) {
            return;
        }
        try {
            os.flush();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
        try {
            os.close();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
    }

    /**
     *
     * @param w
     */
    public static void safeClose(Writer w) {
        if (w == null) {
            return;
        }
        try {
            w.flush();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
        try {
            w.close();
        } catch (Exception e) {
            // Log exception
            // TODO
        }
    }

    /**
     *
     * @param s
     */
    public static void safeClose(Socket s) {
        if (s == null) {
            return;
        }
        try {
            s.close();
        } catch (Exception e) {
            // Log
            // TODO
        }
    }

    /**
     *
     * @param ss
     */
    public static void safeClose(ServerSocket ss) {
        if (ss == null) {
            return;
        }
        try {
            ss.close();
        } catch (Exception e) {
            // Log
            // TODO
        }
    }

    /**
     * 
     * @param s
     */
    public static void safeClose(Server s) {
        if (s == null) {
            return;
        }
        try {
            s.close();
        } catch (Exception e) {
            // Log
            // TODO
        }
    }
}
