package egats.web;

import egats.API;
import egats.EGATSProcess;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
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
     * @param ids
     * @return
     * @throws Exception 
     */
    public static List<EGATSProcess> get(List<String> ids) throws Exception {
        if (ids == null) {
            return null;
        }

        List<EGATSProcess> processes = new LinkedList<EGATSProcess>();
        if (ids.isEmpty()) {
            return processes;
        }

        // Fill with null
        for (int i = 0; i < ids.size(); i++) {
            processes.add(null);
        }

        // Get from the cache
        Map<String, Integer> idIndexMap = new HashMap<String, Integer>();
        List<String> processesToGet = new LinkedList<String>();
        for (int index = 0; index < ids.size(); index++) {
            String id = ids.get(index);
            if (cache.containsKey(id) && cache.get(id).get() != null) {
                processes.add(index, cache.get(id).get());
            } else {
                idIndexMap.put(id, index);
                processesToGet.add(id);
            }
        }

        // Get remaining from server
        List<EGATSProcess> gotProcesses = API.getProcesses(processesToGet);
        // Put in return list and cache
        for (EGATSProcess o : gotProcesses) {
            processes.add(idIndexMap.get(o.getID()), o);
            // Save the Java object to the cache if finished
            if (o.getFinishTime() != null) {
                cache.put(o.getID(), new SoftReference<EGATSProcess>(o));
            }
        }

        return processes;
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
