package egats;

import junit.framework.TestCase;

/**
 *
 * @author Augie
 */
public class EGATSTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Flags.TESTING = true;
    }
}
