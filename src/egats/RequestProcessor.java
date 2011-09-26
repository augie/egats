package egats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class RequestProcessor implements Runnable {

    private final Server server;
    private final Socket socket;

    public RequestProcessor(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public final void run() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        try {
            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            // Expecting HTTP-formatted requests
            String line, request = null;
            Map<String, String> headers = new HashMap<String, String>();
            while (!(line = br.readLine()).equals("")) {
                // This is the first line of the request.
                if (request == null) {
                    request = line;
                } // This is the headers of the request.
                else {
                    String name = "", value = "";
                    int indexOfDelim = line.indexOf(':');
                    if (indexOfDelim >= 0) {
                        name = line.substring(0, indexOfDelim).trim();
                        value = line.substring(indexOfDelim + 1).trim();
                    }
                    headers.put(name, value);
                }
            }

            // Mirror the request
            os = socket.getOutputStream();
            if (request != null) {
                os.write(String.valueOf("REQUEST: " + request + "\nHEADERS:\n").getBytes());
                for (String name : headers.keySet()) {
                    os.write(String.valueOf(" " + name + " : " + headers.get(name) + "\n").getBytes());
                }
                os.write("\n".getBytes());
            }
        } catch (Exception e) {
            // Log
            server.logException(e);
            // Try to write a response
            if (os != null) {
                try {
                    os.write("There was an error processing your request.".getBytes());
                } catch (Exception ee) {
                    // Log
                    server.logException(e);
                    // TODO
                }
            }
        } finally {
            IOUtil.safeClose(br);
            IOUtil.safeClose(isr);
            IOUtil.safeClose(is);
            IOUtil.safeClose(os);
            IOUtil.safeClose(socket);
        }
    }

    public void couldNotProcess() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        try {
            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            // Throw away all input
            String request;
            while (!(request = br.readLine()).equals("")) {
                // No-op
            }

            // Notify the user that there was a problem
            os = socket.getOutputStream();
            os.write("[Fail Whale] Your request could not be processed.".getBytes());
        } catch (Exception e) {
            // Log
            server.logException(e);
        } finally {
            IOUtil.safeClose(os);
            IOUtil.safeClose(br);
            IOUtil.safeClose(isr);
            IOUtil.safeClose(is);
            IOUtil.safeClose(socket);
        }
    }
}
