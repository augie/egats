package egats.web;

import java.util.Date;

/**
 *
 * @author Augie
 */
public class WebUtil {

    /**
     * 
     * @param timestamp
     * @return 
     */
    public static String getDate(Long timestamp) {
        if (timestamp == null) {
            return "null";
        }
        return new Date(timestamp).toString();
    }
}
