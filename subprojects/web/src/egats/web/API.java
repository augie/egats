package egats.web;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import egats.EGATProcess;
import egats.EGATSObject;
import egats.EGATSObjectFile;
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

    public static final String HOST = "http://egat.eecs.umich.edu:55555";
    public static final String OBJECT_SUBFOLDER = "/o/";
    public static final String PROCESS_SUBFOLDER = "/p/";
    public static final String PROCESS_LIST_SUBFOLDER = "/pl/";
    public static final String TOOLKIT_SUBFOLDER = "/t/";
    public static final String CREATE_OBJECT_URL = HOST + OBJECT_SUBFOLDER;
    public static final String CREATE_PROCESS_URL = HOST + PROCESS_SUBFOLDER;

    public static String getObjectURL(String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(OBJECT_SUBFOLDER);
        sb.append(id);
        return sb.toString();
    }

    public static String getObjectJSON(String id) throws IOException {
        URL url = new URL(getObjectURL(id));
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        return response.getBody();
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
        
        // Send the process request to the server
        Response response = Response.fromJSON(Util.sendPostRequest(CREATE_OBJECT_URL, egatsObject.getJSON()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending object to server: " + response);
        }

        // Return the ID
        String objectID = response.getBody();
        return objectID;
    }

    public static String createObjectFile(String name, String object) throws Exception {
        EGATSObjectFile egatsObjectFile = new EGATSObjectFile(name, object);
        return createObject(EGATSObjectFile.class.getName(), Util.GSON.toJson(egatsObjectFile));
    }

    public static String getProcessURL(String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(PROCESS_SUBFOLDER);
        sb.append(id);
        return sb.toString();
    }

    public static String getProcessJSON(String id) throws IOException {
        URL url = new URL(getProcessURL(id));
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        return response.getBody();
    }

    public static EGATProcess getProcess(String id) throws Exception {
        String json = API.getProcessJSON(id);
        return EGATProcess.read(json);
    }

    public static String createProcess(String name, String process, String[] args) throws Exception {
        // Create the process
        EGATProcess egatProcess = new EGATProcess();
        egatProcess.setName(name);
        egatProcess.setMethodPath(process);
        egatProcess.setArgs(args);

        // Send the process request to the server
        Response response = Response.fromJSON(Util.sendPostRequest(CREATE_PROCESS_URL, egatProcess.getJSON()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending process request to server: " + response);
        }

        // Return the process ID
        String processID = response.getBody();
        return processID;
    }

    public static String getProcessListURL(Long createTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(PROCESS_LIST_SUBFOLDER);
        sb.append(createTime);
        return sb.toString();
    }

    public static String getProcessListJSON(Long createTime) throws IOException {
        URL url = new URL(getProcessListURL(createTime));
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        return response.getBody();
    }

    public static List<EGATProcess> getProcesses(Long createTime) throws Exception {
        String json = API.getProcessListJSON(createTime);
        List<DBObject> dbObjectList = (List<DBObject>) JSON.parse(json);
        Collections.reverse(dbObjectList);
        List<EGATProcess> list = new LinkedList<EGATProcess>();
        for (DBObject o : dbObjectList) {
            list.add(EGATProcess.read(o));
        }
        return list;
    }

    public static String getToolkitURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(HOST);
        sb.append(TOOLKIT_SUBFOLDER);
        return sb.toString();
    }

    public static List<String> getToolkit() throws Exception {
        URL url = new URL(getToolkitURL());
        Response response = Response.fromJSON(IOUtil.readInputStream(url.openStream()));
        String json = response.getBody();
        return (List<String>) JSON.parse(json);
    }
}