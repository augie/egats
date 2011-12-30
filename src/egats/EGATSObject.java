package egats;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATSObject extends DataObject {

    public static final DataObjectCache<EGATSObject> CACHE = new DataObjectCache<EGATSObject>(Data.OBJECTS, EGATSObject.class);
    private Long createTime;
    private String classPath;
    private String object;

    public final Long getCreateTime() {
        return createTime;
    }

    private void setCreateTime(Long time) {
        this.createTime = time;
        put("createTime", time);
    }

    public final String getClassPath() {
        return classPath;
    }

    public final void setClassPath(String classPath) {
        this.classPath = classPath;
        put("classPath", classPath);
    }

    public final String getObject() {
        return object;
    }

    public final void setObject(String object) {
        this.object = object;
        put("object", object);
    }

    public final void save() throws Exception {
        if (!containsField("createTime")) {
            setCreateTime(System.currentTimeMillis());
        }
        Data.save(Data.OBJECTS, this);
    }

    public final String getJSON() {
        return JSON.serialize(this);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EGATSObject)) {
            return false;
        }
        return toString().equals(((EGATSObject) o).toString());
    }

    @Override
    public String toString() {
        return getJSON();
    }

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

    public static EGATSObject read(String json) throws Exception {
        EGATSObject o = CACHE.convert((DBObject) JSON.parse(json));
        o.setClassPath(o.getString("classPath"));
        o.setObject(o.getString("object"));
        if (o.containsField("createTime")) {
            o.setCreateTime(o.getLong("createTime"));
        }
        return o;
    }
}
