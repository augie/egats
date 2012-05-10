package egats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
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
        // Run the server
        server = new Server(new Flags(new String[]{}));
        server.start();
        // Give it a little to get going
        Thread.sleep(50);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        IOUtil.safeClose(server);
    }

    protected Server getServer() {
        return server;
    }

    public static String send(String url) throws Exception {
        URL urlObj = new URL(url);
        // Make the response
        BufferedReader br = new BufferedReader(new InputStreamReader(urlObj.openStream()));
        try {
            // Read the response
            String response = "", line;
            while ((line = br.readLine()) != null) {
                response += line;
            }
            return response;
        } finally {
            IOUtil.safeClose(br);
        }
    }

    public static String send(String url, String body) throws Exception {
        OutputStreamWriter wr = null;
        BufferedReader br = null;
        try {
            // Send data
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(body);
            wr.flush();

            // Get the response
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line, response = "";
            while ((line = br.readLine()) != null) {
                response += line;
            }
            return response;
        } finally {
            IOUtil.safeClose(wr);
            IOUtil.safeClose(br);
        }
    }
}
