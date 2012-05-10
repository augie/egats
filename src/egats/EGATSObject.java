package egats;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * The nouns.
 * 
 * @author Augie Hill - augie@umich.edu
 */
public class EGATSObject extends DataObject {

    public static final DataObjectCache<EGATSObject> CACHE = new DataObjectCache<EGATSObject>(Data.OBJECTS, EGATSObject.class);
    private Long createTime;
    private String classPath;
    private String object;

    /**
     * 
     * @return 
     */
    public final Long getCreateTime() {
        return createTime;
    }

    /**
     * 
     * @param time 
     */
    private void setCreateTime(Long time) {
        this.createTime = time;
        put("createTime", time);
    }

    /**
     * 
     * @return 
     */
    public final String getClassPath() {
        return classPath;
    }

    /**
     * 
     * @param classPath 
     */
    public final void setClassPath(String classPath) {
        this.classPath = classPath;
        put("classPath", classPath);
    }

    /**
     * 
     * @return 
     */
    public final String getObject() {
        return object;
    }

    /**
     * 
     * @param object 
     */
    public final void setObject(String object) {
        this.object = object;
        put("object", object);
    }

    /**
     * 
     * @throws Exception 
     */
    public final void save() throws Exception {
        if (!containsField("createTime")) {
            setCreateTime(System.currentTimeMillis());
        }
        Data.save(Data.OBJECTS, this);
    }

    /**
     * 
     * @return 
     */
    public final String getJSON() {
        return JSON.serialize(this);
    }

    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EGATSObject)) {
            return false;
        }
        return toString().equals(((EGATSObject) o).toString());
    }

    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return getJSON();
    }

    /**
     * Cleans the object JSON and adds it to the database.
     * 
     * @param json
     * @return
     * @throws Exception 
     */
    public static EGATSObject create(String json) throws Exception {
        // Read the DBObject (attributes must be set separately)
        EGATSObject o = CACHE.convert((DBObject) JSON.parse(json));
        // Set the attributes the user can set
        o.setClassPath(o.getString("classPath"));
        o.setObject(o.getString("object"));
        // Set everything the client can't set
        o.removeField(DataObject.ATTR_ID);
        o.setCreateTime(System.currentTimeMillis());
        // Create a new entry in the database
        o.save();
        // Add it to the cache now because it will probably be referenced soon
        CACHE.insert(o.getID(), o);
        return o;
    }

    /**
     * Reads an object already in the database.
     * 
     * @param json
     * @return
     * @throws Exception 
     */
    public static EGATSObject read(String json) throws Exception {
        EGATSObject o = CACHE.convert((DBObject) JSON.parse(json));
        o.setClassPath(o.getString("classPath"));
        o.setObject(o.getString("object"));
        if (o.containsField("createTime")) {
            o.setCreateTime(o.getLong("createTime"));
        }
        // Add it to the cache now because it will probably be referenced soon
        CACHE.insert(o.getID(), o);
        return o;
    }
}
