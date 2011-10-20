package egats;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class RequestProcessor implements Runnable {

    private final Server server;
    private final Socket socket;
    private InputStream is = null;
    private InputStreamReader isr = null;
    private BufferedReader br = null;
    private OutputStream os = null;
    private DataOutputStream dos = null;

    public RequestProcessor(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public final void run() {
        try {
            // Open I/O streams
            open();

            // Read in the request
            StringBuffer requestBuffer = readInput();

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
            String method = queryTokenizer.nextToken().trim().toUpperCase();
            String object = queryTokenizer.nextToken().trim().toLowerCase();

            // Process the request according to the method
            if (method.equals("GET")) {
                processGetRequest(object, header, requestTokenizer);
            } else if (method.equals("POST")) {
                processPostRequest(object, header, requestTokenizer);
            } else {
                sendResponse(new Response(Response.STATUS_CODE_ERROR, "HTTP method not implemented: " + method));
            }
        } catch (Exception e) {
            // Log
            server.logException(e);
            // Notify the user
            try {
                sendResponse(new Response(Response.STATUS_CODE_ERROR, e.getMessage()));
            } catch (Exception ee) {
                // Log
                server.logException(ee);
            }
        } finally {
            safeClose();
        }
    }

    private void open() throws IOException {
        // Open the input streams
        is = socket.getInputStream();
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        // Open the output streams
        os = socket.getOutputStream();
        dos = new DataOutputStream(os);
    }

    private void safeClose() {
        IOUtil.safeClose(br);
        IOUtil.safeClose(isr);
        IOUtil.safeClose(is);
        IOUtil.safeClose(dos);
        IOUtil.safeClose(os);
        IOUtil.safeClose(socket);
    }

    private StringBuffer readInput() throws IOException {
        StringBuffer requestBuffer = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null && !line.equals("")) {
            requestBuffer.append(line + "\n");
        }
        return requestBuffer;
    }

    private final void sendResponse(Response response) throws Exception {
        sendResponse(response.getStatusCode(), response.getStatus(), "text/json", response.toString());
    }

    private final void sendResponse(String response) throws Exception {
        sendResponse(Response.STATUS_CODE_OK, Response.STATUS_OK, "text/plain", response);
    }

    private final void sendResponse(int code, String codeName, String contentType, String response) throws Exception {
        if (os == null || dos == null) {
            return;
        }
        dos.write(("HTTP/1.1 " + code + " " + codeName + "\r\n").getBytes());
        dos.write("Server: EGATS".getBytes());
        dos.write(("Content-Type: " + contentType + "\r\n").getBytes());
        dos.write(("Content-Length: " + response.length() + "\r\n").getBytes());
        dos.write("Connection: close\r\n".getBytes());
        dos.write("\r\n".getBytes());
        dos.write(response.getBytes());
    }

    private final void processGetRequest(String object, String header, StringTokenizer requestTokenizer) throws Exception {
        // Treated as a ping
        if (object.equals("/")) {
            sendResponse(new Response());
        } // Mirrors the request back to the requester. Human-oriented.
        else if (object.startsWith("/mirror")) {
            StringBuffer output = new StringBuffer();
            output.append(header + "\n");
            int count = requestTokenizer.countTokens();
            for (int i = 0; i < count; i++) {
                output.append(requestTokenizer.nextToken() + "\n");
            }
            sendResponse(output.toString());
        } // Responds with the statistics of the server. Human-oriented.
        else if (object.startsWith("/stats")) {
            throw new RuntimeException("TODO");
        } // Response with the current status of an EGAT process
        else if (object.startsWith("/p/")) {
            String id = object.substring(3);
            EGATProcess process = null;
            Response response = null;
            try {
                process = EGATProcess.CACHE.get(id);
                // Check if we found it
                if (process == null) {
                    response = new Response(Response.STATUS_CODE_NOT_FOUND,
                            "The EGAT process you requested was not found. The body of this message contains the ID requested.",
                            id);
                } else {
                    response = new Response(Response.STATUS_CODE_OK,
                            "The body of this message contains the requested EGAT Process object.",
                            process.getJSON());
                }
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it
                response = new Response(Response.STATUS_CODE_NOT_FOUND,
                        "The EGAT process you requested was not found. The body of this message contains the ID requested.",
                        id);
            }
            sendResponse(response);
        } // Response with an object of some type.
        else if (object.startsWith("/o/")) {
            String id = object.substring(3);
            EGATSObject o = null;
            Response response = null;
            try {
                o = EGATSObject.CACHE.get(id);
                if (o == null) {
                    response = new Response(Response.STATUS_CODE_NOT_FOUND,
                            "The object you requested was not found. The body of this message contains the ID requested.",
                            id);
                } else {
                    response = new Response(Response.STATUS_CODE_OK,
                            "The body of this message contains the requested object.",
                            o.getJSON());
                }
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it.
                response = new Response(Response.STATUS_CODE_NOT_FOUND,
                        "The object you requested was not found. The body of this message contains the ID requested.",
                        id);
            }
            sendResponse(response);
        } else {
            sendResponse(new Response(Response.STATUS_CODE_NOT_FOUND, "Object not recognized: " + object));
        }
    }

    private final void processPostRequest(String object, String header, StringTokenizer requestTokenizer) throws Exception {
        // Read in request headers
        Map<String, String> headers = new HashMap<String, String>();
        int headerCount = requestTokenizer.countTokens();
        for (int i = 0; i < headerCount; i++) {
            String subHeader = requestTokenizer.nextToken();
            headers.put(subHeader.substring(0, subHeader.indexOf(":")).trim().toLowerCase(), subHeader.substring(subHeader.indexOf(":") + 1).trim().toLowerCase());
        }

        // Read in the POST body
        int contentLength = Integer.valueOf(headers.get("content-length"));
        StringBuffer body = new StringBuffer();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) br.read());
        }

        // Treated as a ping
        if (object.equals("/")) {
            sendResponse(new Response());
        } // Mirrors the request back to the requester. Human-oriented.
        else if (object.startsWith("/mirror")) {
            StringBuffer output = new StringBuffer();
            output.append(header + "\n");
            for (String key : headers.keySet()) {
                output.append(key + ": " + headers.get(key) + "\n");
            }
            output.append("\n" + body);
            sendResponse(output.toString());
        } // Create a new process
        else if (object.startsWith("/p")) {
            EGATProcess process = null;
            Response response = null;
            try {
                // Submit the new process
                process = new EGATProcess(true);
                // Make a response
                response = new Response(Response.STATUS_CODE_OK,
                        "Your EGAT process has been created. The body of this message contains the ID of the new process.",
                        process.getID());
            } catch (Exception e) {
                response = new Response(Response.STATUS_CODE_ERROR,
                        "COuld not create EGAT process.");
            }
            // Respond with the new ID
            sendResponse(response);
        } // Create a new object
        else if (object.startsWith("/o")) {
            EGATSObject egatsObject = null;
            Response response = null;
            try {
                // Submit the new process
                egatsObject = new EGATSObject(true);
                // Make a response
                response = new Response(Response.STATUS_CODE_OK,
                        "Your EGATS object has been created. The body of this message contains the ID of the new object.",
                        egatsObject.getID());
            } catch (Exception e) {
                response = new Response(Response.STATUS_CODE_ERROR,
                        "Could not create object.");
            }
            // Respond with the new ID
            sendResponse(response);
        } else {
            sendResponse(new Response(Response.STATUS_CODE_NOT_FOUND, "Object not recognized: " + object));
        }
    }

    protected final void couldNotProcess(Response response) {
        try {
            // Open I/O streams
            open();
            // Don't care about the input, so throw it out
            readInput();
            // Notify the user that there was a problem
            sendResponse(response);
        } catch (Exception e) {
            // Log
            server.logException(e);
        } finally {
            safeClose();
        }
    }
}
