package egats;

/**
 *
 * @author Augie
 */
public class RequestProcessorTest extends EGATSTestCase {

    public void testCreateEGATProcess() throws Exception {
        Server server = null;
        try {
            server = TestUtil.startServer();

            // Create an object to test and set all the attributes
            EGATProcess o = new EGATProcess();
            o.setMethodPath("method path");
            o.setArgs(new String[]{"id1", "id2"});

            // Create the object on the server and get a response
            String responseJSON = TestUtil.sendPostRequest(server.getURL("/p"), o.getJSON());
            Response response = Response.fromJSON(responseJSON);

            // For testing need to add the ID of the object to the original object to verify correctness
            String id = response.getBody();
            o.put(DataObject.ATTR_ID, id);

            // Did it make it into the cache?
            assertEquals(o, EGATProcess.CACHE.get(id));
        } finally {
            IOUtil.safeClose(server);
        }
    }

    public void testCreateEGATSObject() throws Exception {
        Server server = null;
        try {
            server = TestUtil.startServer();

            // Create an object to test and set all the attributes
            EGATSObject o = new EGATSObject();
            o.setClassPath("class path");
            o.setObject("my object");

            // Create the object on the server and get a response
            String responseJSON = TestUtil.sendPostRequest(server.getURL("/o"), o.getJSON());
            Response response = Response.fromJSON(responseJSON);

            // For testing need to add the ID of the object to the original object to verify correctness
            String id = response.getBody();
            o.put(DataObject.ATTR_ID, id);

            // Did it make it into the cache?
            assertEquals(o, EGATSObject.CACHE.get(id));
        } finally {
            IOUtil.safeClose(server);
        }
    }

    public void testGetEGATProcess() throws Exception {
        Server server = null;
        try {
            server = TestUtil.startServer();

            // Create an object to test and set all the attributes
            EGATProcess o = new EGATProcess();

            // Create the object on the server and get a response
            String responseJSON = TestUtil.sendPostRequest(server.getURL("/p"), o.getJSON());
            Response response = Response.fromJSON(responseJSON);

            // For testing need to add the ID of the object to the original object to verify correctness
            String id = response.getBody();
            o.put(DataObject.ATTR_ID, id);

            // Check that you can fetch the object from the server
            responseJSON = TestUtil.sendRequest(server.getURL("/p/" + id));
            response = Response.fromJSON(responseJSON);
            assertEquals(o.getJSON(), response.getBody());
        } finally {
            IOUtil.safeClose(server);
        }
    }

    public void testGetEGATSObject() throws Exception {
        Server server = null;
        try {
            server = TestUtil.startServer();

            // Create an object to test and set all the attributes
            EGATSObject o = new EGATSObject();
            o.setClassPath("class path");
            o.setObject("my object");

            // Create the object on the server and get a response
            String responseJSON = TestUtil.sendPostRequest(server.getURL("/o"), o.getJSON());
            Response response = Response.fromJSON(responseJSON);

            // For testing need to add the ID of the object to the original object to verify correctness
            String id = response.getBody();
            o.put(DataObject.ATTR_ID, id);

            // Check that you can fetch the object from the server
            responseJSON = TestUtil.sendRequest(server.getURL("/o/" + id));
            response = Response.fromJSON(responseJSON);
            assertEquals(o.getJSON(), response.getBody());
        } finally {
            IOUtil.safeClose(server);
        }
    }
}
