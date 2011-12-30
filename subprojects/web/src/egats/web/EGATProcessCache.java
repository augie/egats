package egats.web;

import egats.EGATProcess;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Augie
 */
public class EGATProcessCache {
    
    private static Map<String, SoftReference<EGATProcess>> cache = new HashMap<String, SoftReference<EGATProcess>>();
    
    public static EGATProcess get(String id) throws Exception {
        if (id == null) {
            return null;
        }

        // Is the object in the cache?
        if (cache.containsKey(id) && cache.get(id).get() != null) {
            return cache.get(id).get();
        }

        // Get the process
        EGATProcess o = API.getProcess(id);

        // Save the Java object to the cache
        cache.put(id, new SoftReference<EGATProcess>(o));
        
        return o;
    }
    
    public static List<EGATProcess> get() throws Exception {
        return API.getProcesses((long) 0);
    }
}
