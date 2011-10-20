package egats;

import com.mongodb.BasicDBObject;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class DataObject extends BasicDBObject {

    public static final String ATTR_ID = "_id";

    public final String getID() {
        return getString(ATTR_ID);
    }
}
