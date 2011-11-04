package egats;

import junit.framework.TestCase;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class EGATSTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Flags.TESTING = true;
    }
}
