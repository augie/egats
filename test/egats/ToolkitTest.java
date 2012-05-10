package egats;

/**
 *
 * @author Augie
 */
public class ToolkitTest extends EGATSTestCase {

    public void testLoad() throws Exception {
        Toolkit toolkit = server.getToolkit();
        assertTrue(toolkit.isTool("egats.EGATSProcessTest.process"));
        assertFalse(toolkit.isTool("something else"));
    }
}
