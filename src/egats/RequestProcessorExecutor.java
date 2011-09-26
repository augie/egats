package egats;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Augie
 */
public class RequestProcessorExecutor extends ThreadPoolExecutor {

    public static final int THREAD_COUNT = 2;
    public static final int QUEUE_SIZE = 100;
    private final Server server;

    public RequestProcessorExecutor(Server server) {
        this(server, THREAD_COUNT, QUEUE_SIZE);
    }

    public RequestProcessorExecutor(Server server, int threadCount, int queueSize) {
        super(threadCount, threadCount, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueSize));
        this.server = server;
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
