package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATSObject extends DataObject {

    public static final DataObjectCache<EGATSObject> CACHE = new DataObjectCache<EGATSObject>(Data.OBJECTS, EGATSObject.class);

    public EGATSObject() throws Exception {
        this(false);
    }

    public EGATSObject(boolean createInDatabase) throws Exception {
        if (createInDatabase) {
            Data.insert(Data.OBJECTS, this);
        }
    }

    public final String getJSON() {
        return Data.GSON.toJson(this);
    }
}
