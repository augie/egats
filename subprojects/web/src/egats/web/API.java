package egats.web;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import egats.EGATProcess;
import egats.EGATSObject;
import egats.Response;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Augie
 */
public class API {

    public static final String HOST = "http://egat.eecs.umich.edu:80";
    public static final String OBJECT_SUBFOLDER = "/o/";
    public static final String PROCESS_SUBFOLDER = "/p/";
    public static final String PROCESS_LIST_SUBFOLDER = "/pl/";

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
        List<EGATProcess> list = new LinkedList<EGATProcess>();
        for (DBObject o : dbObjectList) {
            list.add(EGATProcess.read(o));
        }
        return list;
    }
}