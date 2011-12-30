package egats.web;

import egats.EGATProcess;
import java.util.List;

/**
 *
 * @author Augie
 */
public class EGATProcessCache {
    
    public static EGATProcess get(String id) throws Exception {
        return API.getProcess(id);
    }
    
    public static List<EGATProcess> get() throws Exception {
        return API.getProcesses((long) 0);
    }
}
