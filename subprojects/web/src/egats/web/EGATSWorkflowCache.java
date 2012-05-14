package egats.web;

import egats.API;
import egats.EGATSWorkflow;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Workflows are immutable after they are finished running.
 * @author Augie Hill - augie@umich.edu
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
     * @param ids
     * @return
     * @throws Exception 
     */
    public static List<EGATSWorkflow> get(List<String> ids) throws Exception {
        if (ids == null) {
            return null;
        }

        List<EGATSWorkflow> workflows = new LinkedList<EGATSWorkflow>();
        if (ids.isEmpty()) {
            return workflows;
        }

        // Fill with null
        for (int i = 0; i < ids.size(); i++) {
            workflows.add(null);
        }

        // Get from the cache
        Map<String, Integer> idIndexMap = new HashMap<String, Integer>();
        List<String> workflowsToGet = new LinkedList<String>();
        for (int index = 0; index < ids.size(); index++) {
            String id = ids.get(index);
            if (cache.containsKey(id) && cache.get(id).get() != null) {
                workflows.add(index, cache.get(id).get());
            } else {
                idIndexMap.put(id, index);
                workflowsToGet.add(id);
            }
        }

        // Get remaining from server
        List<EGATSWorkflow> gotWorkflows = API.getWorkflows(workflowsToGet);
        // Put in return list and cache
        for (EGATSWorkflow o : gotWorkflows) {
            workflows.add(idIndexMap.get(o.getID()), o);
            // Save the Java object to the cache if finished
            if (o.getFinishTime() != null) {
                cache.put(o.getID(), new SoftReference<EGATSWorkflow>(o));
            }
        }

        return workflows;
    }

    /**
     * 
     * @return
     * @throws Exception 
     */
    public static List<EGATSWorkflow> get() throws Exception {
        return API.getWorkflowsByTimestamp(0l);
    }
}
