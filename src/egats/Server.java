package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class Server {

    private final RequestListeningThread listener;
    private final RequestProcessorExecutor executor;
    private final EGATProcessExecutor egatExecutor;
    private final Flags flags;
    private boolean testing = false;

    public Server(Flags flags) {
        this.flags = flags;
        listener = new RequestListeningThread(this);
        executor = new RequestProcessorExecutor(this);
        egatExecutor = new EGATProcessExecutor(this);
    }

    public final void close() {
        listener.close();
    }

    public final Flags getFlags() {
        return flags;
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

    public final void setIsTesting(boolean testing) {
        this.testing = testing;
    }

    public final void logException(Exception e) {
        if (!testing) {
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

        // Start the server using the given flags
        Server s = new Server(flags);
        s.start();
    }
}
