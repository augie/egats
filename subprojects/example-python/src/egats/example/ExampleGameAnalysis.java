package egats.example;

import egats.EGATProcess;
import egats.EGATSObject;
import egats.EGATSObjectFile;
import egats.IOUtil;
import egats.Response;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class ExampleGameAnalysis {

    public static final String HOST = "egat.eecs.umich.edu";
    //public static final String HOST = "localhost";
    public static final String PORT = "80";

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        // Turn the input into an object
        String input = IOUtil.getResourceAsString("/egats/example/example.in");
        EGATSObjectFile egatsObjectFile = new EGATSObjectFile("example.json", input);
        EGATSObject arg1Obj = new EGATSObject();
        arg1Obj.setClassPath(EGATSObjectFile.class.getName());
        arg1Obj.setObject(Util.GSON.toJson(egatsObjectFile));

        // Send the args to the server
        String objectURL = "http://" + HOST + ":" + PORT + "/o";
        Response response = Response.fromJSON(Util.sendPostRequest(objectURL, arg1Obj.getJSON()));
        if (response.getStatusCode() != Response.STATUS_CODE_OK) {
            throw new Exception("Problem sending first argument to server: " + response);
        }
        System.out.println("Stored on server: " + arg1Obj.getJSON());
        String arg1ID = response.getBody();
        System.out.println("Arg 1 ID: " + arg1ID);

        // Create an EGAT process to run
        EGATProcess egatProcess = new EGATProcess();
        egatProcess.setMethodPath("GameAnalysis/Analysis.py");
        egatProcess.setArgs(new String[]{"-r", "1e-4", "-d", "1e-3", "egats-obj-file:" + arg1ID});

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
            Thread.sleep(500);
            response = Response.fromJSON(Util.sendRequest(processObjURL));
            if (response.getStatusCode() != Response.STATUS_CODE_OK) {
                throw new Exception("Problem checking process progress on server: " + response);
            }
            egatProcess = EGATProcess.read(response.getBody());
            if (egatProcess.getFinishTime() == null) {
                System.out.println("Process is not finished yet. Waiting 500 ms. Total time " + (System.currentTimeMillis() - startTime) + " ms");
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
        System.out.println("Output: ");
        System.out.println(outputObj.getObject());

        // Fin
        System.out.println("Everything went better than expected.");
        System.out.println("Execution time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
