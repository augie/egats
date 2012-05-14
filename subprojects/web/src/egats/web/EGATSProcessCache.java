package egats.web;

import egats.API;
import egats.EGATSProcess;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes are immutable after they are finished running.
 * @author Augie Hill - augie@umich.edu
 */
public class EGATSProcessCache {

    private static Map<String, SoftReference<EGATSProcess>> cache = new HashMap<String, SoftReference<EGATSProcess>>();

    /**
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public static EGATSProcess get(String id) throws Exception {
        if (id == null) {
            return null;
        }

        // Is the object in the cache?
        if (cache.containsKey(id) && cache.get(id).get() != null) {
            return cache.get(id).get();
        }

        // Get the object
        EGATSProcess o = API.getProcess(id);

        // Save the Java object to the cache if finished
        if (o.getFinishTime() != null) {
            cache.put(id, new SoftReference<EGATSProcess>(o));
        }

        return o;
    }

    /**
     * 
     * @return
     * @throws Exception 
     */
    public static List<EGATSProcess> get() throws Exception {
        return API.getProcessesByTimestamp(0l);
    }
}
