package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class Response extends Object {

    public static final String STATUS_OK = "OK";
    public static final int STATUS_CODE_OK = 200;
    public static final String STATUS_ERROR = "Error";
    public static final int STATUS_CODE_ERROR = 300;
    public static final String STATUS_NOT_FOUND = "Not Found";
    public static final int STATUS_CODE_NOT_FOUND = 404;
    private final String status, message, body;
    private final int statusCode;

    public Response() {
        this(STATUS_CODE_OK, null, null);
    }

    public Response(int statusCode, String message) {
        this(statusCode, message, null);
    }

    public Response(int statusCode, String message, String body) {
        this.statusCode = statusCode;
        if (statusCode == STATUS_CODE_OK) {
            this.status = STATUS_OK;
        } else if (statusCode == STATUS_CODE_NOT_FOUND) {
            this.status = STATUS_NOT_FOUND;
        } else if (statusCode == STATUS_CODE_ERROR) {
            this.status = STATUS_ERROR;
        } else {
            throw new RuntimeException("Unrecognized status code: " + statusCode);
        }
        this.message = message;
        this.body = body;
    }

    public final int getStatusCode() {
        return statusCode;
    }

    public final String getStatus() {
        return status;
    }

    public final byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        return Data.GSON.toJson(this);
    }
}
