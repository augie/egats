package egats;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATProcessExecutor extends ThreadPoolExecutor {

    public static final int THREAD_COUNT = 2;
    public static final int QUEUE_SIZE = 100;
    private final Server server;

    public EGATProcessExecutor(Server server) {
        this(server, THREAD_COUNT, QUEUE_SIZE);
    }

    public EGATProcessExecutor(Server server, int threadCount, int queueSize) {
        super(threadCount, threadCount, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueSize));
        this.server = server;
    }

    public final Server getServer() {
        return server;
    }

    public synchronized Future submit(EGATProcess ep) {
        // Do not use the blocking functionality of the blocking queue
        if (getQueue().remainingCapacity() == 0) {
            // A return of null denotes a full queue
            return null;
        }
        // Submit the runnable process
        return submit((Runnable) ep);
    }
}
