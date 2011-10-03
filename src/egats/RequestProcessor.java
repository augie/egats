package egats;

import java.io.BufferedReader;
import java.io.IOException;
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

            // Expecting HTTP requests
            String line, request = null, verb = null, noun = null, body = "";
            Map<String, String> headers = new HashMap<String, String>();
            boolean readBody = false;
            while ((line = br.readLine()) != null) {
                // This is the first line of the request.
                if (request == null) {
                    // Parse the request of the form "[verb] [object] [protocol]"
                    request = line;
                    String[] requestParts = request.split(" ");
                    verb = requestParts[0];
                    noun = requestParts[1];
                } // Read content
                else if (!line.equals("")) {
                    // This is the body of the request..
                    if (readBody) {
                        body += line;
                    } // These are the headers of the request.
                    else {
                        String name = "", value = "";
                        int indexOfDelim = line.indexOf(':');
                        if (indexOfDelim >= 0) {
                            name = line.substring(0, indexOfDelim).trim();
                            value = line.substring(indexOfDelim + 1).trim();
                        }
                        headers.put(name, value);
                    }
                } // POST has a blank line separating the headers from the body
                else if (!verb.equals("POST") || readBody) {
                    break;
                } else {
                    readBody = true;
                }
            }

            // Process the request
            os = socket.getOutputStream();
            if (verb.equals("GET")) {
                // Treated as a ping
                if (noun.equals("/")) {
                    os.write(new Response().getBytes());
                } // Mirrors the request back to the requester. Human-oriented.
                else if (noun.equals("/mirror")) {
                    os.write(String.valueOf(request + "\n").getBytes());
                    for (String name : headers.keySet()) {
                        os.write(String.valueOf(name + ": " + headers.get(name) + "\n").getBytes());
                    }
                } // Responds with the statistics of the server. Human-oriented.
                else if (noun.equals("/stats")) {
                    processStatsRequest(os);
                } // Response with the current status of an EGAT process
                else if (noun.startsWith("/p/")) {
                    String id = noun.substring(3);
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
                else if (noun.startsWith("/o/")) {
                    String id = noun.substring(3);
                    EGATSObject object = EGATSObjectCache.get(id);
                    Response response = null;
                    if (object == null) {
                        response = new Response(Response.STATUS_NOT_FOUND,
                                "The object you requested was not found. The body of this message contains the ID requested.",
                                id);
                    } else {
                        response = new Response(Response.STATUS_OK,
                                "The body of this message contains the requested object.",
                                object.getJSON());
                    }
                    os.write(response.getBytes());
                } else {
                    os.write(new Response(Response.STATUS_ERROR, "Object not recognized: " + noun).getBytes());
                }
            } else {
                os.write(new Response(Response.STATUS_ERROR, "HTTP verb not implemented: " + verb).getBytes());
            }
        } catch (Exception e) {
            // Log
            server.logException(e);
            // Try to write a response
            if (os != null) {
                try {
                    os.write(new Response(Response.STATUS_ERROR, e.getMessage()).getBytes());
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
