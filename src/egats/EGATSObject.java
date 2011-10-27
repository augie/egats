package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATSObject extends DataObject {

    public static final DataObjectCache<EGATSObject> CACHE = new DataObjectCache<EGATSObject>(Data.OBJECTS, EGATSObject.class);
    private String classPath;
    private String object;

    public final String getJSON() {
        return Data.GSON.toJson(this);
    }
}
