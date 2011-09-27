package egats;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class IOUtil {

    public static final void safeClose(InputStream is) {
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

    public static final void safeClose(Reader r) {
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

    public static final void safeClose(OutputStream os) {
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

    public static final void safeClose(Writer w) {
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

    public static final void safeClose(Socket s) {
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

    public static final void safeClose(ServerSocket ss) {
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
}
