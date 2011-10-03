package egats;

import com.mongodb.BasicDBObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Need to find a way to do these caches using generics.
 * @author Augie
 */
public class EGATSObjectCache {

    private static final Map<String, EGATSObject> cache = new HashMap<String, EGATSObject>();

    public static final EGATSObject get(String id) throws Exception {
        // Is the object in the cache?
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        
        // get from the database
        BasicDBObject query = new BasicDBObject();
        query.put(EGATSObject.ATTR_ID, id);

        // Convert the result to a Java object
        EGATSObject o = EGATSObject.convert(Data.OBJECTS.findOne(query));

        // Save the Java object to the cache
        cache.put(id, o);
        
        return o;
    }
}
