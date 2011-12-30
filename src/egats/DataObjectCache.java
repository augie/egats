package egats;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class DataObjectCache<T extends DataObject> {

    private Map<String, SoftReference<T>> cache = new HashMap<String, SoftReference<T>>();
    private DBCollection dc;
    private Class c;

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
    
    public final List<T> get(Long createTime) throws Exception {
        if (createTime == null) {
            return null;
        }
        
        // Must be greater than the given creation time
        DBObject query = new BasicDBObject();
        query.put("createTime", new BasicDBObject("$gt", createTime));
        DBCursor cursor = dc.find(query);
        
        // Process the results
        List<T> list = new LinkedList<T>();
        while (cursor.hasNext()) {
            // Convert the DB object to a Java object
            T o = convert(cursor.next());
            // Save the Java object to the cache
            cache.put(o.getID(), new SoftReference<T>(o));
            // Add the Java object to the list
            list.add(o);
        }
        
        return list;
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
