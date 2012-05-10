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
 * Data structure for caching generic objects retrieved from the database.
 * Uses soft references, so the cache will fill memory only as it is available.
 * 
 * @author Augie Hill - augie@umich.edu
 */
public class DataObjectCache<T extends DataObject> {

    private final Map<String, SoftReference<T>> cache = new HashMap<String, SoftReference<T>>();
    private DBCollection dc;
    private Class c;

    /**
     * 
     * @param s
     * @param dc
     * @param c 
     */
    public DataObjectCache(DBCollection dc, Class<T> c) {
        this.dc = dc;
        this.c = c;
    }
    
    /**
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public final boolean contains(String id) throws Exception {
        // Checks
        if (id == null) {
            throw new NullPointerException("ID is null.");
        }
        synchronized (cache) {
            return cache.containsKey(id);
        }
    }

    /**
     * Gets the object with the given ID from the first of:
     *  (1) Cache
     *  (2) Database
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public final T get(String id) throws Exception {
        if (id == null) {
            return null;
        }

        // Is the object in the cache?
        synchronized (cache) {
            if (cache.containsKey(id) && cache.get(id).get() != null) {
                return cache.get(id).get();
            }
        }
        
        // No database when testing
        if (Flags.TESTING) {
            return null;
        }

        // Get from the database
        BasicDBObject query = new BasicDBObject(DataObject.ATTR_ID, new ObjectId(id));
        DBObject result = dc.findOne(query);
        if (result == null) {
            return null;
        }

        // Convert the result to a Java object
        T o = convert(result);

        // Save the Java object to the cache
        synchronized (cache) {
            cache.put(id, new SoftReference<T>(o));
        }

        return o;
    }

    /**
     * Queries the database for objects created after the given time.
     * 
     * @param createTime
     * @return
     * @throws Exception 
     */
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
            synchronized (cache) {
                // Save the Java object to the cache
                cache.put(o.getID(), new SoftReference<T>(o));
            }
            // Add the Java object to the list
            list.add(o);
        }

        return list;
    }

    /**
     * Caches the given object.
     * 
     * @param id
     * @param t 
     */
    public final void insert(String id, T t) {
        synchronized (cache) {
            cache.put(id, new SoftReference<T>(t));
        }
    }

    /**
     * Converts the given database object to the generic object for the data structure.
     * 
     * @param o
     * @return
     * @throws Exception 
     */
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
