package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class RequestListeningThreadTest extends EGATSTestCase {

    public void testClose() throws Exception {
        Server server = new Server(new Flags());
        RequestListeningThread thread = new RequestListeningThread(server);
        try {
            thread.start();
            Thread.sleep(50);
        } finally {
            thread.close();
        }
    }

    public void testRequest() throws Exception {
        Server server = TestUtil.getServer();
        RequestListeningThread thread = new RequestListeningThread(server);
        try {
            thread.start();
            Thread.sleep(50);
            String response = TestUtil.sendRequest(server.getURL());
            assertEquals(new Response(), Response.fromJSON(response));
        } finally {
            thread.close();
        }
    }
}
