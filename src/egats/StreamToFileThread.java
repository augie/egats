package egats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class StreamToFileThread extends Thread {

    private InputStream is;
    private StringBuffer buffer;

    public StreamToFileThread(InputStream is, StringBuffer buffer) {
        this.is = is;
        this.buffer = buffer;
    }

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
        } finally {
            IOUtil.safeClose(reader);
        }
    }
}
