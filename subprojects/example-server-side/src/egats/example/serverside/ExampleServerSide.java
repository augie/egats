package egats.example.serverside;

/**
 * This is the server-side code to be run in the EGATS Example subproject.
 * It's entirely self-contained by design to demonstrate the ability of EGATS to
 * plug in independent libraries for execution.
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class ExampleServerSide {

    /**
     * A fake EGAT process that simply increments a value in arg1 if its
     * corresponding value in arg2 is true.
     * @param arg1
     * @param arg2
     * @return
     * @throws Exception If the two args are not of equal length.
     */
    public static int[] fakeEGATProcess(int[] arg1, boolean[] arg2) throws Exception {
        // Check inputs for equal size
        if (arg1.length != arg2.length) {
            throw new Exception("Arguments 1 and 2 are not of equal size.");
        }
        // Instantiate an array to be returned
        int[] returnArr = new int[arg1.length];
        // Build the array to be returned
        for (int i = 0; i < arg1.length; i++) {
            returnArr[i] = arg1[i];
            // If the boolean arg is set, increment the returned value at this index
            if (arg2[i]) {
                returnArr[i]++;
            }
        }
        return returnArr;
    }
}
