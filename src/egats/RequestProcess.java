package egats;

import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class RequestProcess implements Runnable {

    private final Server server;
    private final Socket socket;
    private BufferedReader br = null;
    private DataOutputStream dos = null;

    /**
     * 
     * @param server
     * @param socket 
     */
    public RequestProcess(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    /**
     * 
     */
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
                processGet(object, header, requestTokenizer);
            } else if (method.equals("POST")) {
                processPost(object, header, requestTokenizer);
            } else {
                send(new Response(Response.STATUS_CODE_ERROR, "HTTP method not implemented: " + method));
            }
        } catch (Exception e) {
            // Log
            server.logException(e);
            // Notify the user
            try {
                send(new Response(Response.STATUS_CODE_ERROR, e.getMessage()));
            } catch (Exception ee) {
                // Log
                server.logException(ee);
            }
        } finally {
            safeClose();
        }
    }

    /**
     * 
     * @throws IOException 
     */
    private void open() throws IOException {
        // Open the input stream
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Open the output stream
        dos = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * 
     */
    private void safeClose() {
        IOUtils.closeQuietly(br);
        IOUtils.closeQuietly(dos);
        IOUtils.closeQuietly(socket);
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    private StringBuilder readInput() throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.equals("")) {
            requestBuilder.append(line);
            requestBuilder.append("\n");
        }
        return requestBuilder;
    }

    /**
     * 
     * @param response
     * @throws Exception 
     */
    private void send(Response response) throws Exception {
        // Always send as 200. The JSON Response representation contains the status code information.
        send(200, Response.STATUS_OK, "text/json", response.toString());
    }

    /**
     * 
     * @param response
     * @throws Exception 
     */
    private void send(String response) throws Exception {
        send(200, Response.STATUS_OK, "text/plain", response);
    }

    /**
     * 
     * @param code
     * @param codeName
     * @param contentType
     * @param response
     * @throws Exception 
     */
    private void send(int code, String codeName, String contentType, String response) throws Exception {
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

    /**
     * 
     * @param object
     * @param header
     * @param requestTokenizer
     * @throws Exception 
     */
    private void processGet(String object, String header, StringTokenizer requestTokenizer) throws Exception {
        // Ping
        if (object.startsWith(API.PING)) {
            send("");
        } // Mirrors the request back to the requester. Human-oriented.
        else if (object.startsWith(API.MIRROR)) {
            StringBuilder output = new StringBuilder();
            output.append(header);
            output.append("\n");
            int count = requestTokenizer.countTokens();
            for (int i = 0; i < count; i++) {
                output.append(requestTokenizer.nextToken());
                output.append("\n");
            }
            send(output.toString());
        } // Reloads the toolkit
        else if (object.startsWith(API.REFRESH) || object.startsWith("/reloadlibs") || object.startsWith("/reloadtoolkit")) {
            Response response = null;
            try {
                server.getToolkit().load();
                response = new Response(Response.STATUS_CODE_OK, "The libraries were reloaded.");
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it
                response = new Response(Response.STATUS_CODE_ERROR,
                        "There was a problem refreshing the toolkit.",
                        e.toString());
            }
            send(response);
        } // Processes
        else if (object.startsWith(API.PROCESSES_FOLDER)) {
            String ids = object.substring(3);
            Response response = null;
            try {
                String[] idSplit = ids.split(",");
                List<EGATSProcess> processList = new LinkedList<EGATSProcess>();
                for (String id : idSplit) {
                    EGATSProcess o = EGATSProcess.CACHE.get(id);
                    if (o == null) {
                        processList = null;
                        response = new Response(Response.STATUS_CODE_NOT_FOUND,
                                "A process you requested was not found. The body of this message contains the ID of the missing process.",
                                id);
                        break;
                    }
                    processList.add(o);
                }
                // Found all requested processes
                if (processList != null) {
                    response = new Response(Response.STATUS_CODE_OK,
                            "The body of this message contains the requested processes.",
                            JSON.serialize(processList));
                }
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it.
                response = new Response(Response.STATUS_CODE_ERROR,
                        "There was a problem getting the requested processes.",
                        e.toString());
            }
            send(response);
        } // Processes by timestamp
        else if (object.startsWith(API.PROCESSES_BY_TIMESTAMP_FOLDER)) {
            Long createTime = null;
            Response response = null;
            try {
                createTime = Long.valueOf(object.substring(4));
                List<EGATSProcess> processes = EGATSProcess.CACHE.get(createTime);
                response = new Response(Response.STATUS_CODE_OK,
                        "The body of this message contains a list of processes.",
                        JSON.serialize(processes));
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it
                response = new Response(Response.STATUS_CODE_ERROR,
                        "There was a problem getting a list of processes.",
                        e.toString());
            }
            send(response);
        } // Objects
        else if (object.startsWith(API.OBJECTS_FOLDER)) {
            String ids = object.substring(3);
            Response response = null;
            try {
                String[] idSplit = ids.split(",");
                List<EGATSObject> objectList = new LinkedList<EGATSObject>();
                for (String id : idSplit) {
                    EGATSObject o = EGATSObject.CACHE.get(id);
                    if (o == null) {
                        objectList = null;
                        response = new Response(Response.STATUS_CODE_NOT_FOUND,
                                "An object you requested was not found. The body of this message contains the ID of the missing object.",
                                id);
                        break;
                    }
                    objectList.add(o);
                }
                // Found all requested objects
                if (objectList != null) {
                    response = new Response(Response.STATUS_CODE_OK,
                            "The body of this message contains the requested objects.",
                            JSON.serialize(objectList));
                }
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it.
                response = new Response(Response.STATUS_CODE_ERROR,
                        "There was a problem getting the requested objects.",
                        e.toString());
            }
            send(response);
        } // Workflows
        else if (object.startsWith(API.WORKFLOWS_FOLDER)) {
            String ids = object.substring(3);
            Response response = null;
            try {
                String[] idSplit = ids.split(",");
                List<EGATSWorkflow> workflowList = new LinkedList<EGATSWorkflow>();
                for (String id : idSplit) {
                    EGATSWorkflow o = EGATSWorkflow.CACHE.get(id);
                    if (o == null) {
                        workflowList = null;
                        response = new Response(Response.STATUS_CODE_NOT_FOUND,
                                "A workflow you requested was not found. The body of this message contains the id of the missing workflow.",
                                id);
                        break;
                    }
                    workflowList.add(o);
                }
                // Found all requested workflows
                if (workflowList != null) {
                    response = new Response(Response.STATUS_CODE_OK,
                            "The body of this message contains the requested workflows.",
                            JSON.serialize(workflowList));
                }
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it
                response = new Response(Response.STATUS_CODE_ERROR,
                        "There was a problem getting the requested workflows.",
                        e.toString());
            }
            send(response);
        } // Workflows by timestamp
        else if (object.startsWith(API.WORKFLOWS_BY_TIMESTAMP_FOLDER)) {
            Long createTime = null;
            Response response = null;
            try {
                createTime = Long.valueOf(object.substring(4));
                List<EGATSWorkflow> workflows = EGATSWorkflow.CACHE.get(createTime);
                response = new Response(Response.STATUS_CODE_OK,
                        "The body of this message contains a list of workflows.",
                        JSON.serialize(workflows));
            } catch (Exception e) {
                // Log
                // TODO
                // Tell them we couldn't find it
                response = new Response(Response.STATUS_CODE_ERROR,
                        "There was a problem getting the requested workflows.",
                        e.toString());
            }
            send(response);
        } // Response with the list of scripts in the toolkit.
        else if (object.startsWith(API.TOOLKIT_FOLDER)) {
            Response response = new Response(Response.STATUS_CODE_OK,
                    "The body of this message contains the list of approved tools.",
                    JSON.serialize(server.getToolkit().getTools()));
            send(response);
        } else {
            send(new Response(Response.STATUS_CODE_NOT_FOUND, "Object not recognized: " + object));
        }
    }

    /**
     * 
     * @param object
     * @param header
     * @param requestTokenizer
     * @throws Exception 
     */
    private void processPost(String object, String header, StringTokenizer requestTokenizer) throws Exception {
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

        // Ping
        if (object.startsWith(API.PING)) {
            send("");
        } // Mirrors the request back to the requester. Human-oriented.
        else if (object.startsWith(API.MIRROR)) {
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
            send(output.toString());
        } // Processes
        else if (object.startsWith(API.PROCESSES_FOLDER)) {
            Response response = null;
            try {
                // The body is an EGATSProcess list
                List<Object> list = (List<Object>) JSON.parse(body.toString());
                // Create new processes
                List<String> IDs = new LinkedList<String>();
                for (Object o : list) {
                    // Create the process (serializing back to JSON is a lazy hack)
                    EGATSProcess egatsProcess = EGATSProcess.create(JSON.serialize(o));
                    egatsProcess.setServer(server);
                    // Submit the new process for execution
                    if (server.getProcessExecutor().submit(egatsProcess) == null) {
                        // TODO: need to execute the process somehow. Occasional checks for processes to queue from the DB?
                    }
                    // Remember the ID
                    IDs.add(egatsProcess.getID());
                }
                // Make a response
                response = new Response(Response.STATUS_CODE_OK,
                        "Your processes have been created. The body of this message contains the IDs of the new processes.",
                        JSON.serialize(IDs));
            } catch (Exception e) {
                // Log
                server.logException(e);
                response = new Response(Response.STATUS_CODE_ERROR,
                        "Could not create process.",
                        e.getMessage());
            }
            // Respond with the new ID
            send(response);
        } // Objects
        else if (object.startsWith(API.OBJECTS_FOLDER)) {
            Response response = null;
            try {
                // The body is an EGATSObject list
                List<Object> list = (List<Object>) JSON.parse(body.toString());
                // Create new objects
                List<String> IDs = new LinkedList<String>();
                for (Object o : list) {
                    // Create the object (serializing back to JSON is a lazy hack)
                    EGATSObject egatsObject = EGATSObject.create(JSON.serialize(o));
                    // Remember the ID
                    IDs.add(egatsObject.getID());
                }
                // Make a response
                response = new Response(Response.STATUS_CODE_OK,
                        "Your objects have been created. The body of this message contains the IDs of the new objects.",
                        JSON.serialize(IDs));
            } catch (Exception e) {
                // Log
                server.logException(e);
                response = new Response(Response.STATUS_CODE_ERROR,
                        "Could not create objects.",
                        e.getMessage());
            }
            // Respond with the new ID
            send(response);
        } // Create new workflows
        else if (object.startsWith(API.WORKFLOWS_FOLDER)) {
            Response response = null;
            try {
                // The body is an EGATSObject list
                List<Object> list = (List<Object>) JSON.parse(body.toString());
                // Create new objects
                List<String> IDs = new LinkedList<String>();
                for (Object o : list) {
                    // Create the object (serializing back to JSON is a lazy hack)
                    EGATSWorkflow egatsWorkflow = EGATSWorkflow.create(JSON.serialize(o));
                    egatsWorkflow.setServer(server);
                    // Submit the new process for execution
                    if (server.getWorkflowExecutor().submit(egatsWorkflow) == null) {
                        // TODO: need to execute the process somehow. Occasional checks for processes to queue from the DB?
                    }
                    // Remember the ID
                    IDs.add(egatsWorkflow.getID());
                }
                // Make a response
                response = new Response(Response.STATUS_CODE_OK,
                        "Your workflows have been created. The body of this message contains the IDs of the new workflows.",
                        JSON.serialize(IDs));
            } catch (Exception e) {
                // Log
                server.logException(e);
                response = new Response(Response.STATUS_CODE_ERROR,
                        "Could not create workflow.", e.getMessage());
            }
            // Respond with the new ID
            send(response);
        } else {
            send(new Response(Response.STATUS_CODE_NOT_FOUND, "Object not recognized: " + object));
        }
    }

    /**
     * 
     * @param response 
     */
    protected final void couldNotProcess(Response response) {
        try {
            // Open I/O streams
            open();
            // Don't care about the input, so throw it out
            readInput();
            // Notify the user that there was a problem
            send(response);
        } catch (Exception e) {
            // Log
            server.logException(e);
        } finally {
            safeClose();
        }
    }
}
