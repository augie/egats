package egats;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class EGATSProcessExecutor extends ThreadPoolExecutor {

    static {
        Flags.setDefault(Flags.EGAT_PROCESSING_THREADS, 2);
        Flags.setDefault(Flags.EGAT_PROCESSING_QUEUE, 100);
    }

    /**
     * 
     * @param server 
     */
    public EGATSProcessExecutor(Server server) {
        super(server.getFlags().getInt(Flags.EGAT_PROCESSING_THREADS),
                server.getFlags().getInt(Flags.EGAT_PROCESSING_THREADS),
                100,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(server.getFlags().getInt(Flags.EGAT_PROCESSING_QUEUE)));
    }

    /**
     * 
     * @param ep
     * @return 
     */
    public synchronized Future submit(EGATSProcess ep) {
        // Do not use the blocking functionality of the blocking queue
        if (getQueue().remainingCapacity() == 0) {
            // A return of null denotes a full queue
            return null;
        }
        // Submit the runnable process
        return submit((Runnable) ep);
    }
}
