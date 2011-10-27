package egats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import junit.framework.TestCase;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class RequestListeningThreadTest extends TestCase {

    public void testRequest() throws Exception {
        Server server = TestUtil.getServer();
        RequestListeningThread thread = new RequestListeningThread(server);
        try {
            thread.start();
            Thread.sleep(200);

            // Query the server
            int port = server.getFlags().getInt(Flags.PORT);
            URL url = new URL("http://localhost:" + port);

            // Check the response
            InputStream is = url.openStream();
            InputStreamReader r = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(r);
            try {
                // Is the response null?
                assertNotNull(is);
                // Read the response
                String response = "", line;
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                // Check the response
                assertEquals(new Response(), Data.GSON.fromJson(response, Response.class));
            } finally {
                // Close the response readers
                IOUtil.safeClose(br);
                IOUtil.safeClose(r);
                IOUtil.safeClose(is);
            }
        } finally {
            thread.close();
        }
    }

    public void testClose() throws Exception {
        Server server = new Server(new Flags());
        RequestListeningThread thread = new RequestListeningThread(server);
        try {
            thread.start();
            Thread.sleep(200);
        } finally {
            thread.close();
        }
    }
}
