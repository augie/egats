package egats;

/** 
 * Wrapper for responses to all requests.
 * @author Augie Hill - augie@umich.edu
 */
public class Response extends Object {

    public static final String STATUS_OK = "OK";
    public static final int STATUS_CODE_OK = 200;
    public static final String STATUS_ERROR = "Error";
    public static final int STATUS_CODE_ERROR = 300;
    public static final String STATUS_NOT_FOUND = "Not Found";
    public static final int STATUS_CODE_NOT_FOUND = 404;
    private String status, message, body;
    private int statusCode;

    /**
     * 
     */
    public Response() {
        this(STATUS_CODE_OK, null, null);
    }

    /**
     * 
     * @param statusCode
     * @param message 
     */
    public Response(int statusCode, String message) {
        this(statusCode, message, null);
    }

    /**
     * 
     * @param statusCode
     * @param message
     * @param body 
     */
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

    /**
     * 
     * @return 
     */
    public final String getBody() {
        return body;
    }

    /**
     * 
     * @return 
     */
    public final String getMessage() {
        return message;
    }

    /**
     * 
     * @return 
     */
    public final int getStatusCode() {
        return statusCode;
    }

    /**
     * 
     * @return 
     */
    public final String getStatus() {
        return status;
    }

    /**
     * 
     * @return 
     */
    public final byte[] getBytes() {
        return toString().getBytes();
    }

    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Response)) {
            return false;
        }
        return toString().equals(((Response) o).toString());
    }

    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return Data.GSON.toJson(this);
    }

    /**
     * 
     * @param json
     * @return 
     */
    public static Response fromJSON(String json) {
        return Data.GSON.fromJson(json, Response.class);
    }
}
