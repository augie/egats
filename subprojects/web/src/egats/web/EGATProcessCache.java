package egats.web;

import egats.EGATProcess;
import java.lang.ref.SoftReference;
import java.util.HashMap;
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

        // Get the object from the EGATS server
        String json = API.getProcessJSON(id);

        // Convert the result to a Java object
        EGATProcess o = EGATProcess.read(json);

        // Save the Java object to the cache
        cache.put(id, new SoftReference<EGATProcess>(o));

        return o;
    }
}
