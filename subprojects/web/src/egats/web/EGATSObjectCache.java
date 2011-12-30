package egats.web;

import egats.EGATSObject;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Augie
 */
public class EGATSObjectCache {

    private static Map<String, SoftReference<EGATSObject>> cache = new HashMap<String, SoftReference<EGATSObject>>();

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
}
