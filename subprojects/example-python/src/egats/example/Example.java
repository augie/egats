package egats.example;

import egats.EGATProcess;
import egats.EGATSObject;
import egats.Response;

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

        // The expected output
        String expectedJSON = "This is an example.";

        // Create an EGAT process to run
        EGATProcess egatProcess = new EGATProcess();
        egatProcess.setMethodPath("example.py");
        egatProcess.setArgs(new String[]{});

        // Send the process request to the server
        String processURL = "http://" + HOST + ":" + PORT + "/p";
        Response response = Response.fromJSON(Util.sendPostRequest(processURL, egatProcess.getJSON()));
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
            if (egatProcess.getFinishTime() == null) {
                System.out.println("Process is not finished yet. Waiting 100 ms. Total time " + (System.currentTimeMillis() - startTime) + " ms");
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
        String objectURL = "http://" + HOST + ":" + PORT + "/o";
        String outputObjURL = objectURL + "/" + outputID;
        response = Response.fromJSON(Util.sendRequest(outputObjURL));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("There was a proble getting the output from the server: " + response);
        }
        EGATSObject outputObj = EGATSObject.read(response.getBody());
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
