package egats;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class RequestProcessorExecutor extends ThreadPoolExecutor {

    public static final int DEFAULT_THREAD_COUNT = 2;
    public static final int DEFAULT_QUEUE_SIZE = 100;
    private final Server server;

    static {
        Flags.setDefault(Flags.REQUEST_PROCESSING_THREADS, DEFAULT_THREAD_COUNT);
        Flags.setDefault(Flags.REQUEST_PROCESSING_QUEUE, DEFAULT_QUEUE_SIZE);
    }

    public RequestProcessorExecutor(Server server) {
        super(server.getFlags().getInt(Flags.REQUEST_PROCESSING_THREADS),
                server.getFlags().getInt(Flags.REQUEST_PROCESSING_THREADS),
                100,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(server.getFlags().getInt(Flags.REQUEST_PROCESSING_QUEUE)));
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public synchronized Future submit(RequestProcessor rp) {
        // Do not use the blocking functionality of the blocking queue
        if (getQueue().remainingCapacity() == 0) {
            // A return of null denotes a full queue
            return null;
        }
        // Submit the runnable process
        return submit((Runnable) rp);
    }
}
