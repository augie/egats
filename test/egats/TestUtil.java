package egats;

/**
 *
 * @author Augie
 */
public class TestUtil {

    public static final Server getServer() {
        Server server = new Server(new Flags(new String[0]));
        server.setIsTesting(true);
        return server;
    }
}
