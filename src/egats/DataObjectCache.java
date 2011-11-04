package egats;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import org.bson.types.ObjectId;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class DataObjectCache<T extends DataObject> {

    private final Map<String, SoftReference<T>> cache = new HashMap<String, SoftReference<T>>();
    private final DBCollection dc;
    private final Class c;

    public DataObjectCache(DBCollection dc, Class<T> c) {
        this.dc = dc;
        this.c = c;
    }

    public final T get(String id) throws Exception {
        if (id == null) {
            return null;
        }

        // Is the object in the cache?
        if (cache.containsKey(id) && cache.get(id).get() != null) {
            return cache.get(id).get();
        }

        // get from the database
        BasicDBObject query = new BasicDBObject(DataObject.ATTR_ID, new ObjectId(id));

        // Convert the result to a Java object
        T o = convert(dc.findOne(query));

        // Save the Java object to the cache
        cache.put(id, new SoftReference<T>(o));

        return o;
    }

    public final void insert(String id, T t) {
        cache.put(id, new SoftReference<T>(t));
    }

    public final T convert(DBObject o) throws Exception {
        if (o == null) {
            return null;
        }
        T n = (T) c.newInstance();
        for (String s : o.keySet()) {
            ((DataObject) n).put(s, o.get(s));
        }
        return n;
    }
}
