package egats;

/**
 *
 * @author Augie Hill - augman85@gmail.com
 */
public class IOUtilTest extends EGATSTestCase {

    public void testGetResourceAsString() throws Exception {
        assertEquals("line 1\nline 2", IOUtil.getResourceAsString("/egats/html/test.html"));
    }

    public void testGetResourceAsStringFails() throws Exception {
        try {
            IOUtil.getResourceAsString("blah dee blah blah blah");
            fail("Expected method to fail.");
        } catch (Exception e) {
            // Expected
        }
    }

    public void testSafeGetResourceAsString() throws Exception {
        assertEquals("", IOUtil.safeGetResourceAsString("blah dee blah blah blah"));
    }
}
