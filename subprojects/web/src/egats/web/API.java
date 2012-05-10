package egats.web;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import egats.EGATSProcess;
import egats.EGATSObject;
import egats.EGATSObjectFile;
import egats.EGATSWorkflow;
import egats.IOUtil;
import egats.Response;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Augie
 */
public class API {

//    public static final String HOST = "http://egat.eecs.umich.edu:55555";
    public static final String HOST = "http://localhost:55555";
    public static final String OBJECT_FOLDER = "/o/";
    public static final String PROCESS_FOLDER = "/p/";
    public static final String PROCESS_LIST_FOLDER = "/pt/";
    public static final String WORKFLOW_FOLDER = "/w/";
    public static final String WORKFLOW_LIST_FOLDER = "/wt/";
    public static final String TOOLKIT_FOLDER = "/t/";
    public static final String CREATE_OBJECT_URL = HOST + OBJECT_FOLDER;
    public static final String CREATE_PROCESS_URL = HOST + PROCESS_FOLDER;
    public static final String CREATE_WORKFLOW_URL = HOST + WORKFLOW_FOLDER;

    public static String getObjectURL(String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(OBJECT_FOLDER);
        sb.append(id);
        return sb.toString();
    }

    public static String getObjectJSON(String id) throws IOException {
        URL url = new URL(getObjectURL(id));
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new IOException(response.getMessage());
        }
        List<Object> objectList = (List<Object>) JSON.parse(response.getBody());
        return JSON.serialize(objectList.get(0));
    }

    public static EGATSObject getObject(String id) throws Exception {
        String json = API.getObjectJSON(id);
        return EGATSObject.read(json);
    }

    public static String createObject(String classPath, String object) throws Exception {
        // Create the object
        EGATSObject egatsObject = new EGATSObject();
        egatsObject.setClassPath(classPath);
        egatsObject.setObject(object);
        
        // Put in a list
        List<EGATSObject> list = new LinkedList<EGATSObject>();
        list.add(egatsObject);

        // Send the process request to the server
        Response response = Response.fromJSON(Util.send(CREATE_OBJECT_URL, JSON.serialize(list)));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending object to server: " + response);
        }

        // Return the ID
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        return IDs.get(0);
    }

    public static String createObjectFile(String name, String object) throws Exception {
        EGATSObjectFile egatsObjectFile = new EGATSObjectFile(name, object);
        return createObject(EGATSObjectFile.class.getName(), Util.GSON.toJson(egatsObjectFile));
    }

    public static String getProcessURL(String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(PROCESS_FOLDER);
        sb.append(id);
        return sb.toString();
    }

    public static String getProcessJSON(String id) throws IOException {
        URL url = new URL(getProcessURL(id));
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new IOException(response.getMessage());
        }
        List<Object> objectList = (List<Object>) JSON.parse(response.getBody());
        return JSON.serialize(objectList.get(0));
    }

    public static EGATSProcess getProcess(String id) throws Exception {
        String json = API.getProcessJSON(id);
        return EGATSProcess.read(json);
    }

    public static String createProcess(String name, String process, String[] args) throws Exception {
        // Create the process
        EGATSProcess egatProcess = new EGATSProcess();
        egatProcess.setName(name);
        egatProcess.setMethodPath(process);
        egatProcess.setArgs(args);
        
        // Put in a list
        List<EGATSProcess> list = new LinkedList<EGATSProcess>();
        list.add(egatProcess);

        // Send the process request to the server
        Response response = Response.fromJSON(Util.send(CREATE_PROCESS_URL, JSON.serialize(list)));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending process request to server: " + response);
        }

        // Return the ID
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        return IDs.get(0);
    }

    public static String getProcessListURL() {
        return getProcessListURL((long) 0);
    }

    public static String getProcessListURL(Long createTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(PROCESS_LIST_FOLDER);
        sb.append(createTime);
        return sb.toString();
    }

    public static String getProcessListJSON(Long createTime) throws IOException {
        URL url = new URL(getProcessListURL(createTime));
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new IOException(response.getMessage());
        }
        return response.getBody();
    }

    public static List<EGATSProcess> getProcesses(Long createTime) throws Exception {
        String json = API.getProcessListJSON(createTime);
        List<DBObject> dbObjectList = (List<DBObject>) JSON.parse(json);
        Collections.reverse(dbObjectList);
        List<EGATSProcess> list = new LinkedList<EGATSProcess>();
        for (DBObject o : dbObjectList) {
            list.add(EGATSProcess.read(o));
        }
        return list;
    }

    public static String getToolkitURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(TOOLKIT_FOLDER);
        return sb.toString();
    }

    public static List<String> getToolkit() throws Exception {
        URL url = new URL(getToolkitURL());
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        String json = response.getBody();
        return (List<String>) JSON.parse(json);
    }

    public static String getWorkflowURL(String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(WORKFLOW_FOLDER);
        sb.append(id);
        return sb.toString();
    }

    public static String getWorkflowJSON(String id) throws IOException {
        URL url = new URL(getWorkflowURL(id));
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new IOException(response.getMessage());
        }
        List<Object> objectList = (List<Object>) JSON.parse(response.getBody());
        return JSON.serialize(objectList.get(0));
    }

    public static EGATSWorkflow getWorkflow(String id) throws Exception {
        String json = API.getWorkflowJSON(id);
        return EGATSWorkflow.read(json);
    }

    public static String createWorkflow(String name, String classPath, String[] args) throws Exception {
        // Create the process
        EGATSWorkflow egatsWorkflow = new EGATSWorkflow();
        egatsWorkflow.setName(name);
        egatsWorkflow.setClassPath(classPath);
        egatsWorkflow.setArgs(args);

        // Put in a list
        List<EGATSWorkflow> list = new LinkedList<EGATSWorkflow>();
        list.add(egatsWorkflow);
        
        // Send the process request to the server
        Response response = Response.fromJSON(Util.send(CREATE_WORKFLOW_URL, JSON.serialize(list)));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending process request to server: " + response);
        }

        // Return the ID
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        return IDs.get(0);
    }

    public static String getWorkflowListURL() {
        return getWorkflowListURL((long) 0);
    }

    public static String getWorkflowListURL(Long createTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(WORKFLOW_LIST_FOLDER);
        sb.append(createTime);
        return sb.toString();
    }

    public static String getWorkflowListJSON(Long createTime) throws IOException {
        URL url = new URL(getWorkflowListURL(createTime));
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new IOException(response.getMessage());
        }
        return response.getBody();
    }

    public static List<EGATSWorkflow> getWorkflows(Long createTime) throws Exception {
        String json = API.getWorkflowListJSON(createTime);
        List<DBObject> dbObjectList = (List<DBObject>) JSON.parse(json);
        Collections.reverse(dbObjectList);
        List<EGATSWorkflow> list = new LinkedList<EGATSWorkflow>();
        for (DBObject o : dbObjectList) {
            list.add(EGATSWorkflow.read(o));
        }
        return list;
    }
}