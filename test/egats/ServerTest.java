package egats;

import com.mongodb.util.JSON;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class ServerTest extends EGATSTestCase {

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

    public void testRunEGATProcess() throws Exception {
        // Create the arguments
        int[] arg1 = new int[]{0, 0, 0, 0};
        boolean[] arg2 = new boolean[]{true, false, true, false};
        // Expected output
        int[] expectedOutput = new int[]{1, 0, 1, 0};

        // Create the first object
        EGATSObject arg1Obj = new EGATSObject();
        arg1Obj.setClassPath(arg1.getClass().getName());
        arg1Obj.setObject(Data.GSON.toJson(arg1));
        arg1Obj = EGATSObject.create(Data.GSON.toJson(arg1Obj));

        // Create the second object
        EGATSObject arg2Obj = new EGATSObject();
        arg2Obj.setClassPath(arg2.getClass().getName());
        arg2Obj.setObject(Data.GSON.toJson(arg2));
        arg2Obj = EGATSObject.create(Data.GSON.toJson(arg2Obj));

        // Create an process request
        EGATSProcess request = new EGATSProcess();
        request.setMethodPath("egats.ServerTest.fakeEGATProcess");
        request.setArgs(new String[]{arg1Obj.getID(), arg2Obj.getID()});
        
        // Put into a list
        List<EGATSProcess> oList = new LinkedList<EGATSProcess>();
        oList.add(request);

        // Send the process request
        String responseJSON = send(server.getURL("/p"), JSON.serialize(oList));
        Response response = Response.fromJSON(responseJSON);

        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());
        
        // Read the response
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        assertEquals(1, IDs.size());
        
        // Get the process that was created
        EGATSProcess p = EGATSProcess.CACHE.get(IDs.get(0));
        assertNotNull(p);

        // Wait a little for the process to run
        while (p.getFinishTime() == null) {
            Thread.sleep(25);
        }

        // Should have not been an error
        assertNull(p.getExceptionMessage());

        // Get the output object
        EGATSObject o = EGATSObject.CACHE.get(p.getOutputID());
        assertNotNull(o);

        // Is the output from the process as expected?
        assertEquals(Data.GSON.toJson(expectedOutput), o.getObject());
    }
}
