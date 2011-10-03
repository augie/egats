package egats;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Future;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class RequestListeningThread extends Thread {

    public static final int DEFAULT_PORT = 55555;
    private final Server server;
    private final Integer port;

    static {
        Flags.setDefault(Flags.PORT, DEFAULT_PORT);
    }

    public RequestListeningThread(Server server) {
        setName("Request Listening Thread");
        setPriority(Thread.MAX_PRIORITY);
        this.server = server;
        this.port = server.getFlags().getInt(Flags.PORT);
    }

    public final int getPort() {
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
                    IOUtil.safeClose(ss);
                    // Log the creation of a new ServerSocket
                    // TODO
                    // Create the new socket.
                    ss = new ServerSocket(getPort());
                }
                // Listen for requests
                while (true) {
                    // It's possible that a socket could be accepted but never processed. Need some utility that will clean up sockets
                    try {
                        // Waits until a request is made
                        Socket s = ss.accept();
                        // Create a request processor to handle the client request.
                        RequestProcessor processor = new RequestProcessor(server, s);
                        // Submit the processor for execution
                        Future f = null;
                        try {
                            f = server.getExecutor().submit(processor);
                        } catch (Exception e) {
                            // Notify the user that an exception occurred.
                            processor.couldNotProcess(new Response(Response.STATUS_ERROR, e.getMessage()));
                        } finally {
                            // The process could not be submitted for execution, so notify the user.
                            if (f == null) {
                                processor.couldNotProcess(new Response(Response.STATUS_ERROR, "Your request could not be submitted."));
                            }
                        }
                    } catch (Exception e) {
                        // Log
                        server.logException(e);
                        // Try to notify the user that there was a problem
                        // TODO
                    }
                }
            } catch (Exception e) {
                // Log exception
                server.logException(e);
                // Send an email to administration
                // TODO
            }
        }
    }
}
