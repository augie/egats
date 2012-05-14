package egats.web;

import egats.API;
import egats.EGATSObject;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Objects are immutable, so caching isn't a problem.
 * @author Augie Hill - augie@umich.edu
 */
public class EGATSObjectCache {

    private static Map<String, SoftReference<EGATSObject>> cache = new HashMap<String, SoftReference<EGATSObject>>();

    /**
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public static EGATSObject get(String id) throws Exception {
        if (id == null) {
            return null;
        }

        // Is the object in the cache?
        if (cache.containsKey(id) && cache.get(id).get() != null) {
            return cache.get(id).get();
        }

        // Get the object
        EGATSObject o = API.getObject(id);

        // Save the Java object to the cache
        cache.put(id, new SoftReference<EGATSObject>(o));

        return o;
    }

    /**
     * 
     * @param ids
     * @return
     * @throws Exception 
     */
    public static List<EGATSObject> get(List<String> ids) throws Exception {
        if (ids == null) {
            return null;
        }

        List<EGATSObject> objects = new LinkedList<EGATSObject>();
        if (ids.isEmpty()) {
            return objects;
        }

        // Fill with null
        for (int i = 0; i < ids.size(); i++) {
            objects.add(null);
        }

        // Get from the cache
        Map<String, Integer> idIndexMap = new HashMap<String, Integer>();
        List<String> objectsToGet = new LinkedList<String>();
        for (int index = 0; index < ids.size(); index++) {
            String id = ids.get(index);
            if (cache.containsKey(id) && cache.get(id).get() != null) {
                objects.add(index, cache.get(id).get());
            } else {
                idIndexMap.put(id, index);
                objectsToGet.add(id);
            }
        }

        // Get remaining from server
        List<EGATSObject> gotObjects = API.getObjects(objectsToGet);
        // Put in return list and cache
        for (EGATSObject o : gotObjects) {
            objects.add(idIndexMap.get(o.getID()), o);
            // Save the Java object to the cache
            cache.put(o.getID(), new SoftReference<EGATSObject>(o));
        }

        return objects;
    }
}
