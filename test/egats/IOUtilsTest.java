package egats;

import java.net.URL;

/**
 *
 * @author Augie
 */
public class IOUtilsTest extends EGATSTestCase {

    public void testGetGoogle() throws Exception {
        System.out.println(IOUtils.toString(new URL("http://google.com")));
    }
}
