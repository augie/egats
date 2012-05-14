package egats;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class Server {
    
    public static final String DEFAULT_HOST = "localhost";
    private final RequestListeningThread listener;
    private final RequestProcessExecutor requestExecutor;
    private final EGATSProcessExecutor processExecutor;
    private final EGATSWorkflowExecutor workflowExecutor;
    private final Toolkit toolkit;
    private final WorkFileManager workFileManager;
    private final Flags flags;
    private boolean listening = false;

    static {
        Flags.setDefault(Flags.HOST, DEFAULT_HOST);
    }

    /**
     * 
     * @param flags
     * @throws Exception 
     */
    public Server(Flags flags) throws Exception {
        this.flags = flags;
        listener = new RequestListeningThread(this);
        requestExecutor = new RequestProcessExecutor(this);
        processExecutor = new EGATSProcessExecutor(this);
        workflowExecutor = new EGATSWorkflowExecutor(this);
        toolkit = new Toolkit(this);
        workFileManager = new WorkFileManager(this);
    }

    /**
     * 
     */
    public final synchronized void close() {
        if (listening) {
            listener.close();
            listening = false;
        }
    }

    /**
     * 
     */
    public final Flags getFlags() {
        return flags;
    }

    /**
     * 
     * @return 
     */
    public final String getHost() {
        return flags.getString(Flags.HOST);
    }

    /**
     * 
     * @return 
     */
    public final int getPort() {
        return flags.getInt(Flags.PORT);
    }

    /**
     * 
     * @return 
     */
    public final String getURL() {
        return "http://" + getHost() + ":" + getPort();
    }

    /**
     * 
     * @param path
     * @return 
     */
    public final String getURL(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return getURL() + path;
    }

    /**
     * 
     * @return 
     */
    public final RequestListeningThread getListener() {
        return listener;
    }

    /**
     * 
     * @return 
     */
    public final RequestProcessExecutor getRequestExecutor() {
        return requestExecutor;
    }

    /**
     * 
     * @return 
     */
    public final EGATSProcessExecutor getProcessExecutor() {
        return processExecutor;
    }
    
    /**
     * 
     * @return 
     */
    public final EGATSWorkflowExecutor getWorkflowExecutor() {
        return workflowExecutor;
    }

    /**
     * 
     * @return 
     */
    public final Toolkit getToolkit() {
        return toolkit;
    }

    /**
     * 
     * @return 
     */
    public final WorkFileManager getWorkFileManager() {
        return workFileManager;
    }

    /**
     * 
     * @param e 
     */
    public final void logException(Exception e) {
        e.printStackTrace();
    }

    /**
     * 
     */
    public final synchronized void start() {
        // Start listening and responding to requests if not already doing so.
        if (!listening) {
            listener.start();
            listening = true;
        }
    }

    /**
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        // Process the arguments
        Flags flags = new Flags(args);

        // If the help flag is set, then print the manual and exit
        if (flags.contains(Flags.HELP)) {
            // TODO: print the real manual
            System.out.println("Manual");
            // Print the manual
            System.exit(0);
        }

        // Create the server with the given flags
        Server s = new Server(flags);
        // Start the server
        s.start();
    }
}
