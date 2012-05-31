package egats;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Manages the execution of requests.
 * @author Augie Hill - augie@umich.edu
 */
public class RequestProcessExecutor extends ThreadPoolExecutor {

    static {
        Flags.setDefault(Flags.REQUEST_PROCESSING_THREADS, 2);
        Flags.setDefault(Flags.REQUEST_PROCESSING_QUEUE, 1000);
    }

    /**
     * 
     * @param server 
     */
    public RequestProcessExecutor(Server server) {
        super(server.getFlags().getInt(Flags.REQUEST_PROCESSING_THREADS),
                server.getFlags().getInt(Flags.REQUEST_PROCESSING_THREADS),
                100,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(server.getFlags().getInt(Flags.REQUEST_PROCESSING_QUEUE)));
    }

    /**
     * 
     * @param rp
     * @return 
     */
    public synchronized Future submit(RequestProcess rp) {
        // Do not use the blocking functionality of the blocking queue
        if (getQueue().remainingCapacity() == 0) {
            // A return of null denotes a full queue
            return null;
        }
        // Submit the runnable process
        return submit((Runnable) rp);
    }
}
