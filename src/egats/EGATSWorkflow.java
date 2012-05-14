package egats;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The sentences.
 * 
 * @author Augie
 */
public class EGATSWorkflow extends DataObject implements Runnable {

    public static final DataObjectCache<EGATSWorkflow> CACHE = new DataObjectCache<EGATSWorkflow>(Data.WORKFLOWS, EGATSWorkflow.class);
    public static final String STATUS_CREATED = "Created";
    public static final String STATUS_SUBMITTED = "Submitted";
    public static final String STATUS_RUNNING = "Running";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_FAILED = "Failed";
    // Transient variables are not serialized
    private transient Server server;
    private String status;
    private String exceptionMessage;
    private Long createTime;
    private Long startTime;
    private Long finishTime;
    private String name;
    private String[] args;
    private String classPath;
    private List<String> processIDs;

    /**
     * 
     */
    @Override
    public final void run() {
        try {
            // Set up
            setStatus(STATUS_RUNNING);
            setStartTime(System.currentTimeMillis());
            save();

            // Check the method
            if (classPath == null) {
                throw new Exception("Class path is not set.");
            }

            // Convert all '/' to '.'
            String cClassPath = classPath.replaceAll("/", ".").trim();
            if (cClassPath.endsWith(".")) {
                cClassPath = cClassPath.substring(0, cClassPath.length() - 1);
            }
            Class methodClass = server.getToolkit().getClass(cClassPath);
            if (methodClass == null) {
                throw new Exception("Class not found.");
            }

            // Instantiate the method to be run
            Method method = methodClass.getMethod("runWorkflow", new Class[0]);
            if (method == null) {
                throw new Exception("Unknown method name and argument class combination.");
            }

            // Instantiate the workflow object and set vars
            AbstractWorkflow w = (AbstractWorkflow) methodClass.newInstance();
            w.setArgs(Arrays.asList(args));
            w.setServer(server);
            w.setProcessIDList(processIDs);

            // Execute the method
            method.invoke(w, new Object[0]);

            // All done
            setStatus(STATUS_COMPLETED);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                setExceptionMessage("Null pointer encountered.");
            } else if (e instanceof NoSuchMethodException) {
                setExceptionMessage("No such method.");
            } else {
                setExceptionMessage(e.toString());
            }
            setStatus(STATUS_FAILED);
        } finally {
            setFinishTime(System.currentTimeMillis());
        }

        try {
            save();
        } catch (Exception e) {
            // Log
            // TODO
        }
    }

    /**
     * 
     * @return 
     */
    public final Server getServer() {
        return server;
    }

    /**
     * 
     * @param server 
     */
    public final void setServer(Server server) {
        this.server = server;
    }

    /**
     * 
     * @return 
     */
    public final String[] getArgs() {
        return args;
    }

    /**
     * 
     * @param args 
     */
    public final void setArgs(String[] args) {
        this.args = args;
        put("args", args);
    }

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
    public final String getExceptionMessage() {
        return exceptionMessage;
    }

    /**
     * 
     * @param message 
     */
    private void setExceptionMessage(String message) {
        this.exceptionMessage = message;
        put("exceptionMessage", message);
    }

    /**
     * 
     * @return 
     */
    public final Long getFinishTime() {
        return finishTime;
    }

    /**
     * 
     * @param time 
     */
    private void setFinishTime(Long time) {
        this.finishTime = time;
        put("finishTime", time);
    }

    /**
     * 
     * @return 
     */
    public final String getName() {
        return name;
    }

    /**
     * 
     * @param name 
     */
    public final void setName(String name) {
        this.name = name;
        put("name", name);
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
    public final Long getStartTime() {
        return startTime;
    }

    /**
     * 
     * @param time 
     */
    private void setStartTime(Long time) {
        this.startTime = time;
        put("startTime", time);
    }

    /**
     * 
     * @return 
     */
    public final String getStatus() {
        return status;
    }

    /**
     * 
     * @param status 
     */
    private void setStatus(String status) {
        this.status = status;
        put("status", status);
    }
    
    /**
     * 
     * @param index
     * @return
     * @throws Exception 
     */
    public EGATSProcess getProcess(int index) throws Exception {
        return EGATSProcess.CACHE.get(processIDs.get(index));
    }

    /**
     * 
     * @return 
     */
    public int getProcessCount() {
        return processIDs.size();
    }
    
    /**
     * 
     * @param processIDs 
     */
    private void setProcessIDs(List<String> processIDs) {
        this.processIDs = processIDs;
        put("processIDs", processIDs);
    }

    /**
     * 
     * @throws Exception 
     */
    protected final void save() throws Exception {
        Data.save(Data.WORKFLOWS, this);
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
        if (!(o instanceof EGATSWorkflow)) {
            return false;
        }
        return toString().equals(((EGATSWorkflow) o).toString());
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
     * 
     * @param json
     * @return
     * @throws Exception 
     */
    public static EGATSWorkflow create(String json) throws Exception {
        // Read the DBObject (attributes must be set separately)
        EGATSWorkflow o = CACHE.convert((DBObject) JSON.parse(json));
        // Set the attributes the user can set
        o.setName(o.getString("name"));
        o.setClassPath(o.getString("classPath"));
        o.setArgs(((BasicDBList) o.get("args")).toArray(new String[0]));
        // Set everything the client can't set
        o.removeField(DataObject.ATTR_ID);
        o.setCreateTime(System.currentTimeMillis());
        o.removeField("outputID");
        o.setStatus(STATUS_CREATED);
        o.removeField("exceptionMessage");
        o.removeField("startTime");
        o.removeField("finishTime");
        o.setProcessIDs(new ArrayList<String>());
        // Create a new entry in the database
        o.save();
        // Add it to the cache now because it will probably be referenced soon
        CACHE.insert(o.getID(), o);
        return o;
    }

    /**
     * 
     * @param json
     * @return
     * @throws Exception 
     */
    public static EGATSWorkflow read(String json) throws Exception {
        return read((DBObject) JSON.parse(json));
    }

    /**
     * 
     * @param dbo
     * @return
     * @throws Exception 
     */
    public static EGATSWorkflow read(DBObject dbo) throws Exception {
        EGATSWorkflow o = CACHE.convert(dbo);
        o.setName(o.getString("name"));
        o.setClassPath(o.getString("classPath"));
        o.setArgs(((BasicDBList) o.get("args")).toArray(new String[0]));
        o.setCreateTime(o.getLong("createTime"));
        o.setStatus(o.getString("status"));
        o.setExceptionMessage(o.getString("exceptionMessage"));
        o.setStartTime(o.getLong("startTime"));
        o.setProcessIDs((List<String>) o.get("processIDs"));
        if (o.containsField("finishTime")) {
            o.setFinishTime(o.getLong("finishTime"));
        }
        return o;
    }
}
