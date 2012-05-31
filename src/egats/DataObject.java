package egats;

import com.mongodb.BasicDBObject;

/**
 * The basic database object for this project providing ease-of-use methods.
 * @author Augie Hill - augie@umich.edu
 */
public class DataObject extends BasicDBObject {

    public static final String ATTR_ID = "_id";

    /**
     * 
     * @return 
     */
    public final String getID() {
        return getString(ATTR_ID);
    }

    /**
     * 
     * @param id 
     */
    protected final void setID(String id) {
        put(ATTR_ID, id);
    }
}
