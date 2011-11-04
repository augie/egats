package egats;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.lang.reflect.Method;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATProcess extends DataObject implements Runnable {

    public static final DataObjectCache<EGATProcess> CACHE = new DataObjectCache<EGATProcess>(Data.EGAT_PROCESSES, EGATProcess.class);
    public static final String STATUS_CREATED = "Created";
    public static final String STATUS_SUBMITTED = "Submitted";
    public static final String STATUS_RUNNING = "Running";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_FAILED = "Failed";
    private transient Server server;
    private String status;
    private String exceptionMessage;
    private Long createTime;
    private Long startTime;
    private Long finishTime;
    private String methodPath;
    private String[] args;
    private String outputID;

    public final void run() {
        try {
            // Set up
            setStatus(STATUS_RUNNING);
            setStartTime(System.currentTimeMillis());
            save();

            // Check the args
            String[] mArgs = args;
            if (mArgs == null) {
                mArgs = new String[0];
            }
            // Get the objects from the cache
            EGATSObject[] argEObjs = new EGATSObject[mArgs.length];
            for (int i = 0; i < mArgs.length; i++) {
                argEObjs[i] = EGATSObject.CACHE.get(mArgs[i]);
                if (argEObjs[i] == null) {
                    throw new Exception("Could not find object with id \"" + mArgs[i] + "\".");
                }
            }
            // Generate objects
            Class[] argCObjs = new Class[argEObjs.length];
            Object[] argObjs = new Object[argEObjs.length];
            for (int i = 0; i < argEObjs.length; i++) {
                String classPath = argEObjs[i].getClassPath().replaceAll("/", ".").trim();
                argCObjs[i] = server.getClassLoader().getClass(classPath);
                argObjs[i] = argCObjs[i].cast(Data.GSON.fromJson(argEObjs[i].getObject(), argCObjs[i]));
            }

            // Check the method
            if (methodPath == null) {
                throw new Exception("Method path is not set.");
            }
            // Convert all '/' to '.'
            String cMethodPath = methodPath.replaceAll("/", ".").trim();
            String methodClassPath = cMethodPath.substring(0, cMethodPath.lastIndexOf("."));
            Class methodClass = server.getClassLoader().getClass(methodClassPath);
            String methodName = cMethodPath.substring(cMethodPath.lastIndexOf(".") + 1);
            Method method = methodClass.getMethod(methodName, argCObjs);
            if (method == null) {
                throw new Exception("Unknown method name and argument class combination.");
            }

            // Execute the static method (need to create a dummy instance even though it's a static method)
            Object output = method.invoke(methodClass.newInstance(), argObjs);

            // Create an EGATSObject from the output
            EGATSObject o = new EGATSObject();
            o.setClassPath(method.getReturnType().getName());
            o.setObject(Data.GSON.toJson(output));
            // Save it
            o.save();
            // Add it to the cache
            EGATSObject.CACHE.insert(o.getID(), o);

            // Save the ID of the output object
            setOutputID(o.getID().intern());

            // All done
            setStatus(STATUS_COMPLETED);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof NullPointerException) {
                setExceptionMessage("Null pointer encountered.");
            } else if (e instanceof NoSuchMethodException) {
                setExceptionMessage("No such method.");
            } else {
                setExceptionMessage(e.getMessage());
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

    public final Server getServer() {
        return server;
    }

    public final void setServer(Server server) {
        this.server = server;
    }

    public final String[] getArgs() {
        return args;
    }

    public final void setArgs(String[] args) {
        this.args = args;
        put("args", args);
    }

    public final Long getCreateTime() {
        return createTime;
    }

    private final void setCreateTime(Long time) {
        this.createTime = time;
        put("createTime", time);
    }

    public final String getExceptionMessage() {
        return exceptionMessage;
    }

    private final void setExceptionMessage(String message) {
        this.exceptionMessage = message;
        put("exceptionMessage", message);
    }

    public final Long getFinishTime() {
        return finishTime;
    }

    private final void setFinishTime(Long time) {
        this.finishTime = time;
        put("finishTime", time);
    }

    public final String getMethodPath() {
        return methodPath;
    }

    public final void setMethodPath(String methodPath) {
        this.methodPath = methodPath;
        put("methodPath", methodPath);
    }

    public final String getOutputID() {
        return outputID;
    }

    private final void setOutputID(String outputID) {
        this.outputID = outputID;
        put("outputID", outputID);
    }

    public final Long getStartTime() {
        return startTime;
    }

    private final void setStartTime(Long time) {
        this.startTime = time;
        put("startTime", time);
    }

    public final String getStatus() {
        return status;
    }

    private final void setStatus(String status) {
        this.status = status;
        put("status", status);
    }

    protected final void save() throws Exception {
        Data.save(Data.EGAT_PROCESSES, this);
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
        if (!(o instanceof EGATProcess)) {
            return false;
        }
        return toString().equals(((EGATProcess) o).toString());
    }

    @Override
    public String toString() {
        return getJSON();
    }

    public static final EGATProcess create(String json) throws Exception {
        // Read the DBObject (attributes must be set separately)
        EGATProcess o = CACHE.convert((DBObject) JSON.parse(json));
        // Set the attributes the user can set
        o.setMethodPath(o.getString("methodPath"));
        o.setArgs(((BasicDBList) o.get("args")).toArray(new String[0]));
        // Set everything the client can't set
        o.removeField(DataObject.ATTR_ID);
        o.setCreateTime(System.currentTimeMillis());
        o.removeField("outputID");
        o.setStatus(STATUS_CREATED);
        o.removeField("exceptionMessage");
        o.removeField("startTime");
        o.removeField("finishTime");
        // Create a new entry in the database
        o.save();
        // Add it to the cache now because it will probably be referenced soon
        CACHE.insert(o.getID(), o);
        return o;
    }

    public static final EGATProcess read(String json) throws Exception {
        EGATProcess o = CACHE.convert((DBObject) JSON.parse(json));
        o.setMethodPath(o.getString("methodPath"));
        o.setArgs(((BasicDBList) o.get("args")).toArray(new String[0]));
        o.setCreateTime(o.getLong("createTime"));
        o.setOutputID(o.getString("outputID"));
        o.setStatus(o.getString("status"));
        o.setExceptionMessage(o.getString("exceptionMessage"));
        o.setCreateTime(o.getLong("startTime"));
        o.setFinishTime(o.getLong("finishTime"));
        return o;
    }
}
