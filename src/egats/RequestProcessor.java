package egats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Augie
 */
public class RequestProcessor implements Runnable {

    private final RequestListeningThread listener;
    private final Socket socket;

    public RequestProcessor(RequestListeningThread listener, Socket socket) {
        this.listener = listener;
        this.socket = socket;
    }

    public final void run() {
        try {
            // TODO: Fix this crap
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            String request;
            while (!(request = br.readLine()).equals(""))  {
                System.out.println(request);
            }
            os.write("10-4\n".getBytes());
            os.flush();
            os.close();
        } catch (Exception e) {
            // Log
            listener.getServer().logException(e);
        } finally {
            try {
                this.socket.close();
            } catch (Exception e) {
                // Log
                listener.getServer().logException(e);
            }
        }
    }
}
