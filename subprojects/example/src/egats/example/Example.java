package egats.example;

import egats.EGATProcess;
import egats.EGATSObject;
import egats.Response;
import egats.example.serverside.ExampleServerSide;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class Example {

    public static final String HOST = "egat.eecs.umich.edu";
    //public static final String HOST = "localhost";
    public static final String PORT = "80";

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        // Create the arguments
        int[] arg1 = new int[]{10, 10, 10, 10};
        boolean[] arg2 = new boolean[]{true, false, true, false};

        // Get the expected output
        String expectedJSON = Util.GSON.toJson(ExampleServerSide.fakeEGATProcess(arg1, arg2));

        // Turn the first arg into an object
        EGATSObject arg1Obj = new EGATSObject();
        arg1Obj.setClassPath(arg1.getClass().getName());
        arg1Obj.setObject(Util.GSON.toJson(arg1));

        // Turn the second arg into an object
        EGATSObject arg2Obj = new EGATSObject();
        arg2Obj.setClassPath(arg2.getClass().getName());
        arg2Obj.setObject(Util.GSON.toJson(arg2));

        // Send the args to the server
        String objectURL = "http://" + HOST + ":" + PORT + "/o";
        Response response = Response.fromJSON(Util.sendPostRequest(objectURL, arg1Obj.getJSON()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending first argument to server: " + response);
        }
        System.out.println("Stored on server: " + arg1Obj.getJSON());
        String arg1ID = response.getBody();
        System.out.println("Arg 1 ID: " + arg1ID);
        response = Response.fromJSON(Util.sendPostRequest(objectURL, arg2Obj.getJSON()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending second argument to server: " + response);
        }
        System.out.println("Stored on server: " + arg2Obj.getJSON());
        String arg2ID = response.getBody();
        System.out.println("Arg 2 ID: " + arg2ID);

        // Create an EGAT process to run
        EGATProcess egatProcess = new EGATProcess();
        egatProcess.setMethodPath("egats.example.serverside.ExampleServerSide.fakeEGATProcess");
        egatProcess.setArgs(new String[]{arg1ID, arg2ID});

        // Send the process request to the server
        String processURL = "http://" + HOST + ":" + PORT + "/p";
        response = Response.fromJSON(Util.sendPostRequest(processURL, egatProcess.getJSON()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending process request to server: " + response);
        }
        System.out.println("Requested process: " + egatProcess.getJSON());

        // Poll server until our process is completed
        String processID = response.getBody();
        System.out.println("Process ID: " + processID);
        String processObjURL = processURL + "/" + processID;
        do {
            Thread.sleep(100);
            response = Response.fromJSON(Util.sendRequest(processObjURL));
            if (response.getStatusCode() != Response.STATUS_CODE_OK) {
                throw new Exception("Problem checking process progress on server: " + response);
            }
            egatProcess = EGATProcess.read(response.getBody());
            if (egatProcess.getFinishTime() != null) {
                System.out.println("Process is not finished yet. Waiting 100 ms.");
            }
        } while (egatProcess.getFinishTime() == null);

        // Was there a problem?
        if (!egatProcess.getStatus().equals(EGATProcess.STATUS_COMPLETED)) {
            throw new Exception("The process failed to execute with the following error: " + egatProcess.getExceptionMessage());
        }

        // What is the ID of the output object?
        String outputID = egatProcess.getOutputID();
        System.out.println("Process completed successfully. Result ID: " + outputID);

        // Get the output object
        String outputObjURL = objectURL + "/" + outputID;
        response = Response.fromJSON(Util.sendRequest(outputObjURL));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("There was a proble getting the output from the server: " + response);
        }
        EGATSObject outputObj = EGATSObject.read(response.getBody());
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
