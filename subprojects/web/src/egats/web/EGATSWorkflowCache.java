package egats.web;

import egats.EGATSWorkflow;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Workflows are immutable after they are finished running.
 * @author Augie
 */
public class EGATSWorkflowCache {

    private static Map<String, SoftReference<EGATSWorkflow>> cache = new HashMap<String, SoftReference<EGATSWorkflow>>();

    /**
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public static EGATSWorkflow get(String id) throws Exception {
        if (id == null) {
            return null;
        }

        // Is the object in the cache?
        if (cache.containsKey(id) && cache.get(id).get() != null) {
            return cache.get(id).get();
        }

        // Get the object
        EGATSWorkflow o = API.getWorkflow(id);

        // Save the Java object to the cache if finished
        if (o.getFinishTime() != null) {
            cache.put(id, new SoftReference<EGATSWorkflow>(o));
        }

        return o;
    }

    /**
     * 
     * @return
     * @throws Exception 
     */
    public static List<EGATSWorkflow> get() throws Exception {
        return API.getWorkflows((long) 0);
    }
}
