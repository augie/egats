package egats;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class Flags {

    // Define flags
    public static final String HELP = "h";
    public static final String PORT = "p";
    public static final String REQUEST_PROCESSING_THREADS = "rpt";
    public static final String REQUEST_PROCESSING_QUEUE = "rpq";
    // The flag map maps the flag to the expected number of inputs for that flag
    private static final Map<String, Integer> FLAG_MAP = new HashMap<String, Integer>();
    private static final Map<String, String> FLAG_ALTERNATIVES_MAP = new HashMap<String, String>();
    // Default values for flags
    private static final Map<String, List<String>> DEFAULTS = new HashMap<String, List<String>>();
    // Stores the flags entered by the user
    private final Map<String, List<String>> flags = new HashMap<String, List<String>>();

    static {
        // Help flag
        FLAG_MAP.put(HELP, 0);
        FLAG_ALTERNATIVES_MAP.put("help", HELP);

        // Port flag
        FLAG_MAP.put(PORT, 1);
        FLAG_ALTERNATIVES_MAP.put("port", PORT);

        // Request processing threads flag
        FLAG_MAP.put(REQUEST_PROCESSING_THREADS, 1);
        FLAG_ALTERNATIVES_MAP.put("request-processing-threads", REQUEST_PROCESSING_THREADS);

        // Request processing threads flag
        FLAG_MAP.put(REQUEST_PROCESSING_QUEUE, 1);
        FLAG_ALTERNATIVES_MAP.put("request-processing-queue", REQUEST_PROCESSING_QUEUE);
    }

    public Flags() {
        this(new String[0]);
    }

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

    public final boolean contains(String flag) {
        return flags.containsKey(flag);
    }

    public final List<String> get(String flag) {
        List<String> value = flags.get(flag);
        if (value == null) {
            value = DEFAULTS.get(flag);
        }
        return value;
    }

    public final Boolean getBoolean(String flag) {
        return Boolean.valueOf(get(flag).get(0));
    }

    public final Double getDouble(String flag) {
        return Double.valueOf(get(flag).get(0));
    }

    public final Integer getInt(String flag) {
        return Integer.valueOf(get(flag).get(0));
    }

    public final String getString(String flag) {
        return get(flag).get(0);
    }

    public static final void setDefault(String flag, Boolean value) {
        List<String> args = new LinkedList<String>();
        args.add(String.valueOf(value));
        DEFAULTS.put(flag, args);
    }

    public static final void setDefault(String flag, Integer value) {
        List<String> args = new LinkedList<String>();
        args.add(String.valueOf(value));
        DEFAULTS.put(flag, args);
    }
}
