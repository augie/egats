package egats.example;

import egats.API;
import egats.Data;
import egats.EGATSProcess;
import egats.EGATSObject;
import egats.EGATSObjectFile;
import egats.IOUtils;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class ExampleGameAnalysis {

//    public static final String HOST = "egat.eecs.umich.edu:55555";
    public static final String HOST = "localhost:55555";

    public static void main(String[] args) throws Exception {
        API.setHost(HOST);
        long startTime = System.currentTimeMillis();

        // Turn the input into an object
        String input = IOUtils.toString("/egats/example/example.in");
        EGATSObjectFile egatsObjectFile = new EGATSObjectFile("example.json", input);
        EGATSObject arg1Obj = new EGATSObject();
        arg1Obj.setClassPath(EGATSObjectFile.class.getName());
        arg1Obj.setObject(Data.GSON.toJson(egatsObjectFile));

        // Send the args to the server
        String arg1ID = API.createObject(arg1Obj);
        System.out.println("Arg 1 ID: " + arg1ID);

        // Create an EGAT process to run
        EGATSProcess egatProcess = new EGATSProcess();
        egatProcess.setMethodPath("GameAnalysis/Analysis.py");
        egatProcess.setArgs(new String[]{"-r", "1e-4", "-d", "1e-3", "egats-obj-file:" + arg1ID});

        // Send the process request to the server
        String processID = API.createProcess(egatProcess);

        // Poll server until our process is completed
        do {
            Thread.sleep(500);
            egatProcess = API.getProcess(processID);
            if (egatProcess.getFinishTime() == null) {
                System.out.println("Process is not finished yet. Waiting 500 ms. Total time " + (System.currentTimeMillis() - startTime) + " ms");
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
        System.out.println("Output: ");
        System.out.println(outputObj.getObject());

        // Fin
        System.out.println("Everything went better than expected.");
        System.out.println("Execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
