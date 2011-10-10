package egats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

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
            // Open the input streams
            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            // Open the output stream
            os = socket.getOutputStream();

            // Read in the request
            StringBuffer requestBuffer = new StringBuffer();
            while (br.ready()) {
                requestBuffer.append(br.readLine() + "\n");
            }

            // Parse the request
            String request = requestBuffer.toString();
            StringTokenizer requestTokenizer = new StringTokenizer(request, "\n");

            // Sometimes receive a blank request?
            if (requestTokenizer.countTokens() == 0) {
                return;
            }
            String header = requestTokenizer.nextToken();

            // Parse the header
            StringTokenizer queryTokenizer = new StringTokenizer(header);
            String method = queryTokenizer.nextToken();
            String object = queryTokenizer.nextToken();

            // Process the request according to the method
            if (method.equals("GET")) {
                processGetRequest(os, object, header, requestTokenizer);
            } else if (method.equals("POST")) {
                processPostRequest(os, object, requestTokenizer);
            } else {
                os.write(new Response(Response.STATUS_ERROR, "HTTP method not implemented: " + method).getBytes());
            }
        } catch (Exception e) {
            // Log
            server.logException(e);
            // Notify the user
            if (os != null) {
                try {
                    os.write(new Response(Response.STATUS_ERROR, e.getMessage()).getBytes());
                } catch (Exception ee) {
                    // Log
                    server.logException(ee);
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

    private void processGetRequest(OutputStream os, String object, String header, StringTokenizer requestTokenizer) throws Exception {
        // Treated as a ping
        if (object.equals("/")) {
            os.write(new Response().getBytes());
        } // Mirrors the request back to the requester. Human-oriented.
        else if (object.equals("/mirror")) {
            StringBuffer output = new StringBuffer();
            output.append(header + "\n");
            int count = requestTokenizer.countTokens();
            for (int i = 0; i < count; i++) {
                output.append(requestTokenizer.nextToken() + "\n");
            }
            os.write(output.toString().getBytes());
        } // Responds with the statistics of the server. Human-oriented.
        else if (object.equals("/stats")) {
            processStatsRequest(os);
        } // Response with the current status of an EGAT process
        else if (object.startsWith("/p/")) {
            String id = object.substring(3);
            EGATProcess process = EGATProcessCache.get(id);
            Response response = null;
            if (process == null) {
                response = new Response(Response.STATUS_NOT_FOUND,
                        "The EGAT process you requested was not found. The body of this message contains the ID requested.",
                        id);
            } else {
                response = new Response(Response.STATUS_OK,
                        "The body of this message contains the requested EGAT Process object.",
                        process.getJSON());
            }
            os.write(response.getBytes());
        } // Response with an object of some type.
        else if (object.startsWith("/o/")) {
            String id = object.substring(3);
            EGATSObject o = EGATSObjectCache.get(id);
            Response response = null;
            if (o == null) {
                response = new Response(Response.STATUS_NOT_FOUND,
                        "The object you requested was not found. The body of this message contains the ID requested.",
                        id);
            } else {
                response = new Response(Response.STATUS_OK,
                        "The body of this message contains the requested object.",
                        o.getJSON());
            }
            os.write(response.getBytes());
        } else {
            os.write(new Response(Response.STATUS_ERROR, "Object not recognized: " + object).getBytes());
        }
    }

    private void processPostRequest(OutputStream os, String object, StringTokenizer requestTokenizer) {
        throw new RuntimeException("POST handling not yet implemented.");
    }

    public void processStatsRequest(OutputStream os) throws IOException {
        // TODO
        String stats =
                "FLAGS: \n"
                + "TIME: \n"
                + "PROCESSES: \n"
                + "ERRORS: \n";
        os.write(stats.getBytes());
    }

    public void couldNotProcess(Response response) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        try {
            // Don't care about the input
            try {
                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

                // Throw away all input
                String request;
                while (!(request = br.readLine()).equals("")) {
                    // No-op
                }
            } catch (Exception e) {
                // Log
                // TODO
            }

            // Notify the user that there was a problem
            os = socket.getOutputStream();
            os.write(Data.GSON.toJson(response).getBytes());
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
