package egats;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A separate executor for workflows is necessary so that a livelock does not
 *  occur.
 * @author Augie Hill - augie@umich.edu
 */
public class EGATSWorkflowExecutor extends ThreadPoolExecutor {

    static {
        Flags.setDefault(Flags.WORKFLOW_PROCESSING_THREADS, 1);
        Flags.setDefault(Flags.WORKFLOW_PROCESSING_QUEUE, 100);
    }

    /**
     * 
     * @param server 
     */
    public EGATSWorkflowExecutor(Server server) {
        super(server.getFlags().getInt(Flags.WORKFLOW_PROCESSING_THREADS),
                server.getFlags().getInt(Flags.WORKFLOW_PROCESSING_QUEUE),
                100,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(server.getFlags().getInt(Flags.WORKFLOW_PROCESSING_QUEUE)));
    }

    /**
     * 
     * @param ew
     * @return 
     */
    public synchronized Future submit(EGATSWorkflow ew) {
        // Do not use the blocking functionality of the blocking queue
        if (getQueue().remainingCapacity() == 0) {
            // A return of null denotes a full queue
            return null;
        }
        // Submit the runnable process
        return submit((Runnable) ew);
    }
}
