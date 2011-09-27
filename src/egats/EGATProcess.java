package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATProcess extends DataObject implements Runnable {

    public EGATProcess() throws Exception {
        Data.insert(Data.EGAT_PROCESSES, this);
    }

    public final void run() {
    }
}
