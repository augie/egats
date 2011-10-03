package egats;

import com.mongodb.BasicDBObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Need to find a way to do these caches using generics.
 * @author Augie
 */
public class EGATProcessCache {

    private static final Map<String, EGATProcess> cache = new HashMap<String, EGATProcess>();

    public static final EGATProcess get(String id) throws Exception {
        // Is the object in the cache?
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        
        // get from the database
        BasicDBObject query = new BasicDBObject();
        query.put(EGATProcess.ATTR_ID, id);

        // Convert the result to a Java object
        EGATProcess o = EGATProcess.convert(Data.EGAT_PROCESSES.findOne(query));

        // Save the Java object to the cache
        cache.put(id, o);
        
        return o;
    }
}
