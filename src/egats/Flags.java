package egats;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Execution flag management.
 * @author Augie Hill - augie@umich.edu
 */
public class Flags {

    public static boolean TESTING = false;
    // Define flags
    public static final String EGAT_PROCESSING_THREADS = "ept";
    public static final String EGAT_PROCESSING_QUEUE = "epq";
    public static final String HELP = "h";
    public static final String HOST = "H";
    public static final String PORT = "p";
    public static final String REQUEST_PROCESSING_THREADS = "rpt";
    public static final String REQUEST_PROCESSING_QUEUE = "rpq";
    public static final String TOOLKIT = "t";
    public static final String TOOLS_CONFIG = "tc";
    public static final String WORK = "w";
    public static final String WORKFLOW_PROCESSING_THREADS = "wpt";
    public static final String WORKFLOW_PROCESSING_QUEUE = "wpq";
    // The flag map maps the flag to the expected number of inputs for that flag
    private static final Map<String, Integer> FLAG_MAP = new HashMap<String, Integer>();
    private static final Map<String, String> FLAG_ALTERNATIVES_MAP = new HashMap<String, String>();
    // Default values for flags
    private static final Map<String, List<String>> DEFAULTS = new HashMap<String, List<String>>();
    // Stores the flags entered by the user
    private final Map<String, List<String>> flags = new HashMap<String, List<String>>();

    static {
        // Processing threads flag
        FLAG_MAP.put(EGAT_PROCESSING_THREADS, 1);
        FLAG_ALTERNATIVES_MAP.put("egat-processing-threads", EGAT_PROCESSING_THREADS);

        // Processing queue size flag
        FLAG_MAP.put(EGAT_PROCESSING_QUEUE, 1);
        FLAG_ALTERNATIVES_MAP.put("egat-processing-queue", EGAT_PROCESSING_QUEUE);
        
        // Help flag
        FLAG_MAP.put(HELP, 0);
        FLAG_ALTERNATIVES_MAP.put("help", HELP);

        // Host flag
        FLAG_MAP.put(HOST, 1);
        FLAG_ALTERNATIVES_MAP.put("host", HOST);

        // Port flag
        FLAG_MAP.put(PORT, 1);
        FLAG_ALTERNATIVES_MAP.put("port", PORT);

        // Request processing threads flag
        FLAG_MAP.put(REQUEST_PROCESSING_THREADS, 1);
        FLAG_ALTERNATIVES_MAP.put("request-processing-threads", REQUEST_PROCESSING_THREADS);

        // Request processing queue size flag
        FLAG_MAP.put(REQUEST_PROCESSING_QUEUE, 1);
        FLAG_ALTERNATIVES_MAP.put("request-processing-queue", REQUEST_PROCESSING_QUEUE);
        
        // Toolkit flag
        FLAG_MAP.put(TOOLKIT, 1);
        FLAG_ALTERNATIVES_MAP.put("toolkit", TOOLKIT);
        
        // Toolkit configuration flag
        FLAG_MAP.put(TOOLS_CONFIG, 1);
        FLAG_ALTERNATIVES_MAP.put("tools-config", TOOLS_CONFIG);

        // Work flag
        FLAG_MAP.put(WORK, 1);
        FLAG_ALTERNATIVES_MAP.put("work", WORK);
        
        // Workflow processing threads flag
        FLAG_MAP.put(WORKFLOW_PROCESSING_THREADS, 1);
        FLAG_ALTERNATIVES_MAP.put("workflow-processing-threads", WORKFLOW_PROCESSING_THREADS);

        // Workflow processing queue size flag
        FLAG_MAP.put(WORKFLOW_PROCESSING_QUEUE, 1);
        FLAG_ALTERNATIVES_MAP.put("workflow-processing-queue", WORKFLOW_PROCESSING_QUEUE);
    }

    /**
     * 
     */
    public Flags() {
        this(new String[0]);
    }

    /**
     * 
     * @param args 
     */
    public Flags(String[] args) {
        final int argsLength = args.length;
        for (int i = 0; i < argsLength; i++) {
            String flag = args[i];
            // Strip leading -'s
            while (flag.startsWith("-")) {
                flag = flag.substring(1);
            }
            // Switch alternative flags with the primary flag
            if (FLAG_ALTERNATIVES_MAP.containsKey(flag)) {
                flag = FLAG_ALTERNATIVES_MAP.get(flag);
            }
            // Check for valid flag
            if (!FLAG_MAP.containsKey(flag)) {
                throw new RuntimeException("Unknown flag: " + flag);
            }
            // Save the flag and its arguments
            flags.put(flag, new LinkedList<String>());
            // How many args are expected for this flag?
            final int expectedArgs = FLAG_MAP.get(flag);
            // Are we expected any arguments to be associated with this flag?
            if (expectedArgs > 0) {
                // If there aren't that many args remaining, then there is a problem.
                if (i + expectedArgs >= argsLength) {
                    throw new RuntimeException("Expected " + expectedArgs + " for flag: " + flag);
                }
                // Get arguments for this flag
                for (int j = i + 1; j <= i + expectedArgs; j++) {
                    flags.get(flag).add(args[j]);
                }
                // Skip over flag arguments in the outer loop
                i += expectedArgs;
            }
        }
    }

    /**
     * 
     * @param flag
     * @return 
     */
    public final boolean contains(String flag) {
        return flags.containsKey(flag);
    }

    /**
     * 
     * @param flag
     * @return 
     */
    public final List<String> get(String flag) {
        List<String> value = flags.get(flag);
        if (value == null) {
            value = DEFAULTS.get(flag);
        }
        return value;
    }

    /**
     * 
     * @param flag
     * @return 
     */
    public final Boolean getBoolean(String flag) {
        return Boolean.valueOf(get(flag).get(0));
    }

    /**
     * 
     * @param flag
     * @return 
     */
    public final Double getDouble(String flag) {
        return Double.valueOf(get(flag).get(0));
    }

    /**
     * 
     * @param flag
     * @return 
     */
    public final Integer getInt(String flag) {
        return Integer.valueOf(get(flag).get(0));
    }

    /**
     * 
     * @param flag
     * @return 
     */
    public final String getString(String flag) {
        return get(flag).get(0);
    }

    /**
     * 
     * @param flag
     * @param value 
     */
    public static void setDefault(String flag, Object value) {
        List<String> args = new LinkedList<String>();
        args.add(String.valueOf(value));
        DEFAULTS.put(flag, args);
    }
    
    /**
     * 
     * @param flag
     * @return 
     */
    public static Object getDefault(String flag) {
        return DEFAULTS.get(flag).get(0);
    }
}
