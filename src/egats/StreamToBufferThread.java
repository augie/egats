package egats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class StreamToBufferThread extends Thread {

    private InputStream is;
    private StringBuffer buffer;

    /**
     * 
     * @param is
     * @param buffer 
     */
    public StreamToBufferThread(InputStream is, StringBuffer buffer) {
        this.is = is;
        this.buffer = buffer;
    }

    /**
     * 
     */
    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            boolean first = true;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!first) {
                    buffer.append("\n");
                } else {
                    first = false;
                }
                buffer.append(line);
            }
        } catch (Exception e) {
            // Log the exception
            // TODO
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
