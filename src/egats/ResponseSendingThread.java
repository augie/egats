package egats;

/**
 *
 * @author Augie
 */
public class ResponseSendingThread extends Thread {

    private Server server = null;

    public ResponseSendingThread(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
