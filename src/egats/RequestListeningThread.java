package egats;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Augie
 */
public class RequestListeningThread extends Thread {

    public static final int DEFAULT_PORT = 55555;
    private final Server server;
    private final Integer port;

    public RequestListeningThread(Server server) {
        this(server, null);
    }

    public RequestListeningThread(Server server, Integer port) {
        this.server = server;
        this.port = port;
    }

    public final int getPort() {
        if (port == null) {
            return DEFAULT_PORT;
        }
        return port;
    }

    public final Server getServer() {
        return server;
    }

    @Override
    public final void run() {
        ServerSocket ss = null;
        while (true) {
            try {
                // Do we need to create a new socket?
                if (ss == null || ss.isClosed()) {
                    // Close the old socket if there is one.
                    if (ss != null) {
                        try {
                            ss.close();
                        } catch (Exception e) {
                            // Log
                            server.logException(e);
                        }
                    }
                    // Log the creation of a new ServerSocket
                    //
                    // Create the new socket.
                    ss = new ServerSocket(getPort());
                }
                // Listen for requests
                while (true) {
                    try {
                        // Waits until a request is made
                        Socket s = ss.accept();
                        // Create a request processor to handle the client request.
                        RequestProcessor processor = new RequestProcessor(this, s);
                        // TODO: This needs to change to being executed on another thread
                        processor.run();
                    } catch (Exception e) {
                        // Log
                        server.logException(e);
                    }
                }
            } catch (Exception e) {
                // Log exception
                server.logException(e);
                // Send an email to administration
            }
        }
    }
}
