package egats;

/**
 *
 * @author Augie
 */
public class EGATSProcessTest extends EGATSTestCase {

    public static boolean process() {
        return false;
    }

    public static void voidProcess() {
    }

    public void testRun() throws Exception {
        EGATSProcess o = new EGATSProcess();
        o.setServer(getServer());
        o.setMethodPath("egats.EGATSProcessTest.process");
        o.run();
        assertNull(o.getExceptionMessage());
        EGATSObject output = EGATSObject.CACHE.get(o.getOutputID());
        assertEquals(Data.GSON.toJson(Boolean.FALSE), output.getObject());
    }

    public void testVoidMethod() throws Exception {
        EGATSProcess o = new EGATSProcess();
        o.setServer(getServer());
        o.setMethodPath("egats.EGATSProcessTest.voidProcess");
        o.run();
        assertNull(o.getExceptionMessage());
        EGATSObject output = EGATSObject.CACHE.get(o.getOutputID());
        assertEquals("", output.getObject());
        assertEquals("void", output.getClassPath());
    }
}
