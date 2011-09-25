package egats;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Augie
 */
public class Server {

    // Define flags
    public static final String FLAG_HELP = "h";
    public static final String FLAG_PORT = "p";
    // The flag map maps the flag to the expected number of inputs for that flag
    private static final Map<String, Integer> FLAG_MAP = new HashMap<String, Integer>();
    private static final Map<String, String> FLAG_ALTERNATIVES_MAP = new HashMap<String, String>();
    private RequestListeningThread listener = null;
    private ResponseSendingThread responder = null;

    static {
        // Help flag
        FLAG_MAP.put(FLAG_HELP, 0);
        FLAG_ALTERNATIVES_MAP.put("help", FLAG_HELP);

        // Port flag
        FLAG_MAP.put(FLAG_PORT, 1);
        FLAG_ALTERNATIVES_MAP.put("port", FLAG_PORT);
    }

    public Server() {
        // Create and start a request listening thread and a response sending thread
        listener = new RequestListeningThread(this);
        responder = new ResponseSendingThread(this);
    }

    public Server(int port) {
        // Create and start a request listening thread and a response sending thread
        listener = new RequestListeningThread(this, port);
        responder = new ResponseSendingThread(this);
    }

    public final RequestListeningThread getListener() {
        return listener;
    }

    public final ResponseSendingThread getResponder() {
        return responder;
    }

    public final void logException(Exception e) {
        e.printStackTrace();
    }

    public final void start() {
        // Start listening and responding to requests.
        listener.start();
        responder.start();
    }

    public static final void main(String[] args) throws Exception {
        // Process the arguments
        Map<String, List<String>> flags = new HashMap<String, List<String>>();
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

        // If the help flag is set, then print the manual and exit
        if (flags.containsKey(FLAG_HELP)) {
            // TODO: print the real manual
            System.out.println("Manual");
            // Print the manual
            System.exit(0);
        }

        // Start the server
        Server s;
        if (flags.containsKey(FLAG_PORT)) {
            s = new Server(Integer.valueOf(flags.get(FLAG_PORT).get(0)));
        } else {
            s = new Server();
        }
        s.start();
    }
}
