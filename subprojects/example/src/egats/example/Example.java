package egats.example;

import egats.API;
import egats.Data;
import egats.EGATSProcess;
import egats.EGATSObject;
import egats.example.serverside.ExampleServerSide;
import java.util.LinkedList;
import java.util.List;

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

        // Create the arguments
        int[] arg1 = new int[]{10, 10, 10, 10};
        boolean[] arg2 = new boolean[]{true, false, true, false};

        // Get the expected output
        String expectedJSON = Data.GSON.toJson(ExampleServerSide.fakeEGATProcess(arg1, arg2));

        // Turn the first arg into an object
        EGATSObject arg1Obj = new EGATSObject();
        arg1Obj.setClassPath(arg1.getClass().getName());
        arg1Obj.setObject(Data.GSON.toJson(arg1));

        // Turn the second arg into an object
        EGATSObject arg2Obj = new EGATSObject();
        arg2Obj.setClassPath(arg2.getClass().getName());
        arg2Obj.setObject(Data.GSON.toJson(arg2));

        // Put objects in a list
        List<EGATSObject> argList = new LinkedList<EGATSObject>();
        argList.add(arg1Obj);
        argList.add(arg2Obj);

        // Send the args to the server
        List<String> argIDs = API.createObjects(argList);
        System.out.println("Stored on server: " + arg1Obj.getJSON());
        System.out.println("Arg 1 ID: " + argIDs.get(0));
        System.out.println("Stored on server: " + arg2Obj.getJSON());
        System.out.println("Arg 2 ID: " + argIDs.get(1));

        // Create an EGAT process to run
        EGATSProcess egatProcess = new EGATSProcess();
        egatProcess.setMethodPath("egats.example.serverside.ExampleServerSide.fakeEGATProcess");
        egatProcess.setArgs(new String[]{argIDs.get(0), argIDs.get(1)});

        // Send the process request to the server
        String processID = API.createProcess(egatProcess);
        System.out.println("Process ID: " + processID);

        // Poll server until our process is completed
        do {
            Thread.sleep(100);

            // Get process
            egatProcess = API.getProcess(processID);

            // Is it finished?
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
            throw new Exception("The output was not as expected: " + outputObj.getObject());
        }

        // Fin
        System.out.println("Everything went better than expected.");
        System.out.println("Execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
