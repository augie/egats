package egats;

import com.mongodb.util.JSON;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Augie
 */
public class RequestProcessTest extends EGATSTestCase {

    public void testCreateEGATSWorkflow() throws Exception {
        // Create an workflow to test and set all the attributes
        EGATSWorkflow o = new EGATSWorkflow();
        o.setClassPath("class path");
        o.setName("my workflow");
        o.setArgs(new String[]{"arg"});

        // Put into a list
        List<EGATSWorkflow> oList = new LinkedList<EGATSWorkflow>();
        oList.add(o);

        // Create on the server and get a response
        String responseJSON = send(server.getURL("/w"), JSON.serialize(oList));
        Response response = Response.fromJSON(responseJSON);

        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());

        // Read the response
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        assertEquals(1, IDs.size());

        // Did it make it into the cache?
        assertTrue(EGATSWorkflow.CACHE.contains(IDs.get(0)));
    }

    public void testCreateEGATSProcess() throws Exception {
        // Create a process to test and set all the attributes
        EGATSProcess o = new EGATSProcess();
        o.setMethodPath("method path");
        o.setArgs(new String[]{"id1", "id2"});

        // Put into a list
        List<EGATSProcess> oList = new LinkedList<EGATSProcess>();
        oList.add(o);

        // Create on the server and get a response
        String responseJSON = send(server.getURL("/p"), JSON.serialize(oList));
        Response response = Response.fromJSON(responseJSON);

        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());

        // Read the response
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        assertEquals(1, IDs.size());

        // Did it make it into the cache?
        assertTrue(EGATSProcess.CACHE.contains(IDs.get(0)));
    }

    public void testCreateEGATSObject() throws Exception {
        // Create an object to test and set all the attributes
        EGATSObject o = new EGATSObject();
        o.setClassPath("class path");
        o.setObject("my object");

        // Put into a list
        List<EGATSObject> oList = new LinkedList<EGATSObject>();
        oList.add(o);

        // Createon the server and get a response
        String responseJSON = send(server.getURL("/o"), JSON.serialize(oList));
        Response response = Response.fromJSON(responseJSON);

        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());

        // Read the response
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        assertEquals(1, IDs.size());

        // Did it make it into the cache?
        assertTrue(EGATSObject.CACHE.contains(IDs.get(0)));
    }

    public void testGetEGATSWorkflow() throws Exception {
        // Create a workflow to test and set all the attributes
        EGATSWorkflow o = new EGATSWorkflow();
        o.setClassPath("class path");
        o.setName("my workflow");
        o.setArgs(new String[]{"arg"});

        // Put into a list
        List<EGATSWorkflow> oList = new LinkedList<EGATSWorkflow>();
        oList.add(o);

        // Create on the server and get a response
        String responseJSON = send(server.getURL("/w"), JSON.serialize(oList));
        Response response = Response.fromJSON(responseJSON);

        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());

        // Read the response
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        assertEquals(1, IDs.size());

        // Check that you can fetch from the server
        responseJSON = send(server.getURL("/w/" + IDs.get(0)));
        response = Response.fromJSON(responseJSON);

        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());

        // Read the response
        List<Object> egatsWorkflows = (List<Object>) JSON.parse(response.getBody());
        assertEquals(1, egatsWorkflows.size());

        // Check the workflow (serializing to JSON is a lazy hack)
        EGATSWorkflow getO = EGATSWorkflow.read(JSON.serialize(egatsWorkflows.get(0)));
        assertEquals(o.getClassPath(), getO.getClassPath());
        assertEquals(o.getName(), getO.getName());
        assertEquals(o.getArgs()[0], getO.getArgs()[0]);
    }

    public void testGetEGATSProcess() throws Exception {
        // Create an object to test and set all the attributes
        EGATSProcess o = new EGATSProcess();
        o.setMethodPath("method path");
        o.setName("name");
        o.setArgs(new String[]{"arg"});
        
        // Put into a list
        List<EGATSProcess> oList = new LinkedList<EGATSProcess>();
        oList.add(o);

        // Create on the server and get a response
        String responseJSON = send(server.getURL("/p"), JSON.serialize(oList));
        Response response = Response.fromJSON(responseJSON);

        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());

        // Read the response
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        assertEquals(1, IDs.size());

        // Check that you can fetch from the server
        responseJSON = send(server.getURL("/p/" + IDs.get(0)));
        response = Response.fromJSON(responseJSON);
        
        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());
        
        // Read the response
        List<Object> egatsProcesses = (List<Object>) JSON.parse(response.getBody());
        assertEquals(1, egatsProcesses.size());
        
        // Check the process (serializing to JSON is a lazy hack)
        EGATSProcess getO = EGATSProcess.read(JSON.serialize(egatsProcesses.get(0)));
        assertEquals(o.getMethodPath(), getO.getMethodPath());
        assertEquals(o.getName(), getO.getName());
        assertEquals(o.getArgs()[0], getO.getArgs()[0]);
    }

    public void testGetEGATSObject() throws Exception {
        // Create an object to test and set all the attributes
        EGATSObject o = new EGATSObject();
        o.setClassPath("class path");
        o.setObject("my object");

        // Put into a list
        List<EGATSObject> oList = new LinkedList<EGATSObject>();
        oList.add(o);
        
        // Create the object on the server and get a response
        String responseJSON = send(server.getURL("/o"), JSON.serialize(oList));
        Response response = Response.fromJSON(responseJSON);
        
        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());

        // Read the response
        List<String> IDs = (List<String>) JSON.parse(response.getBody());
        assertEquals(1, IDs.size());

        // Check that you can fetch from the server
        responseJSON = send(server.getURL("/o/" + IDs.get(0)));
        response = Response.fromJSON(responseJSON);
        
        // Check response code
        assertEquals(Response.STATUS_CODE_OK, response.getStatusCode());
        
        // Read the response
        List<Object> egatsObjects = (List<Object>) JSON.parse(response.getBody());
        assertEquals(1, egatsObjects.size());
        
        // Check the object (serializing to JSON is a lazy hack)
        EGATSObject getO = EGATSObject.read(JSON.serialize(egatsObjects.get(0)));
        assertEquals(o.getClassPath(), getO.getClassPath());
        assertEquals(o.getObject(), getO.getObject());
    }
}
