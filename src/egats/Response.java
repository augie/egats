package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class Response extends Object {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_ERROR = "Error";
    public static final String STATUS_NOT_FOUND = "Not Found";
    private final String status, message, body;

    public Response() {
        this(STATUS_OK, null, null);
    }

    public Response(String status, String message) {
        this(status, message, null);
    }

    public Response(String status, String message, String body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

    public byte[] getBytes() {
        return Data.GSON.toJson(this).getBytes();
    }
}
