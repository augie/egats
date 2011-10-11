package egats;

import com.mongodb.DBObject;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATProcess extends DataObject implements Runnable {

    public static final String STATUS_SUBMITTED = "Submitted";
    private String status = STATUS_SUBMITTED;
    private long created = System.currentTimeMillis();

    public EGATProcess() throws Exception {
        this(true);
    }

    private EGATProcess(boolean createInDatabase) throws Exception {
        if (createInDatabase) {
            put("status", status);
            put("created", created);
            Data.insert(Data.EGAT_PROCESSES, this);
        }
    }

    public final void run() {
    }

    public final String getJSON() {
        return Data.GSON.toJson(this);
    }

    public static final EGATProcess convert(DBObject o) throws Exception {
        if (o == null) {
            return null;
        }
        EGATProcess n = new EGATProcess(false);
        for (String s : o.keySet()) {
            n.put(s, o.get(s));
        }
        return n;
    }
}
