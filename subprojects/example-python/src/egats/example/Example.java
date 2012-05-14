package egats.example;

import egats.API;
import egats.EGATSProcess;
import egats.EGATSObject;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class Example {

//    public static final String HOST = "egat.eecs.umich.edu:55555";
    public static final String HOST = "localhost:55555";

    public static void main(String[] args) throws Exception {
        API.setHost(HOST);
        long startTime = System.currentTimeMillis();

        // The expected output
        String expectedJSON = "This is an example.";

        // Create an EGAT process to run
        EGATSProcess egatProcess = new EGATSProcess();
        egatProcess.setMethodPath("example.py");
        egatProcess.setArgs(new String[]{});

        // Send the process request to the server
        String processID = API.createProcess(egatProcess);

        // Poll server until our process is completed
        do {
            Thread.sleep(100);
            egatProcess = API.getProcess(processID);
            if (egatProcess.getFinishTime() == null) {
                System.out.println("Process is not finished yet. Waiting 100 ms. Total time " + (System.currentTimeMillis() - startTime) + " ms");
            }
        } while (egatProcess.getFinishTime() == null);

        // Was there a problem?
        if (!egatProcess.getStatus().equals(EGATSProcess.STATUS_COMPLETED)) {
            throw new Exception("The process failed to execute with the following error: " + egatProcess.getExceptionMessage());
        }

        // What is the ID of the output object?
        String outputID = egatProcess.getOutputID();
        System.out.println("Process completed successfully. Result ID: " + outputID);

        // Get the output object
        EGATSObject outputObj = API.getObject(outputID);
        System.out.println("Output: " + outputObj.getJSON());

        // Print the JSON of the results
        if (!expectedJSON.equals(outputObj.getObject())) {
            throw new Exception("The output was not as expected. Expected \"" + expectedJSON + "\". Received \"" + outputObj.getObject() + "\".");
        }

        // Fin
        System.out.println("Everything went better than expected.");
        System.out.println("Execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
