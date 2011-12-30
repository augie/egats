package egats.web;

import egats.Response;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author Augie
 */
public class API {

    public static final String HOST = "http://egat.eecs.umich.edu:80";
    public static final String OBJECT_SUBFOLDER = "/o/";
    public static final String PROCESS_SUBFOLDER = "/p/";

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
}
