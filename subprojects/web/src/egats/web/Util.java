package egats.web;

import java.util.Date;

/**
 *
 * @author Augie Hill - augie@umich.edu
 */
public class Util {

    public static final String HOST = "egat.eecs.umich.edu:55555";
//    public static final String HOST = "localhost:55555";

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
