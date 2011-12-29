package egats;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private BufferedReader br = null;
    private DataOutputStream dos = null;

    public RequestProcessor(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public final void run() {
        try {
            // Open I/O streams
            open();

            // Read in the request
            StringBuilder requestBuffer = readInput();

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
        // Open the input stream
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Open the output stream
        dos = new DataOutputStream(socket.getOutputStream());
    }

    private void safeClose() {
        IOUtil.safeClose(br);
        IOUtil.safeClose(dos);
        IOUtil.safeClose(socket);
    }

    private StringBuilder readInput() throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.equals("")) {
            requestBuilder.append(line);
            requestBuilder.append("\n");
        }
        return requestBuilder;
    }

    private void sendResponse(Response response) throws Exception {
        sendResponse(response.getStatusCode(), response.getStatus(), "text/json", response.toString());
    }

    private void sendResponse(String response) throws Exception {
        sendResponse(Response.STATUS_CODE_OK, Response.STATUS_OK, "text/plain", response);
    }

    private void sendResponse(String response, String type) throws Exception {
        sendResponse(Response.STATUS_CODE_OK, Response.STATUS_OK, type, response);
    }

    private void sendResponse(int code, String codeName, String contentType, String response) throws Exception {
        if (dos == null) {
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

    private void processGetRequest(String object, String header, StringTokenizer requestTokenizer) throws Exception {
        // Mirrors the request back to the requester. Human-oriented.
        if (object.startsWith("/mirror")) {
            StringBuilder output = new StringBuilder();
            output.append(header);
            output.append("\n");
            int count = requestTokenizer.countTokens();
            for (int i = 0; i < count; i++) {
                output.append(requestTokenizer.nextToken());
                output.append("\n");
            }
            sendResponse(output.toString());
        } // Responds with the statistics of the server. Human-oriented.
        else if (object.startsWith("/reloadlibs")) {
            Response response = null;
            try {
                server.getToolkit().reload();
                response = new Response(Response.STATUS_CODE_OK, "The libraries were reloaded.");
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it
                response = new Response(Response.STATUS_CODE_ERROR,
                        "There was a problem reloading the libraries.",
                        e.getMessage());
            }
            sendResponse(response);
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

    private void processPostRequest(String object, String header, StringTokenizer requestTokenizer) throws Exception {
        // Read in request headers
        Map<String, String> headers = new HashMap<String, String>();
        int headerCount = requestTokenizer.countTokens();
        for (int i = 0; i < headerCount; i++) {
            String subHeader = requestTokenizer.nextToken();
            headers.put(subHeader.substring(0, subHeader.indexOf(":")).trim().toLowerCase(), subHeader.substring(subHeader.indexOf(":") + 1).trim().toLowerCase());
        }

        // Read in the POST body
        int contentLength = Integer.valueOf(headers.get("content-length"));
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) br.read());
        }

        // Mirrors the request back to the requester. Human-oriented.
        if (object.startsWith("/mirror")) {
            StringBuilder output = new StringBuilder();
            output.append(header);
            output.append("\n");
            for (String key : headers.keySet()) {
                output.append(key);
                output.append(": ");
                output.append(headers.get(key));
                output.append("\n");
            }
            output.append("\n");
            output.append(body);
            sendResponse(output.toString());
        } // Create a new process
        else if (object.startsWith("/p")) {
            EGATProcess process = null;
            Response response = null;
            try {
                // Create the new process
                process = EGATProcess.create(body.toString());
                process.setServer(server);
                // Submit the new process for execution
                if (server.getEGATExecutor().submit(process) == null) {
                    // TODO: need to execute the process somehow. Occasional checks for processes to queue from the DB?
                }
                // Make a response
                response = new Response(Response.STATUS_CODE_OK,
                        "Your EGAT process has been created. The body of this message contains the ID of the new process.",
                        process.getID());
            } catch (Exception e) {
                e.printStackTrace();
                // Log
                server.logException(e);
                response = new Response(Response.STATUS_CODE_ERROR,
                        "Could not create EGAT process.", e.getMessage());
            }
            // Respond with the new ID
            sendResponse(response);
        } // Create a new object
        else if (object.startsWith("/o")) {
            EGATSObject egatsObject = null;
            Response response = null;
            try {
                // Create a new object from the JSON
                egatsObject = EGATSObject.create(body.toString());
                // Make a response
                response = new Response(Response.STATUS_CODE_OK,
                        "Your EGATS object has been created. The body of this message contains the ID of the new object.",
                        egatsObject.getID());
            } catch (Exception e) {
                // Log
                server.logException(e);
                response = new Response(Response.STATUS_CODE_ERROR,
                        "Could not create object.", e.getMessage());
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
