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
    
    /**
     * 
     * @param string
     * @return 
     */
    public static String getString(String string) {
        if (string == null) {
            return "null";
        }
        return string;
    }
}
