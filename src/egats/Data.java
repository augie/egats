package egats;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class Data {

    private static int TEST_ID = 0;
    public static final Gson GSON = new Gson();
    public static Mongo MONGO;
    public static DB EGATS;
    public static DBCollection EGAT_PROCESSES, OBJECTS;

    static {
        try {
            MONGO = new Mongo("localhost", 27017);
            EGATS = MONGO.getDB("egats");
            EGAT_PROCESSES = EGATS.getCollection("egat_processes");
            OBJECTS = EGATS.getCollection("objects");
        } catch (Exception e) {
            // Log
            // TODO
        }
    }

    public static void save(DBCollection c, DataObject o) throws Exception {
        // Do not do anything if testing
        if (Flags.TESTING) {
            // Fake an ID if necessary
            if (!o.containsField(DataObject.ATTR_ID)) {
                o.put(DataObject.ATTR_ID, String.valueOf(TEST_ID++));
            }
            return;
        }
        // Save to the database
        WriteResult r = c.save(o);
        // Check for error
        String error = r.getError();
        if (error != null && !error.equals("")) {
            throw new Exception(error);
        }
    }
}
