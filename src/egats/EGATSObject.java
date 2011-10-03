package egats;

import com.mongodb.DBObject;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATSObject extends DataObject {

    public EGATSObject() throws Exception {
        this(true);
    }

    private EGATSObject(boolean createInDatabase) throws Exception {
        if (createInDatabase) {
            Data.insert(Data.OBJECTS, this);
        }
    }

    public final String getJSON() {
        return Data.GSON.toJson(this);
    }

    public static final EGATSObject convert(DBObject o) throws Exception {
        if (o == null) {
            return null;
        }
        EGATSObject n = new EGATSObject(false);
        for (String s : o.keySet()) {
            n.put(s, o.get(s));
        }
        return n;
    }
}
