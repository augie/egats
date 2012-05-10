package egats.example;

import com.google.gson.Gson;
import egats.IOUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Augie
 */
public class Util {

    public static final Gson GSON = new Gson();

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
