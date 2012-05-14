package egats;

import junit.framework.TestCase;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class EGATSTestCase extends TestCase {

    protected Server server;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Flags.TESTING = true;
        API.setHost(Server.DEFAULT_HOST + ":" + RequestListeningThread.DEFAULT_PORT);
        // Run the server
        server = new Server(new Flags(new String[]{}));
        server.start();
        // Give it a little to get going
        Thread.sleep(50);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        IOUtils.closeQuietly(server);
    }

    protected Server getServer() {
        return server;
    }
}
