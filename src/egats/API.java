package egats;

import com.mongodb.util.JSON;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class API {

    private static String HOST = "localhost:55555";
    public static final String PING = "/ping";
    public static final String MIRROR = "/mirror";
    public static final String REFRESH = "/refresh";
    public static final String OBJECTS_FOLDER = "/o/";
    public static final String PROCESSES_FOLDER = "/p/";
    public static final String PROCESSES_BY_TIMESTAMP_FOLDER = "/pt/";
    public static final String WORKFLOWS_FOLDER = "/w/";
    public static final String WORKFLOWS_BY_TIMESTAMP_FOLDER = "/wt/";
    public static final String TOOLKIT_FOLDER = "/t/";

    /**
     * The IP or domain name (and optionally the port number) of the EGATS server.
     */
    public static String getHost() {
        return HOST;
    }

    /**
     * Sets the IP or domain name (and optionally the port number) of the EGATS server.
     * @param host 
     */
    public static void setHost(String host) {
        HOST = host;
        if (HOST.startsWith("http://")) {
            HOST = HOST.substring(7);
        }
        if (HOST.startsWith("https://")) {
            HOST = HOST.substring(8);
        }
        if (!HOST.endsWith("/")) {
            HOST = HOST + "/";
        }
    }

    /**
     * HTTP GET request.
     * @param url
     * @return
     * @throws IOException 
     */
    public static Response send(String url) throws IOException {
        if (url == null) {
            throw new NullPointerException("URL is null.");
        }
        Response response = Response.fromJSON(IOUtils.toString(new URL(url).openStream()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new IOException(response.getMessage());
        }
        return response;
    }

    /**
     * HTTP POST request.
     * @param url
     * @param body
     * @return
     * @throws IOException 
     */
    public static Response send(String url, String body) throws IOException {
        // Checks
        if (url == null) {
            throw new NullPointerException("URL is null.");
        }
        if (body == null) {
            throw new NullPointerException("Body is null.");
        }
        // Send
        OutputStreamWriter wr = null;
        try {
            // Send data
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(body);
            wr.flush();
            // Get the response
            Response response = Response.fromJSON(IOUtils.toString(conn.getInputStream()));
            if (response.getStatusCode() != Response.STATUS_CODE_OK) {
                throw new IOException(response.getMessage());
            }
            return response;
        } finally {
            IOUtils.closeQuietly(wr);
        }
    }

    /**
     * 
     * @param folder
     * @return 
     */
    public static String getURL(String folder) {
        return getURL(folder, null);
    }

    /**
     * 
     * @param folder
     * @param param
     * @return 
     */
    public static String getURL(String folder, String param) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(HOST);
        if (folder != null) {
            if (folder.startsWith("/")) {
                folder = folder.substring(1);
            }
            if (!folder.endsWith("/")) {
                folder = folder + "/";
            }
            sb.append(folder);
        }
        if (param != null) {
            if (param.startsWith("/")) {
                param = param.substring(1);
            }
            if (param.endsWith("/")) {
                param = param.substring(0, param.length() - 1);
            }
            sb.append(param);
        }
        return sb.toString();
    }

    /**
     * 
     * @param id
     * @return
     * @throws IOException 
     */
    public static String getObjectJSON(String id) throws IOException {
        // Checks
        if (id == null) {
            throw new NullPointerException("ID is null.");
        }
        // Get
        List<String> ids = new LinkedList<String>();
        ids.add(id);
        return getObjectsJSON(ids).get(0);
    }

    /**
     * 
     * @param ids
     * @return
     * @throws IOException 
     */
    public static List<String> getObjectsJSON(List<String> ids) throws IOException {
        // Check
        if (ids == null) {
            throw new NullPointerException("ID list is null.");
        }
        // Get
        List<String> objectsJSON = new LinkedList<String>();
        if (!ids.isEmpty()) {
            Response response = send(getURL(OBJECTS_FOLDER, StringUtils.join(ids, ",")));
            for (Object o : (List<Object>) JSON.parse(response.getBody())) {
                objectsJSON.add(JSON.serialize(o));
            }
        }
        return objectsJSON;
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public static EGATSObject getObject(String id) throws Exception {
        // Check
        if (id == null) {
            throw new NullPointerException("ID is null.");
        }
        // Get
        return EGATSObject.read(API.getObjectJSON(id));
    }

    /**
     * 
     * @param ids
     * @return
     * @throws Exception 
     */
    public static List<EGATSObject> getObjects(List<String> ids) throws Exception {
        // Check
        if (ids == null) {
            throw new NullPointerException("ID list is null.");
        }
        // Get
        List<EGATSObject> objects = new LinkedList<EGATSObject>();
        for (String objectJSON : getObjectsJSON(ids)) {
            objects.add(EGATSObject.read(objectJSON));
        }
        return objects;
    }

    /**
     * 
     * @param object
     * @return
     * @throws Exception 
     */
    public static String createObject(EGATSObject object) throws Exception {
        // Check
        if (object == null) {
            throw new NullPointerException("Object is null.");
        }
        // Send
        List<EGATSObject> list = new LinkedList<EGATSObject>();
        list.add(object);
        return createObjects(list).get(0);
    }

    /**
     * 
     * @param objects
     * @return
     * @throws Exception 
     */
    public static List<String> createObjects(List<EGATSObject> objects) throws Exception {
        // Check
        if (objects == null) {
            throw new NullPointerException("Object list is null.");
        }
        // Send
        if (!objects.isEmpty()) {
            Response response = send(getURL(OBJECTS_FOLDER), JSON.serialize(objects));
            return (List<String>) JSON.parse(response.getBody());
        }
        return new LinkedList<String>();
    }

    /**
     * 
     * @param name
     * @param object
     * @return
     * @throws Exception 
     */
    public static String createObjectFile(String name, String object) throws Exception {
        // Wrap the file info (name is the file name, object is the file contents)
        EGATSObjectFile egatsObjectFile = new EGATSObjectFile(name, object);
        // Create
        EGATSObject egatsObject = new EGATSObject();
        egatsObject.setClassPath(EGATSObjectFile.class.getName());
        egatsObject.setObject(Data.GSON.toJson(egatsObjectFile));
        return createObject(egatsObject);
    }

    /**
     * 
     * @param id
     * @return
     * @throws IOException 
     */
    public static String getProcessJSON(String id) throws IOException {
        // Checks
        if (id == null) {
            throw new NullPointerException("ID is null.");
        }
        // Get
        List<String> ids = new LinkedList<String>();
        ids.add(id);
        return getProcessesJSON(ids).get(0);
    }

    /**
     * 
     * @param ids
     * @return
     * @throws IOException 
     */
    public static List<String> getProcessesJSON(List<String> ids) throws IOException {
        // Check
        if (ids == null) {
            throw new NullPointerException("ID list is null.");
        }
        // Get
        List<String> processesJSON = new LinkedList<String>();
        if (!ids.isEmpty()) {
            Response response = send(getURL(PROCESSES_FOLDER, StringUtils.join(ids, ",")));
            for (Object o : (List<Object>) JSON.parse(response.getBody())) {
                processesJSON.add(JSON.serialize(o));
            }
        }
        return processesJSON;
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public static EGATSProcess getProcess(String id) throws Exception {
        // Check
        if (id == null) {
            throw new NullPointerException("ID is null.");
        }
        // Get
        return EGATSProcess.read(API.getProcessJSON(id));
    }

    /**
     * 
     * @param ids
     * @return
     * @throws Exception 
     */
    public static List<EGATSProcess> getProcesses(List<String> ids) throws Exception {
        // Check
        if (ids == null) {
            throw new NullPointerException("ID list is null.");
        }
        // Get
        List<EGATSProcess> processes = new LinkedList<EGATSProcess>();
        for (String processJSON : getProcessesJSON(ids)) {
            processes.add(EGATSProcess.read(processJSON));
        }
        return processes;
    }

    /**
     * 
     * @param process
     * @return
     * @throws Exception 
     */
    public static String createProcess(EGATSProcess process) throws Exception {
        // Check
        if (process == null) {
            throw new NullPointerException("Process is null.");
        }
        // Send
        List<EGATSProcess> list = new LinkedList<EGATSProcess>();
        list.add(process);
        return createProcesses(list).get(0);
    }

    /**
     * 
     * @param processes
     * @return
     * @throws Exception 
     */
    public static List<String> createProcesses(List<EGATSProcess> processes) throws Exception {
        // Check
        if (processes == null) {
            throw new NullPointerException("Process list is null.");
        }
        // Send
        if (!processes.isEmpty()) {
            Response response = send(getURL(PROCESSES_FOLDER), JSON.serialize(processes));
            return (List<String>) JSON.parse(response.getBody());
        }
        return new LinkedList<String>();
    }

    /**
     * 
     * @param createTime
     * @return
     * @throws IOException 
     */
    public static List<String> getProcessesByTimestampJSON(Long createTime) throws IOException {
        // Check
        if (createTime == null || createTime < 0) {
            createTime = 0l;
        }
        // Get
        Response response = send(getURL(PROCESSES_BY_TIMESTAMP_FOLDER, String.valueOf(createTime.longValue())));
        List<String> processesJSON = new LinkedList<String>();
        for (Object o : (List<Object>) JSON.parse(response.getBody())) {
            processesJSON.add(JSON.serialize(o));
        }
        return processesJSON;
    }

    /**
     * 
     * @param createTime
     * @return
     * @throws Exception 
     */
    public static List<EGATSProcess> getProcessesByTimestamp(Long createTime) throws Exception {
        List<String> processesJSON = getProcessesByTimestampJSON(createTime);
        List<EGATSProcess> processes = new LinkedList<EGATSProcess>();
        for (String processJSON : processesJSON) {
            processes.add(EGATSProcess.read(processJSON));
        }
        return processes;
    }

    /**
     * 
     * @param id
     * @return
     * @throws IOException 
     */
    public static String getWorkflowJSON(String id) throws IOException {
        // Checks
        if (id == null) {
            throw new NullPointerException("ID is null.");
        }
        // Get
        List<String> ids = new LinkedList<String>();
        ids.add(id);
        return getWorkflowsJSON(ids).get(0);
    }

    /**
     * 
     * @param ids
     * @return
     * @throws IOException 
     */
    public static List<String> getWorkflowsJSON(List<String> ids) throws IOException {
        // Check
        if (ids == null) {
            throw new NullPointerException("ID list is null.");
        }
        // Get
        List<String> workflowsJSON = new LinkedList<String>();
        if (!ids.isEmpty()) {
            Response response = send(getURL(WORKFLOWS_FOLDER, StringUtils.join(ids, ",")));
            for (Object o : (List<Object>) JSON.parse(response.getBody())) {
                workflowsJSON.add(JSON.serialize(o));
            }
        }
        return workflowsJSON;
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public static EGATSWorkflow getWorkflow(String id) throws Exception {
        // Check
        if (id == null) {
            throw new NullPointerException("ID is null.");
        }
        // Get
        return EGATSWorkflow.read(API.getWorkflowJSON(id));
    }

    /**
     * 
     * @param ids
     * @return
     * @throws Exception 
     */
    public static List<EGATSWorkflow> getWorkflows(List<String> ids) throws Exception {
        // Check
        if (ids == null) {
            throw new NullPointerException("ID list is null.");
        }
        // Get
        List<EGATSWorkflow> workflows = new LinkedList<EGATSWorkflow>();
        for (String workflowJSON : getWorkflowsJSON(ids)) {
            workflows.add(EGATSWorkflow.read(workflowJSON));
        }
        return workflows;
    }

    /**
     * 
     * @param workflow
     * @return
     * @throws Exception 
     */
    public static String createWorkflow(EGATSWorkflow workflow) throws Exception {
        // Check
        if (workflow == null) {
            throw new NullPointerException("Workflow is null.");
        }
        // Send
        List<EGATSWorkflow> list = new LinkedList<EGATSWorkflow>();
        list.add(workflow);
        return createWorkflows(list).get(0);
    }

    /**
     * 
     * @param workflows
     * @return
     * @throws Exception 
     */
    public static List<String> createWorkflows(List<EGATSWorkflow> workflows) throws Exception {
        // Check
        if (workflows == null) {
            throw new NullPointerException("Process list is null.");
        }
        // Send
        if (!workflows.isEmpty()) {
            Response response = send(getURL(WORKFLOWS_FOLDER), JSON.serialize(workflows));
            return (List<String>) JSON.parse(response.getBody());
        }
        return new LinkedList<String>();
    }

    /**
     * 
     * @param createTime
     * @return
     * @throws IOException 
     */
    public static List<String> getWorkflowsByTimestampJSON(Long createTime) throws IOException {
        // Check
        if (createTime == null || createTime < 0) {
            createTime = 0l;
        }
        // Get
        Response response = send(getURL(WORKFLOWS_BY_TIMESTAMP_FOLDER, String.valueOf(createTime.longValue())));
        List<String> workflowsJSON = new LinkedList<String>();
        for (Object o : (List<Object>) JSON.parse(response.getBody())) {
            workflowsJSON.add(JSON.serialize(o));
        }
        return workflowsJSON;
    }

    /**
     * 
     * @param createTime
     * @return
     * @throws Exception 
     */
    public static List<EGATSWorkflow> getWorkflowsByTimestamp(Long createTime) throws Exception {
        List<String> workflowsJSON = getWorkflowsByTimestampJSON(createTime);
        List<EGATSWorkflow> workflows = new LinkedList<EGATSWorkflow>();
        for (String workflowJSON : workflowsJSON) {
            workflows.add(EGATSWorkflow.read(workflowJSON));
        }
        return workflows;
    }

    /**
     * 
     * @return
     * @throws Exception 
     */
    public static List<String> getToolkit() throws Exception {
        Response response = send(getURL(TOOLKIT_FOLDER));
        return (List<String>) JSON.parse(response.getBody());
    }
}
