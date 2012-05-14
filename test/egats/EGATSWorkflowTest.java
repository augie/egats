package egats;

/**
 *
 * @author Augie
 */
public class EGATSWorkflowTest extends EGATSTestCase {

    public void testBasicWorkflow() throws Exception {
        // Create the arguments
        int[] arg1 = new int[]{0, 0, 0, 0};
        boolean[] arg2 = new boolean[]{true, false, true, false};
        // Expected output
        int[] intermediateExpectedOutput = new int[]{1, 0, 1, 0};
        int[] expectedOutput = new int[]{2, 0, 2, 0};
        
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
        
        // Create the workflow
        EGATSWorkflow o = new EGATSWorkflow();
        o.setClassPath("egats.BasicWorkflow");
        o.setArgs(new String[]{arg1Obj.getID(), arg2Obj.getID()});
        String id = API.createWorkflow(o);
        o = EGATSWorkflow.CACHE.get(id);
        
        // Wait for the workflow to finish
        while (o.getFinishTime() == null) {
            Thread.sleep(25);
        }
        
        // Should have not been an error
        assertNull(o.getExceptionMessage());
        
        // Make sure 2 processes were run
        assertEquals(2, o.getProcessCount());
        
        // Check the output of each process
        // Get the first process
        EGATSProcess p1 = EGATSProcess.CACHE.get(o.getProcesses().get(0));
        
        // Should have not been an error
        assertNull(p1.getExceptionMessage());

        // Get the output object
        EGATSObject o1 = EGATSObject.CACHE.get(p1.getOutputID());
        assertNotNull(o1);

        // Is the output from the process as expected?
        assertEquals(Data.GSON.toJson(intermediateExpectedOutput), o1.getObject());
        
        // Get the second process
        EGATSProcess p2 = EGATSProcess.CACHE.get(o.getProcesses().get(1));
        
        // Should have not been an error
        assertNull(p2.getExceptionMessage());

        // Get the output object
        EGATSObject o2 = EGATSObject.CACHE.get(p2.getOutputID());
        assertNotNull(o2);

        // Is the output from the process as expected?
        assertEquals(Data.GSON.toJson(expectedOutput), o2.getObject());
    }
}
