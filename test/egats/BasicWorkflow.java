package egats;

/**
 *
 * @author Augie
 */
public class BasicWorkflow extends AbstractWorkflow {

    public int[] fakeEGATProcess(int[] arg1, boolean[] arg2) throws Exception {
        // Check inputs
        if (arg1.length != arg2.length) {
            throw new Exception("Arguments 1 and 2 are not of equal size.");
        }
        int[] returnArr = new int[arg1.length];
        for (int i = 0; i < arg1.length; i++) {
            returnArr[i] = arg1[i];
            if (arg2[i]) {
                returnArr[i]++;
            }
        }
        return returnArr;
    }

    /**
     * Run the fake EGAT process twice, using the output from the first
     *  run as an input for the second run.
     * @throws Exception 
     */
    @Override
    public void runWorkflow() throws Exception {
        // Submit the first process
        EGATSProcess p1 = submitProcess("egats.BasicWorkflow.fakeEGATProcess", new String[]{getArgs().get(0), getArgs().get(1)});
        // Wait for the first process to finish
        waitForProcessToFinish(p1);
        // The output of the first run is used as the input to the next run
        EGATSProcess p2 = submitProcess("egats.BasicWorkflow.fakeEGATProcess", new String[]{p1.getOutputID(), getArgs().get(1)});
        // Wait for the second process to finish
        waitForProcessToFinish(p2);
    }
}
