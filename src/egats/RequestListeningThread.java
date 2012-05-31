package egats;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Future;

/**
 * Binds to a server socket and listens for requests.
 * Requests are submitted to the request process executor.
 * @author Augie Hill - augie@umich.edu
 */
public class RequestListeningThread extends Thread {

    public static final Integer DEFAULT_PORT = 55555;
    private Server server;
    private Integer port;
    private ServerSocket ss;
    private boolean run = true;

    static {
        Flags.setDefault(Flags.PORT, DEFAULT_PORT);
    }

    /**
     * 
     * @param server 
     */
    public RequestListeningThread(Server server) {
        setName("Request Listening Thread");
        setPriority(Thread.MAX_PRIORITY);
        this.server = server;
        this.port = server.getPort();
    }

    /**
     * 
     */
    public final void close() {
        // Stop the listener
        run = false;
        // Close the server listener so it returns
        IOUtils.closeQuietly(ss);
    }

    /**
     * 
     * @return 
     */
    public final int getPort() {
        return port;
    }

    /**
     * 
     * @return 
     */
    public final Server getServer() {
        return server;
    }

    /**
     * 
     */
    @Override
    public final void run() {
        while (run) {
            try {
                // Do we need to create a new socket?
                if (ss == null || ss.isClosed()) {
                    // Close the old socket if there is one.
                    IOUtils.closeQuietly(ss);
                    // Log the creation of a new ServerSocket
                    // TODO
                    // Create the new socket.
                    ss = new ServerSocket(getPort());
                }
                // Listen for requests
                while (run) {
                    // It's possible that a socket could be accepted but never processed. Need some utility that will clean up sockets
                    try {
                        // Waits until a request is made
                        Socket s = null;
                        // Throws a meaningless exception when the listener is closed
                        try {
                            s = ss.accept();
                        } catch (SocketException e) {
                            continue;
                        }
                        // Check if the s was returned for some other reason
                        if (s == null) {
                            continue;
                        }
                        // Create a request processor to handle the client request.
                        RequestProcess processor = new RequestProcess(server, s);
                        // Submit the processor for execution
                        Future f = null;
                        try {
                            f = server.getRequestExecutor().submit(processor);
                        } catch (Exception e) {
                            // Notify the user that an exception occurred.
                            processor.couldNotProcess(new Response(Response.STATUS_CODE_ERROR, e.getMessage()));
                        } finally {
                            // The process could not be submitted for execution, so notify the user.
                            if (f == null) {
                                processor.couldNotProcess(new Response(Response.STATUS_CODE_ERROR, "Your request could not be submitted."));
                            }
                        }
                    } catch (Exception e) {
                        // Log
                        server.logException(e);
                        // Try to notify the user that there was a problem
                        // TODO
                    }
                }
            } catch (BindException e) {
                // Log exception
                server.logException(e);
                // No more running
                run = false;
            } catch (Exception e) {
                // Log exception
                server.logException(e);
                // Send an email to administration
                // TODO
            }
        }
    }
}
