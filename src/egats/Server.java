package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class Server {

    private final RequestListeningThread listener;
    private final RequestProcessorExecutor executor;
    private final EGATProcessExecutor egatExecutor;
    private final EGATClassLoader egatClassLoader;
    private final Flags flags;

    static {
        Flags.setDefault(Flags.HOST, "localhost");
    }

    public Server(Flags flags) {
        this.flags = flags;
        listener = new RequestListeningThread(this);
        executor = new RequestProcessorExecutor(this);
        egatExecutor = new EGATProcessExecutor(this);
        egatClassLoader = new EGATClassLoader(this);
    }

    public final void close() {
        listener.close();
    }

    public final Flags getFlags() {
        return flags;
    }

    public final String getHost() {
        return flags.getString(Flags.HOST);
    }

    public final int getPort() {
        return flags.getInt(Flags.PORT);
    }

    public final String getURL() {
        return "http://" + getHost() + ":" + getPort();
    }

    public final String getURL(String path) {
        return getURL() + path;
    }

    public final RequestListeningThread getListener() {
        return listener;
    }

    public final RequestProcessorExecutor getExecutor() {
        return executor;
    }

    public final EGATProcessExecutor getEGATExecutor() {
        return egatExecutor;
    }

    public final EGATClassLoader getClassLoader() {
        return egatClassLoader;
    }

    public final void logException(Exception e) {
        if (!Flags.TESTING) {
            e.printStackTrace();
        }
    }

    public final void start() {
        // Start listening and responding to requests.
        listener.start();
    }

    public static final void main(String[] args) throws Exception {
        // Process the arguments
        Flags flags = new Flags(args);

        // If the help flag is set, then print the manual and exit
        if (flags.contains(Flags.HELP)) {
            // TODO: print the real manual
            System.out.println("Manual");
            // Print the manual
            System.exit(0);
        }

        // The library directory must be set.
        if (!flags.contains(Flags.LIB)) {
            throw new Exception("Library directory flag not set.");
        }

        // Start the server using the given flags
        Server s = new Server(flags);
        // Load the library
        s.getClassLoader().load();
        // Start the server
        s.start();
    }
}
