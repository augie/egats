package egats;

import com.mongodb.DBObject;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATProcess extends DataObject implements Runnable {

    public static final DataObjectCache<EGATProcess> CACHE = new DataObjectCache<EGATProcess>(Data.EGAT_PROCESSES, EGATProcess.class);
    public static final String STATUS_SUBMITTED = "Submitted";
    public static final String STATUS_RUNNING = "Running";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_FAILED = "Failed";
    private String status = STATUS_SUBMITTED;
    private String exceptionMessage;
    private Long createTime = System.currentTimeMillis();
    private Long startTime;
    private Long finishTime;

    public EGATProcess() throws Exception {
        this(false);
    }

    public EGATProcess(boolean createInDatabase) throws Exception {
        if (createInDatabase) {
            put("status", status);
            put("created", createTime);
            Data.insert(Data.EGAT_PROCESSES, this);
        }
    }

    public final void run() {
        try {
            // Set up
            setStatus(STATUS_RUNNING);
            setStartTime(System.currentTimeMillis());
            save();

            // Run the process

            // All done
            setStatus(STATUS_COMPLETED);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                setExceptionMessage("Null pointer encountered.");
            } else {
                setExceptionMessage(e.getMessage());
            }
            setStatus(STATUS_FAILED);
        } finally {
            setFinishTime(System.currentTimeMillis());
        }
        save();
    }

    public final void save() {
    }

    public final void setExceptionMessage(String message) {
        this.exceptionMessage = message;
    }

    public final void setFinishTime(long time) {
        this.finishTime = time;
    }

    public final void setStartTime(long time) {
        this.startTime = time;
    }

    public final void setStatus(String status) {
        this.status = status;
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
