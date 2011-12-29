package egats;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Future;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class RequestListeningThread extends Thread {

    private Server server;
    private Integer port;
    private ServerSocket ss;
    private boolean run = true;

    static {
        Flags.setDefault(Flags.PORT, 55555);
    }

    public RequestListeningThread(Server server) {
        setName("Request Listening Thread");
        setPriority(Thread.MAX_PRIORITY);
        this.server = server;
        this.port = server.getPort();
    }

    public final void close() {
        // Stop the listener
        run = false;
        // Close the server listener so it returns
        IOUtil.safeClose(ss);
    }

    public final int getPort() {
        return port;
    }

    public final Server getServer() {
        return server;
    }

    @Override
    public final void run() {
        while (run) {
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
                while (run) {
                    // It's possible that a socket could be accepted but never processed. Need some utility that will clean up sockets
                    try {
                        // Waits until a request is made
                        Socket s = ss.accept();
                        // Check if the s was returned for some other reason
                        if (s == null) {
                            continue;
                        }
                        // Create a request processor to handle the client request.
                        RequestProcessor processor = new RequestProcessor(server, s);
                        // Submit the processor for execution
                        Future f = null;
                        try {
                            f = server.getExecutor().submit(processor);
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
